package com.example.examplefeature.ui;

import com.example.qrcode.QRCodeGenerator;
import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.flow.server.StreamResource;

@PageTitle("Gerar QR Code")
@Route(value = "qrcode", layout = com.example.base.ui.MainLayout.class)
public class QRCodeView extends VerticalLayout {


        TextField textField = new TextField("Texto para o QR Code");
        Button generateButton = new Button("Gerar QR Code");
        Image qrImage = new Image();

        generateButton.addClickListener(e -> {
            try {
                byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(textField.getValue(), 250, 250);
                StreamResource resource = new StreamResource("qrcode.png",
                        () -> new ByteArrayInputStream(qrBytes));
                qrImage.setSrc(resource);
            } catch (WriterException | IOException ex) {
                ex.printStackTrace();
            }
        });

        add(textField, generateButton, qrImage);
    }
}
