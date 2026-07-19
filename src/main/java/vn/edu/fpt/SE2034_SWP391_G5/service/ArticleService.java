package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import java.util.List;

public interface ArticleService {
    List<Article> getAllArticles();
    List<Article> getArticlesByFilters(String keyword, String category, String status);
    Page<Article> getArticlesByFilters(String keyword, String category, String status, Pageable pageable);
    Article getArticleById(Long id);
    void createArticle(CreateArticleRequest request, User currentUser);
    void updateArticle(Long id, CreateArticleRequest request);
    void deleteArticle(Long id);
    java.util.List<vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment> getCommentsByArticleId(Long articleId);
    long getCommentCountByArticleId(Long articleId);
    java.util.List<vn.edu.fpt.SE2034_SWP391_G5.entity.Article> getRelatedArticles(String category, Long excludeId);
    void addComment(Long articleId, String content, Long parentId, String username);
    void incrementViewCount(Long articleId);
}
