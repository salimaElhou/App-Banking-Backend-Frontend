package ma.enset.ebankingbackend.exceptions;

public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String msg) {
        super(msg);
    }
}
