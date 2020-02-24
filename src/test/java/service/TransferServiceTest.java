package service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import exception.AccountNotFoundException;
import exception.TransferException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.FlywayUtils;
import utils.TestConfig;

import java.math.BigDecimal;

public class TransferServiceTest {

    private static final Injector INJECTOR = Guice.createInjector(new TestConfig());
    private TransferService transferService;

    @Before
    public void setUp() {
        transferService = INJECTOR.getInstance(TransferService.class);
        FlywayUtils.migrate();
    }

    @After
    public void tearDown() {
        FlywayUtils.clean();
    }

    @Test
    public void getBalance() {
        Assert.assertEquals(0, transferService.getBalance(3L).compareTo(new BigDecimal(20000L)));
    }

    @Test(expected = AccountNotFoundException.class)
    public void getBalance_AccNotFound() {
        transferService.getBalance(20L);
    }

    @Test
    public void createAccount() {
        BigDecimal balance = new BigDecimal(1234L);
        long id = transferService.createAccount(balance);
        Assert.assertEquals(5, id);
        Assert.assertEquals(0, transferService.getBalance(id).compareTo(balance));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAccount_IllegalArg() {
        transferService.createAccount(new BigDecimal(-1234L));
    }

    @Test(expected = AccountNotFoundException.class)
    public void deleteAccount() {
        long accountId = 1L;
        transferService.deleteAccount(accountId);
        transferService.getBalance(accountId);
    }

    @Test
    public void transferMoney() {
        long fromId = 3L;
        long toId = 4L;
        BigDecimal amount = new BigDecimal(5000);

        transferService.transferMoney(fromId, toId, amount);
        Assert.assertEquals(0, transferService.getBalance(fromId).compareTo(new BigDecimal(15000L)));
        Assert.assertEquals(0, transferService.getBalance(toId).compareTo(new BigDecimal(35000L)));
    }

    @Test(expected = TransferException.class)
    public void transferMoney_notEnoughMoney() {
        long fromId = 3L;
        long toId = 4L;
        BigDecimal amount = new BigDecimal(50000);

        transferService.transferMoney(fromId, toId, amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void transferMoney_zeroAmount() {
        long fromId = 3L;
        long toId = 4L;
        BigDecimal amount = new BigDecimal(0);

        transferService.transferMoney(fromId, toId, amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void transferMoney_sameAccount() {
        long fromId = 3L;
        long toId = 3L;
        BigDecimal amount = new BigDecimal(5000);

        transferService.transferMoney(fromId, toId, amount);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_noSuchAccount() {
        long fromId = 3L;
        long toId = 20L;
        BigDecimal amount = new BigDecimal(5000);

        transferService.transferMoney(fromId, toId, amount);
    }
}