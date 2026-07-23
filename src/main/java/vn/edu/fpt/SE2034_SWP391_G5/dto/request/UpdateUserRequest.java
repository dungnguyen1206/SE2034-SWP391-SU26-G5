package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private Long id;
    private String profileType;
    private String staffRole;

    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    private String avatar;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private User createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accountStatus;
    private String bio;

    // Patient-only fields
    private String bloodType;
    private Integer provinceId;
    private String addressLine;
    private Boolean defaultAddress;

    // Doctor-only fields
    private Integer departmentId;
    private String degree;
    private String licenseNumber;

    private LocalDate licenseIssueDate;

    private String doctorStatus;

    public Integer getExperienceYear(){
        return 0; // Tạm thời trả về 0, cần implement logic khác nếu cần
    }
}
