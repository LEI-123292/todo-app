package currency;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
            return root.get("rates").get(to).asDouble();
        }
    }
}
