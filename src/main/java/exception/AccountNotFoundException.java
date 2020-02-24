package exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(long id) {
        super("Account with id " + id + " not found");
    }
}
