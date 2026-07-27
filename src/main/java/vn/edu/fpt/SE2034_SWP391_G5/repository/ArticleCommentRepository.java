package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;

import java.util.List;

// Truy vấn dữ liệu bình luận bài viết
@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    // Lấy bình luận của một bài viết, mới nhất lên đầu
    List<ArticleComment> findByArticleIdOrderByCreatedAtDesc(Long articleId);

    // Đếm số bình luận của một bài viết
    long countByArticleId(Long articleId);
}
