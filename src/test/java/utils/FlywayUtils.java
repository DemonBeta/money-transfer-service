package utils;

import lombok.experimental.UtilityClass;
import org.flywaydb.core.Flyway;
import utils.ConfigReader;

import java.util.Properties;

@UtilityClass
public class FlywayUtils {

    private static Flyway flyway;

    static {
        flyway = configureFlyway(ConfigReader.read("test.properties"));
    }

    public static void migrate() {
        flyway.clean();
        flyway.migrate();
    }

    public static void clean() {
        flyway.clean();
    }

    private static Flyway configureFlyway(Properties properties) {
        return Flyway.configure().dataSource(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        ).load();
    }
}
