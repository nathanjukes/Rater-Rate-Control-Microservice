package RateControl.Exceptions;

public class UnauthorizedException extends Exception {
    public UnauthorizedException() {

    }

    public UnauthorizedException(String message) {
        super(message);
    }
}