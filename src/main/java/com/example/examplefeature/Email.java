package com.example.examplefeature;

public class Email {

    public void enviar(String dest, String assunto, String corpo) {
        System.out.println("---- Envio de Email ----");
        System.out.println("Para: " + dest);
        System.out.println("Assunto: " + assunto);
        System.out.println("Mensagem: " + corpo);
        System.out.println("-------------------------------------");
    }
}

