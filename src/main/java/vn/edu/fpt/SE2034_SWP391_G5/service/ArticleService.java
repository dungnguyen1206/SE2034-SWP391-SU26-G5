package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface ArticleService {
    // Lấy toàn bộ bài viết
    List<Article> getAllArticles();

    // Lấy bài viết theo bộ lọc (không phân trang)
    List<Article> getArticlesByFilters(String keyword, String category, String status);

    // Lấy bài viết theo bộ lọc (có phân trang)
    Page<Article> getArticlesByFilters(String keyword, String category, String status, Pageable pageable);

    // Lấy bài viết theo id, bài đã xóa mềm coi như không tồn tại
    Article getArticleById(Long id);

    // Tạo bài viết mới
    void createArticle(CreateArticleRequest request, User currentUser);

    // Cập nhật bài viết đã có
    void updateArticle(Long id, CreateArticleRequest request);

    // Xóa mềm bài viết
    void deleteArticle(Long id);

    // Lấy danh sách bình luận của bài viết
    List<ArticleComment> getCommentsByArticleId(Long articleId);

    // Đếm số bình luận của bài viết
    long getCommentCountByArticleId(Long articleId);

    // Lấy các bài viết cùng chuyên mục
    List<Article> getRelatedArticles(String category, Long excludeId);

    // Thêm bình luận vào bài viết
    void addComment(Long articleId, String content, String username);

    // Tăng lượt xem bài viết
    void incrementViewCount(Long articleId);
}
