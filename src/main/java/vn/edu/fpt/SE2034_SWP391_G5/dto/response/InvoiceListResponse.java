package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InvoiceListResponse {
    private Long appointmentId; // ID của lịch hẹn
    private Integer stt;
    private String appointmentCode;
    private String patientFullName;
    private String phone;
    private BigDecimal displayAmount; // Số tiền hiển thị (tổng đã trả hoặc tổng nợ)
    private String paymentStatus; // "PAID" (Hoàn thành) hoặc "UNPAID" (Chưa thanh toán)
}
