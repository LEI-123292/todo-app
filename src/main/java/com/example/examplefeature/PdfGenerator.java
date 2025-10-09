package com.example.examplefeature;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static byte[] createTaskListPdf(List<Task> tasks) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Configurações básicas
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("LISTA DE TAREFAS (TO-DO)");
                contentStream.endText();

                float y = 720;
                final float margin = 50;
                final float leading = 20;

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

                for (Task task : tasks) {
                    // Verifica se o texto excede o limite da página
                    if (y < margin) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 750);
                        y = 750;
                    }

                    // Formato da linha: [ ] Título da Tarefa (Status)
                    String status = task.isDone() ? "[CONCLUÍDA]" : "[PENDENTE]";
                    String text = status + " - " + task.getDescription();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, y);
                    contentStream.showText(text);
                    contentStream.endText();

                    y -= leading; // Move para a próxima linha
                }
            } // contentStream fecha aqui

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();

        } catch (IOException e) {
            // Logar ou relançar um erro para a UI
            throw new IOException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

}
