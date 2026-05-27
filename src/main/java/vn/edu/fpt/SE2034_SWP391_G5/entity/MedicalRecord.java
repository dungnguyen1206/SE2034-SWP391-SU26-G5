package vn.edu.fpt.SE2034_SWP391_G5.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "examination_date")
    private LocalDateTime examinationDate;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String symptoms;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String diagnosis;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String conclusion;

    @Column(name = "prescription_text", columnDefinition = "NVARCHAR(MAX)")
    private String prescriptionText;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "blood_glucose")
    private BigDecimal bloodGlucose;

    private BigDecimal weight;

    private String status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
