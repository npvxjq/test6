package test;

public class CyclicException extends Exception {

    public CyclicException() {
        super("Cyclic dependencies found");
    }
}
