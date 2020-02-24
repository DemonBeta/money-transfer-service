package utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public final class ConfigReader {

    @SneakyThrows
    public static Properties read(String path) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }
}
