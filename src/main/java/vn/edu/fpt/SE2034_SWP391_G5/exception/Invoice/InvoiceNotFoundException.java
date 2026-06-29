package vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
