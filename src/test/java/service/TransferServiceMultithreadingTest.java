package service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.FlywayUtils;
import utils.TestConfig;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransferServiceMultithreadingTest {

    private static final Injector INJECTOR = Guice.createInjector(new TestConfig());
    private TransferService transferService;
    private ExecutorService executorService;

    @Before
    public void setUp() {
        executorService = Executors.newFixedThreadPool(5);
        transferService = INJECTOR.getInstance(TransferService.class);
        FlywayUtils.migrate();
    }

    @After
    public void tearDown() {
        FlywayUtils.clean();
    }

    @Test
    public void multithreadingTest() {
        BigDecimal amount = new BigDecimal(10L);
        long fromId = 3L;
        long toId = 4L;
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> transferService.transferMoney(fromId, toId, amount));
            executorService.execute(() -> transferService.transferMoney(toId, fromId, amount));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(0, transferService.getBalance(fromId).compareTo(new BigDecimal(20000L)));
        Assert.assertEquals(0, transferService.getBalance(toId).compareTo(new BigDecimal(30000L)));
    }
}
