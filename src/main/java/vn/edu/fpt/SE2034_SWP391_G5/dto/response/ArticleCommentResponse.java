package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Dữ liệu một bình luận dùng để hiển thị ra giao diện
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    // Tên người bình luận
    private String authorName;

    // Chữ cái đầu của tên, dùng cho ảnh đại diện tròn
    private String authorInitial;
}
