package pt.ipvc.estg.desktop.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPERTIES = load();

    private AppConfig() {
    }

    public static boolean isApiEnabled() {
        String override = System.getProperty("aeroschool.api.enabled");
        if (override != null && !override.isBlank()) {
            return Boolean.parseBoolean(override);
        }
        return Boolean.parseBoolean(PROPERTIES.getProperty("api.enabled", "true"));
    }

    public static String getBaseUrl() {
        String override = System.getProperty("aeroschool.api.baseUrl");
        String url = override != null && !override.isBlank()
                ? override
                : PROPERTIES.getProperty("api.baseUrl", "http://localhost:8080/api");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static int getConnectTimeoutSeconds() {
        return Integer.parseInt(PROPERTIES.getProperty("api.connectTimeoutSeconds", "10"));
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("aeroschool.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
            // defaults
        }
        Path external = Path.of("aeroschool.properties");
        if (Files.isRegularFile(external)) {
            try (InputStream in = Files.newInputStream(external)) {
                props.load(in);
            } catch (IOException ignored) {
                // keep classpath values
            }
        }
        return props;
    }
}
