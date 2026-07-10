package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalkInBookingRequest {
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String firstName;
    private String lastName;
    private String gender;
    
    @NotNull(message = "Vui lòng chọn khoa")
    private Integer departmentId;
}
