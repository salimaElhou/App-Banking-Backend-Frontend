package ma.enset.ebankingbackend.exceptions;

public class BalanceNotSufficentException extends Exception {
    public BalanceNotSufficentException(String msg) {
        super(msg);
    }
}
