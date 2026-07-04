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
    private Long id;
    private String invoiceCode;
    
    // Patient info
    private String patientFullName;
    private String patientPhone;
    private String patientAddress;
    
    // Appointment info
    private String appointmentCode;
    private String doctorFullName;
    private String departmentName;
    private String roomNumber;
    
    // Medical Record info
    private String diagnosis;
    
    // Invoice details
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    
    // Items
    private List<InvoiceItemResponse> items;
}
