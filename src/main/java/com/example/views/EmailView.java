package com.example.views;

import com.example.examplefeature.Email;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;

@Route("email")
public class EmailView extends VerticalLayout {
    private final Email emailService = new Email();

    public EmailView() {

        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);


        TextField destinatario = new TextField("Destinatário");
        destinatario.setWidth("400px");

        TextField assunto = new TextField("Assunto");

        assunto.setWidth("400px");

        TextArea corpo = new TextArea("Escreve aqui o conteúdo do email...");
        corpo.setWidth("400px");
        corpo.setHeight("200px");


        Button enviar = new Button("Enviar", e -> {
            String dest = destinatario.getValue();
            String subj = assunto.getValue();
            String body = corpo.getValue();

            if (dest.isEmpty() || subj.isEmpty() || body.isEmpty()) {
                Notification.show(" Preenche todos os campos!");
            } else {
                emailService.enviar(dest, subj, body);
                Notification.show(" Email enviado!");
                destinatario.clear();
                assunto.clear();
                corpo.clear();
            }
        });


        add(destinatario, assunto, corpo, enviar);
    }

}
