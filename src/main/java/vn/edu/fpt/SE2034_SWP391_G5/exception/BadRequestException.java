package vn.edu.fpt.SE2034_SWP391_G5.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}

