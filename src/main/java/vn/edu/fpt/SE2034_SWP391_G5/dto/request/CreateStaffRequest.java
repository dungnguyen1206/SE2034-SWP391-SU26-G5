package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffRequest {
    @NotBlank(message = "Loại nhân viên không được để trống")
    private String staffType; // DOCTOR or RECEPTIONIST

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Họ không được để trống")
    private String firstName;
    private String middleName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;
    private String avatar;

    @NotNull(message = "Ngày sinh không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    // Doctor-only
    private Integer departmentId;
    private String degree;
    private String licenseNumber;

    @Min(value = 0, message = "Số năm kinh nghiệm không được âm")
    private Integer experienceYears;
    private String doctorStatus;
    private String bio;

    // Common status
    private String accountStatus;
}
