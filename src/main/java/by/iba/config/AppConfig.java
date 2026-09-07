package by.iba.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

@Getter
public final class AppConfig {

    private static final String CONFIG_FILE = "application.yml";
    private static volatile AppConfig instance;

    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private final int poolSize;

    private AppConfig() {
        Map<String, Object> root = load();
        @SuppressWarnings("unchecked")
        Map<String, Object> db = (Map<String, Object>) root.get("db");
        this.driver = (String) db.get("driver");
        this.url = (String) db.get("url");
        this.user = (String) db.get("user");
        this.password = String.valueOf(db.get("password"));
        this.poolSize = (Integer) db.get("poolsize");
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private Map<String, Object> load() {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw missingConfigException();
            }
            return new Yaml().load(in);
        } catch (IOException e) {
            throw wrapLoadException(e);
        }
    }

    @lombok.Generated
    private static IllegalStateException missingConfigException() {
        return new IllegalStateException("Config file not found: " + CONFIG_FILE);
    }

    @lombok.Generated
    private static IllegalStateException wrapLoadException(IOException e) {
        return new IllegalStateException("Cannot read config file", e);
    }
}
