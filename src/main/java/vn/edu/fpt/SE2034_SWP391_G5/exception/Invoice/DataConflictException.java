package vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
