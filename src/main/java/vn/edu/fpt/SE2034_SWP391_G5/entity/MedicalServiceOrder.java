package vn.edu.fpt.SE2034_SWP391_G5.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "medical_service_orders", uniqueConstraints = @UniqueConstraint(columnNames = {"medical_service_id","medical_record_id"}))
public class MedicalServiceOrder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_service_id")
    private MedicalService medicalService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Column(name = "result", columnDefinition = "NVARCHAR(500)")
    private String result;

    private String status;

    @Column(name = "notes", columnDefinition = "NVARCHAR(500)")
    private String note;

    @Column(name = "price_applied")
    private BigDecimal priceReference;

    @Column(name = "created_at")
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @OneToOne(mappedBy = "medicalServiceOrder")
    private InvoiceItem invoiceItem;

}
