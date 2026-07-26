package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Dữ liệu bài viết dùng để hiển thị ra giao diện
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private String title;
    private String summary;
    private String content;
    private String category;
    private String thumbnailUrl;
    private String status;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    // Tên tác giả đã chọn sẵn để giao diện không phải tự ghép
    private String authorName;

    // Id bác sĩ đứng tên, dùng để đổ lại vào form khi sửa bài
    private Long doctorId;
}
