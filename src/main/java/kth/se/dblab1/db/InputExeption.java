package kth.se.dblab1.db;

public class InputExeption extends Exception{
    public InputExeption(String msg, Exception cause) {
        super(msg, cause);
    }

    public InputExeption(String msg) {
        super(msg);
    }

    public InputExeption() {
        super();
    }
}
