package controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import service.TransferService;

import java.math.BigDecimal;

@Singleton
public class TransferController {

    private  TransferService transferService;

    @Inject
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    public void getBalance(Context ctx) {
        Long id = ctx.queryParam("id", Long.class).get();

        ctx.json(transferService.getBalance(id));
        ctx.status(HttpStatus.OK_200);
    }

    public void createAccount(Context ctx) {
        BigDecimal balance = ctx.queryParam("balance", BigDecimal.class).get();

        ctx.json(transferService.createAccount(balance));
        ctx.status(HttpStatus.CREATED_201);
    }

    public void deleteAccount(Context ctx) {
        Long id = ctx.queryParam("id", Long.class).get();

        transferService.deleteAccount(id);
        ctx.status(HttpStatus.OK_200);
    }

    public void transferMoney(Context ctx) {
        Long fromId = ctx.queryParam("fromId", Long.class).get();
        Long toId = ctx.queryParam("toId", Long.class).get();
        BigDecimal amount = ctx.queryParam("amount", BigDecimal.class).get();

        transferService.transferMoney(fromId, toId, amount);
        ctx.status(HttpStatus.OK_200);
    }
}
