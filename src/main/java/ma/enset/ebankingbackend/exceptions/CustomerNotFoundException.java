package ma.enset.ebankingbackend.exceptions;

//exception metier no survielleee !
public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
