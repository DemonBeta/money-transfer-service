package configuration;

import com.google.inject.AbstractModule;
import controller.TransferController;
import lombok.SneakyThrows;
import repository.AccountRepository;
import repository.Repository;
import service.TransferService;
import utils.ConfigReader;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class AppConfig extends AbstractModule {

    @SneakyThrows
    @Override
    protected void configure() {
        bind(DataSource.class).toInstance(prepareDataSource());
        bind(AccountRepository.class).toConstructor(AccountRepository.class.getConstructor(DataSource.class));
        bind(Repository.class).to(AccountRepository.class);
        bind(TransferService.class).toConstructor(TransferService.class.getConstructor(Repository.class));
        bind(TransferController.class).toConstructor(TransferController.class.getConstructor(TransferService.class));
    }

    private static DataSource prepareDataSource() {
        Properties properties = ConfigReader.read("app.properties");

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setUser(properties.getProperty("db.user"));
        dataSource.setPassword(properties.getProperty("db.password"));

        return dataSource;
    }
}
