package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class InvoiceDetailResponse {
    private Long appointmentId;
    private String appointmentCode;
    private Integer stt;
    
    // Patient info
    private String patientFullName;
    private String patientPhone;
    private String patientAddress;
    
    // Appointment info
    private String doctorFullName;
    private String departmentName;
    private String roomNumber;
    
    // Medical Record info
    private String diagnosis;
    
    // History & Pending
    private List<PaidInvoiceDto> paidInvoices;
    private List<UnpaidServiceDto> unpaidServices;
    
    private BigDecimal totalPaidAmount;
    private BigDecimal totalUnpaidAmount;

    @Getter
    @Setter
    @Builder
    public static class PaidInvoiceDto {
        private Long invoiceId;
        private String invoiceCode;
        private BigDecimal totalAmount;
        private LocalDateTime paidAt;
        private String paymentMethod;
        private List<InvoiceItemResponse> items;
    }

    @Getter
    @Setter
    @Builder
    public static class UnpaidServiceDto {
        private Long id; // MedicalServiceOrder ID or Appointment ID
        private String serviceName;
        private BigDecimal price;
        private String type; // "APPOINTMENT" or "ADDITIONAL"
    }
}
