package vn.edu.fpt.SE2034_SWP391_G5.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private MedicalService service;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "price_applied")
    private BigDecimal priceApplied;

    private Integer quantity;

    @Column(name = "line_total")
    private BigDecimal lineTotal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_service_order_id", nullable = false)
    private MedicalServiceOrder medicalServiceOrder;
}
