package vn.edu.fpt.SE2034_SWP391_G5.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String status;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    private String phone;

    private String gender;

    private String avatar;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "blood_type")
    private String bloodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private String degree;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String bio;

    @Column(name = "doctor_status")
    private String doctorStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "createdBy")
    private Set<User> createdUsers;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user")
    private Set<UserAddress> addresses;

    @OneToMany(mappedBy = "doctor")
    private Set<DoctorSchedule> doctorSchedules;

    @OneToMany(mappedBy = "createdBy")
    private Set<DoctorSchedule> createdDoctorSchedules;

    @OneToMany(mappedBy = "patient")
    private Set<Appointment> patientAppointments;

    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> doctorAppointments;

    @OneToMany(mappedBy = "patient")
    private Set<MedicalRecord> patientMedicalRecords;

    @OneToMany(mappedBy = "doctor")
    private Set<MedicalRecord> doctorMedicalRecords;

    @OneToMany(mappedBy = "createdBy")
    private Set<MedicalRecord> createdMedicalRecords;

    @OneToMany(mappedBy = "updatedBy")
    private Set<MedicalRecord> updatedMedicalRecords;

    @OneToMany(mappedBy = "doctorAuthor")
    private Set<Article> doctorArticles;

    @OneToMany(mappedBy = "createdBy")
    private Set<Article> createdArticles;

    @OneToMany(mappedBy = "createdBy")
    private Set<News> createdNews;

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method để lấy tên đầy đủ
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        
        if (firstName != null && !firstName.isEmpty()) {
            fullName.append(firstName);
        }
        
        if (middleName != null && !middleName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName);
        }
        
        if (lastName != null && !lastName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName);
        }
        
        return fullName.length() > 0 ? fullName.toString() : "Người dùng";
    }

}
