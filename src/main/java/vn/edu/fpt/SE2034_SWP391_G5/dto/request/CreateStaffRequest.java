package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffRequest {
    private String staffType; // DOCTOR or RECEPTIONIST

    private String email;
    private String password;
    private String confirmPassword;

    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String gender;
    private String avatar;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    // Doctor-only
    private Integer departmentId;
    private String degree;
    private String licenseNumber;
    private Integer experienceYears;
    private String doctorStatus;
    private String bio;

    // Common status
    private String accountStatus;
}