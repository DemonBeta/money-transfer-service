package utils;

import com.google.inject.AbstractModule;
import lombok.SneakyThrows;
import org.h2.jdbcx.JdbcDataSource;
import repository.AccountRepository;
import repository.Repository;
import service.TransferService;
import utils.ConfigReader;

import javax.sql.DataSource;
import java.util.Properties;

public class TestConfig extends AbstractModule {

    @SneakyThrows
    @Override
    protected void configure() {
        bind(DataSource.class).toInstance(prepareDataSource());
        bind(AccountRepository.class).toConstructor(AccountRepository.class.getConstructor(DataSource.class));
        bind(Repository.class).to(AccountRepository.class);
        bind(TransferService.class).toConstructor(TransferService.class.getConstructor(Repository.class));
    }

    private static DataSource prepareDataSource() {
        Properties properties = ConfigReader.read("test.properties");

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setUser(properties.getProperty("db.user"));
        dataSource.setPassword(properties.getProperty("db.password"));

        return dataSource;
    }
}
