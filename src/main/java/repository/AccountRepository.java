package repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.codegen.maven.tables.records.AccountsRecord;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;

import static org.jooq.codegen.maven.tables.Accounts.ACCOUNTS;

@Singleton
public class AccountRepository implements Repository {

    private DataSource dataSource;

    @Inject
    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long createAccount(BigDecimal balance) {
        return getContext().insertInto(ACCOUNTS, ACCOUNTS.BALANCE).values(balance)
                .returning(ACCOUNTS.ID).fetchOne().getId();
    }

    @Override
    public int deleteAccount(long accountId) {
        return getContext().delete(ACCOUNTS).where(ACCOUNTS.ID.eq(accountId))
                .returning(ACCOUNTS.ID).execute();
    }

    @Override
    public AccountsRecord findAccount(long accountId) {
        return getContext().fetchOne(ACCOUNTS, ACCOUNTS.ID.eq(accountId));
    }

    @Override
    public int updateAccounts(AccountsRecord from, AccountsRecord to) {
        return getContext().transactionResult(configuration -> {
            int result = 0;

            DSLContext ctx = DSL.using(configuration);
            result += ctx.executeUpdate(from);
            result += ctx.executeUpdate(to);

            return result;
        });
    }

    @SneakyThrows
    private DSLContext getContext() {
        Connection connection = dataSource.getConnection();
        return DSL.using(connection, SQLDialect.MYSQL);
    }
}
