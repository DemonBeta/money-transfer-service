package service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import exception.AccountNotFoundException;
import exception.TransferException;
import org.jooq.codegen.maven.tables.records.AccountsRecord;
import repository.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class TransferService {

    private static final Map<Long, Lock> locks = new ConcurrentHashMap<>();
    private Repository repository;

    @Inject
    public TransferService(Repository repository) {
        this.repository = repository;
    }

    public BigDecimal getBalance(long accountId) {
        AccountsRecord accountsRecord = repository.findAccount(accountId);
        if (accountsRecord == null) {
            throw new AccountNotFoundException(accountId);
        }
        return accountsRecord.getBalance();
    }

    public long createAccount(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The balance must not be negative");
        }
        return repository.createAccount(balance);
    }

    public void deleteAccount(long accountId) {
        Lock lockAccount = locks.computeIfAbsent(accountId, ignored -> new ReentrantLock());
        boolean locked;
        do {
            locked = lockAccount.tryLock();
        } while (!locked);
        try {
            repository.deleteAccount(accountId);
        } finally {
            lockAccount.unlock();
        }
    }

    public void transferMoney(long fromId, long toId, BigDecimal amount) {
        if (fromId == toId) {
            throw new IllegalArgumentException("Transfer to the same account is not allowed");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must not be negative or zero");
        }

        AccountsRecord fromAccount = repository.findAccount(fromId);
        if (fromAccount == null) {
            throw new AccountNotFoundException(fromId);
        }
        AccountsRecord toAccount = repository.findAccount(toId);
        if (toAccount == null) {
            throw new AccountNotFoundException(toId);
        }

        Lock lockFromAccount = locks.computeIfAbsent(fromId, ignored -> new ReentrantLock());
        Lock lockToAccount = locks.computeIfAbsent(toId, ignored -> new ReentrantLock());

        boolean locked = false;
        do {
            if (lockFromAccount.tryLock()) {
                if (lockToAccount.tryLock()) {
                    locked = true;
                } else {
                    lockFromAccount.unlock();
                }
            }
        } while (!locked);

        try {
            fromAccount = repository.findAccount(fromId);
            toAccount = repository.findAccount(toId);
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new TransferException("Not enough balance on account id " + fromAccount.getId());
            }
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            repository.updateAccount(fromAccount);
            repository.updateAccount(toAccount);
        } finally {
            lockFromAccount.unlock();
            lockToAccount.unlock();
        }
    }
}
