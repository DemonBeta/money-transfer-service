import com.google.inject.Guice;
import com.google.inject.Injector;
import configuration.AppConfig;
import controller.TransferController;
import exception.AccountNotFoundException;
import exception.TransferException;
import io.javalin.Javalin;
import io.javalin.core.validation.JavalinValidation;
import org.eclipse.jetty.http.HttpStatus;

import java.math.BigDecimal;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

public class App {
    private static final Injector INJECTOR = Guice.createInjector(new AppConfig());
    private static Javalin javalin = Javalin.create();

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        JavalinValidation.register(BigDecimal.class, BigDecimal::new);
        configureRequestMapping();
        configureExceptionMapping();

        javalin.start(Integer.parseInt(System.getProperty("port", "8080")));
    }

    private void configureRequestMapping() {
        TransferController transferController = INJECTOR.getInstance(TransferController.class);
        javalin.routes(() -> {
            path("account", () -> {
                path("create", () -> put(transferController::createAccount));
                path("get-balance", () -> get(transferController::getBalance));
                path("delete", () ->  delete(transferController::deleteAccount));
                path("transfer", () -> post(transferController::transferMoney));
            });
        });
    }

    private void configureExceptionMapping() {
        javalin.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST_400);
        });
        javalin.exception(AccountNotFoundException.class, (e, ctx) -> {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND_404);
        });
        javalin.exception(TransferException.class, (e, ctx) -> {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST_400);
        });
    }
}
