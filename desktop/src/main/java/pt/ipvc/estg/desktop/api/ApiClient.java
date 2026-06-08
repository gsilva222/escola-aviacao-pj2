package pt.ipvc.estg.desktop.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final HttpClient httpClient;
    private final String baseUrl;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(AppConfig.getConnectTimeoutSeconds()))
                .build();
        this.baseUrl = AppConfig.getBaseUrl();
    }

    public <T> T get(String path, Class<T> type) {
        return exchange("GET", path, null, type, true);
    }

    public <T> T postPublic(String path, Object body, Class<T> type) {
        return exchange("POST", path, body, type, false);
    }

    public <T> T post(String path, Object body, Class<T> type) {
        return exchange("POST", path, body, type, true);
    }

    public <T> List<T> getList(String path, Class<T> elementType) {
        String pagedPath = path.contains("size=") ? path : path + (path.contains("?") ? "&" : "?") + "size=500";
        try {
            String body = exchangeRaw("GET", pagedPath, null, true);
            if (body == null || body.isBlank()) {
                return Collections.emptyList();
            }
            JsonNode root = MAPPER.readTree(body);
            JsonNode content = root.has("content") ? root.get("content") : root;
            return MAPPER.convertValue(
                    content,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (IOException ex) {
            throw new ApiException("Erro ao ler lista da API: " + ex.getMessage(), ex);
        }
    }

    public <T> T put(String path, Object body, Class<T> type) {
        return exchange("PUT", path, body, type, true);
    }

    public void delete(String path) {
        exchange("DELETE", path, null, Void.class, true);
    }

    public <T> List<T> getArray(String path, Class<T> elementType) {
        try {
            String body = exchangeRaw("GET", path, null, true);
            if (body == null || body.isBlank()) {
                return Collections.emptyList();
            }
            JsonNode root = MAPPER.readTree(body);
            JsonNode content = root.has("content") ? root.get("content") : root;
            return MAPPER.convertValue(
                    content,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (IOException ex) {
            throw new ApiException("Erro ao ler lista da API: " + ex.getMessage(), ex);
        }
    }

    public byte[] downloadBytes(String path) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(AppConfig.getConnectTimeoutSeconds()))
                    .GET();
            requireToken().ifPresent(token -> builder.header("Authorization", "Bearer " + token));
            HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }
            throw new ApiException(response.statusCode(), "Download falhou");
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ApiException("Pedido interrompido", ex);
        } catch (IOException ex) {
            throw new ApiException("Erro no download: " + ex.getMessage(), ex);
        }
    }

    public <T> T uploadMultipart(String path, Path filePath, String category, Class<T> responseType) {
        try {
            String boundary = "----AeroSchoolBoundary" + System.currentTimeMillis();
            byte[] fileBytes = Files.readAllBytes(filePath);
            String fileName = filePath.getFileName().toString();
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            StringBuilder header = new StringBuilder();
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(fileName).append("\"\r\n");
            header.append("Content-Type: ").append(contentType).append("\r\n\r\n");
            String footer = "\r\n";
            if (category != null && !category.isBlank()) {
                footer += "--" + boundary + "\r\n";
                footer += "Content-Disposition: form-data; name=\"category\"\r\n\r\n";
                footer += category + "\r\n";
            }
            footer += "--" + boundary + "--\r\n";

            byte[] headerBytes = header.toString().getBytes(StandardCharsets.UTF_8);
            byte[] footerBytes = footer.getBytes(StandardCharsets.UTF_8);
            byte[] body = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
            System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
            System.arraycopy(fileBytes, 0, body, headerBytes.length, fileBytes.length);
            System.arraycopy(footerBytes, 0, body, headerBytes.length + fileBytes.length, footerBytes.length);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(AppConfig.getConnectTimeoutSeconds()))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body));
            requireToken().ifPresent(token -> builder.header("Authorization", "Bearer " + token));

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return MAPPER.readValue(response.body(), responseType);
            }
            throw new ApiException(response.statusCode(), extractMessage(response.body()));
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ApiException("Pedido interrompido", ex);
        } catch (IOException ex) {
            throw new ApiException("Erro no upload: " + ex.getMessage(), ex);
        }
    }

    private <T> T exchange(String method, String path, Object body, Class<T> type, boolean auth) {
        String raw = exchangeRaw(method, path, body, auth);
        if (type == Void.class || raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(raw, type);
        } catch (IOException ex) {
            throw new ApiException("Erro ao interpretar resposta da API: " + ex.getMessage(), ex);
        }
    }

    String exchangeRaw(String method, String path, Object body, boolean auth) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(AppConfig.getConnectTimeoutSeconds()))
                    .header("Accept", "application/json");

            if (auth) {
                requireToken().ifPresent(token -> builder.header("Authorization", "Bearer " + token));
            }

            String jsonBody = null;
            if (body != null) {
                jsonBody = MAPPER.writeValueAsString(body);
                builder.header("Content-Type", "application/json");
            }

            builder.method(method, bodySelector(method, jsonBody));
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }

            String message = extractMessage(response.body());
            throw new ApiException(response.statusCode(), message);
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ApiException("Pedido interrompido", ex);
        } catch (IOException ex) {
            throw new ApiException("Erro de comunicacao com a API: " + ex.getMessage(), ex);
        }
    }

    private static HttpRequest.BodyPublisher bodySelector(String method, String jsonBody) {
        if (jsonBody != null) {
            return HttpRequest.BodyPublishers.ofString(jsonBody);
        }
        if ("GET".equals(method) || "DELETE".equals(method)) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.noBody();
    }

    private Optional<String> requireToken() {
        if (!SessionContext.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SessionContext.getToken());
    }

    private String extractMessage(String body) {
        if (body == null || body.isBlank()) {
            return "Erro na API";
        }
        try {
            var node = MAPPER.readTree(body);
            if (node.has("message")) {
                return node.get("message").asText();
            }
        } catch (IOException ignored) {
            // use raw body
        }
        return body.length() > 200 ? body.substring(0, 200) : body;
    }
}
