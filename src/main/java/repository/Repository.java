package repository;

import org.jooq.codegen.maven.tables.records.AccountsRecord;

import java.math.BigDecimal;

public interface Repository {

    /**
     * Creates new account with specified balance
     *
     * @param balance initial balance
     * @return new account id
     */
    long createAccount(BigDecimal balance);

    /**
     * Deletes account by id
     *
     * @param accountId account id
     * @return number of deleted rows
     */
    int deleteAccount(long accountId);

    /**
     * Finds account record by id
     *
     * @param accountId account id
     * @return account record
     */
    AccountsRecord findAccount(long accountId);

    /**
     * Updates account record
     * @param record new account record
     * @return number of updated rows
     */
    int updateAccount(AccountsRecord record);
}
