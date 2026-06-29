package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {
    private String title;
    private String summary;
    private String content;
    private String category;
    private Long doctorId;
    private String status;
}
