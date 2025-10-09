package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.StreamResource;
import com.example.examplefeature.PdfGenerator;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        var taskActions = ViewToolbar.group(description, dueDate, createBtn);
        var actionsWithPdf = ViewToolbar.group(taskActions, createExportLink());

        add(new ViewToolbar("Task List", actionsWithPdf));
        add(taskGrid);
    }

    private void createTask() {
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private Anchor createExportLink() {

        // 1. Cria o recurso de Stream: Define o nome do ficheiro e como obter os bytes
        StreamResource sr = new StreamResource("todo_list.pdf", () -> {
            try {
                // Obtém os dados e chama o seu gerador de PDF
                // Nota: toSpringPageRequest é necessário para extrair o Stream<Task> corretamente
                var allTasks = taskService.findAllTasks();

                byte[] pdfBytes = PdfGenerator.createTaskListPdf(allTasks);

                return new ByteArrayInputStream(pdfBytes);
            } catch (IOException e) {
                // Notificação de erro na UI
                Notification.show("Erro ao gerar PDF: " + e.getMessage(), 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                e.printStackTrace();
                return new ByteArrayInputStream(new byte[0]);
            }
        });

        // 2. Cria o Anchor (o elemento que permite o download)
        Anchor downloadLink = new Anchor(sr, "");
        downloadLink.getElement().setAttribute("download", true);

        // 3. Coloca um botão dentro do Anchor
        downloadLink.add(new Button("Exportar PDF"));

        return downloadLink;
    }

}
