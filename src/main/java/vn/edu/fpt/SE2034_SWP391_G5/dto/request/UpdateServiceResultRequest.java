package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateServiceResultRequest {

    @NotBlank(message = "Kết quả khám không được để trống")
    @Size(max = 500, message = "Kết quả khám không được vượt quá 500 ký tự")
    private String result;

    @NotBlank(message = "Ghi chú không được để trống")
    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;
}
