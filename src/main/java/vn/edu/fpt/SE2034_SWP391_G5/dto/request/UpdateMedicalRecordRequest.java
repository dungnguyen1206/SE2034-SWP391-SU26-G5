package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateMedicalRecordRequest {

    @NotBlank(message = "Triệu chứng không được để trống")
    @Size(max = 2000, message = "Triệu chứng không được vượt quá 2000 ký tự")
    private String symptoms;

    @NotBlank(message = "Chẩn đoán không được để trống")
    @Size(max = 2000, message = "Chẩn đoán không được vượt quá 2000 ký tự")
    private String diagnosis;

    @NotBlank(message = "Huyết áp không được để trống")
    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "Huyết áp phải đúng định dạng (ví dụ: 120/80)")
    private String bloodPressure;

    @NotNull(message = "Cân nặng không được để trống")
    @DecimalMin(value = "1.0", message = "Cân nặng phải lớn hơn 1.0 kg")
    @DecimalMax(value = "300.0", message = "Cân nặng không được vượt quá 300.0 kg")
    private BigDecimal weight;

    @NotBlank(message = "Kết luận không được để trống")
    @Size(max = 2000, message = "Kết luận không được vượt quá 2000 ký tự")
    private String conclusion;

    @NotBlank(message = "Đơn thuốc không được để trống")
    @Size(max = 2000, message = "Đơn thuốc không được vượt quá 2000 ký tự")
    private String prescriptionText;

    @NotBlank(message = "Ghi chú không được để trống")
    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String notes;

    @NotNull(message = "Đường huyết không được để trống")
    @DecimalMin(value = "0.1", message = "Đường huyết phải lớn hơn 0 mmol/L")
    @DecimalMax(value = "50.0", message = "Đường huyết không được vượt quá 50.0 mmol/L")
    private BigDecimal bloodGlucose;

    @NotNull(message = "Nhịp tim không được để trống")
    @Min(value = 30, message = "Nhịp tim phải từ 30 bpm trở lên")
    @Max(value = 250, message = "Nhịp tim không được vượt quá 250 bpm")
    private Integer heartRate;
}
