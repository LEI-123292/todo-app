package com.example.currency;

import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "com/example/currency", layout = MainLayout.class)
@PageTitle("Currency")
public class CurrencyView extends VerticalLayout {

    private final CurrencyExchange currencyExchange;

    @Autowired
    public CurrencyView(CurrencyExchange currencyExchange) {
        this.currencyExchange = currencyExchange;

        NumberField amount = new NumberField("Amount");
        amount.setValue(1.0);
        amount.setMin(0);

        Select<String> from = new Select<>();
        Select<String> to = new Select<>();
        // Preenche com algumas moedas comuns; podes expandir
        from.setItems("EUR", "USD", "GBP", "BRL");
        from.setValue("EUR");
        to.setItems("EUR", "USD", "GBP", "BRL");
        to.setValue("USD");

        Button convert = new Button("Convert");
        Paragraph result = new Paragraph();

        convert.addClickListener(e -> {
            try {
                double value = currencyExchange.convert(amount.getValue(), from.getValue(), to.getValue());
                result.setText(String.format("%.4f %s = %.4f %s", amount.getValue(), from.getValue(), value, to.getValue()));
            } catch (Exception ex) {
                result.setText("Erro: " + ex.getMessage());
            }
        });

        add(amount, from, to, convert, result);
    }
}