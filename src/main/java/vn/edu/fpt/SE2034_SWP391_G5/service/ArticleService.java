package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import java.util.List;

public interface ArticleService {
    List<Article> getAllArticles();
    List<Article> getArticlesByFilters(String keyword, String category, String status);
    Article getArticleById(Long id);
    void createArticle(CreateArticleRequest request, User currentUser);
    void updateArticle(Long id, CreateArticleRequest request);
    void deleteArticle(Long id);
}
