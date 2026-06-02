package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Tên không được để trống")
    private String firstName;

    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private String gender;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    private String bloodType;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String addressLine;

    private Integer provinceId;
}
