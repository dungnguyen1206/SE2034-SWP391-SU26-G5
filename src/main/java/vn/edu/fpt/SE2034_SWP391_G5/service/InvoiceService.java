package vn.edu.fpt.SE2034_SWP391_G5.service;

import java.math.BigDecimal;

public interface InvoiceService {
    BigDecimal getTotalAmount(String paymentStatus, int month, int year);

}
