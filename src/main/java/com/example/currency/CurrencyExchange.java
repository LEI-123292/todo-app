package com.example.currency;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CurrencyExchange {
    private static final String API_URL = "https://api.frankfurter.app/latest";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public double convert(double amount, String from, String to) throws Exception {
        String url = API_URL + "?amount=" + amount + "&from=" + from + "&to=" + to;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Erro na API: " + response);
            }
            String jsonData = response.body().string();
            JsonNode root = mapper.readTree(jsonData);
            // Frankfurter returns "rates": { "USD": value }
            JsonNode rates = root.get("rates");
            if (rates == null || rates.get(to) == null) {
                throw new RuntimeException("Moeda não encontrada na resposta da API.");
            }
            return rates.get(to).asDouble();
        }
    }
}