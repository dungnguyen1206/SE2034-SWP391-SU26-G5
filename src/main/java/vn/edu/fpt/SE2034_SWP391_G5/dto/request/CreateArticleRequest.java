package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không vượt quá 200 ký tự")
    private String title;
    
    @Size(max = 500, message = "Tóm tắt không vượt quá 500 ký tự")
    private String summary;
    
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    
    @NotBlank(message = "Chuyên mục không được để trống")
    private String category;
    private Long doctorId;
    private String status;
}
