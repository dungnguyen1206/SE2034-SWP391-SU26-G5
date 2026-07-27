package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentRequest {

    @NotNull(message = "Vui lòng chọn bác sĩ")
    private Long doctorId;

    // serviceId không còn bắt buộc - sẽ tự động set là "Khám lâm sàng"
    private Long serviceId;

    @NotNull(message = "Vui lòng chọn khung giờ")
    private Long slotId;

    // Cần để redirect về step2 đúng URL khi có lỗi
    // Trước đây không có field này nên redirect mất departmentId
    private Integer departmentId;

    @jakarta.validation.constraints.NotBlank(message = "Vui lòng nhập triệu chứng / ghi chú khám")
    private String note;
}
