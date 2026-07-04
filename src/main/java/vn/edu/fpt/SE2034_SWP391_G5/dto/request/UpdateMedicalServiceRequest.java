package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedicalServiceRequest {
    private Long serviceId;
    private String medicalServiceName;
    private Department department;

    @NotNull(message = "Không được bỏ trống giá tiền")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá dịch vụ phải lớn hơn 0")
    private BigDecimal price;

    private String description;

    @NotNull(message = "Không được bỏ trống thời gian dự kiến")
    @DecimalMin(value = "0.0", inclusive = false, message = "Thời gian dự kiến phải lớn hơn 0")
    private Integer duration;

    private String status;
    private String imageUrl;
}
