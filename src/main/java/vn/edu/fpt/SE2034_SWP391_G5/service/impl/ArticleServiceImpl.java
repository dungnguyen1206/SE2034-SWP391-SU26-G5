package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ArticleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ImageUploadService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ArticleCommentRepository;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.util.SlugUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service implementation for managing articles.
 * Handles article creation, retrieval, and updates.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public List<Article> getArticlesByFilters(String keyword, String category, String status) {
        return articleRepository.findByFilters(keyword, category, status);
    }

    @Override
    public Page<Article> getArticlesByFilters(String keyword, String category, String status, Pageable pageable) {
        return articleRepository.findByFiltersPageable(keyword, category, status, pageable);
    }

    @Override
    public Article getArticleById(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article != null && "DELETED".equals(article.getStatus())) {
            return null;
        }
        return article;
    }

    /**
     * Creates a new article with the provided request data.
     * Generates a unique slug, sets the author, and handles thumbnail uploading.
     */
    @Override
    public void createArticle(CreateArticleRequest request, User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Người tạo bài viết không hợp lệ.");
        }

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        
        String status = request.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "DRAFT";
        }
        article.setStatus(status);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setCreatedBy(currentUser);
        article.setViewCount(0);
        
        // Generate slug
        if (request.getTitle() != null) {
            article.setSlug(SlugUtil.generateSlug(request.getTitle()));
        }

        if ("PUBLISHED".equals(request.getStatus())) {
            article.setPublishedAt(LocalDateTime.now());
        }

        if (request.getDoctorId() != null) {
            User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            article.setDoctorAuthor(doctor);
        }

        MultipartFile file = request.getThumbnailFile();
        if (file != null && !file.isEmpty()) {
            String thumbnailUrl = imageUploadService.uploadImage(file);
            article.setThumbnailUrl(thumbnailUrl);
        }

        articleRepository.save(article);
    }

    @Override
    public void updateArticle(Long id, CreateArticleRequest request) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) return;

        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        
        // Handle publishing transitions
        if ("PUBLISHED".equals(request.getStatus()) && !"PUBLISHED".equals(article.getStatus())) {
            article.setPublishedAt(LocalDateTime.now());
        } else if ("DRAFT".equals(request.getStatus()) && "PUBLISHED".equals(article.getStatus())) {
            article.setPublishedAt(null);
        }
        
        String status = request.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "DRAFT";
        }
        article.setStatus(status);
        article.setUpdatedAt(LocalDateTime.now());
        
        if (request.getTitle() != null) {
            article.setSlug(SlugUtil.generateSlug(request.getTitle()));
        }

        if (request.getDoctorId() != null) {
            User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            article.setDoctorAuthor(doctor);
        } else {
            article.setDoctorAuthor(null);
        }

        MultipartFile file = request.getThumbnailFile();
        if (file != null && !file.isEmpty()) {
            String thumbnailUrl = imageUploadService.uploadImage(file);
            article.setThumbnailUrl(thumbnailUrl);
        }

        articleRepository.save(article);
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article != null) {
            article.setStatus("DELETED");
            articleRepository.save(article);
        }
    }

    @Override
    public List<ArticleComment> getCommentsByArticleId(Long articleId) {
        return articleCommentRepository.findByArticleIdAndParentIsNullOrderByCreatedAtDesc(articleId);
    }

    @Override
    public long getCommentCountByArticleId(Long articleId) {
        return articleCommentRepository.countByArticleId(articleId);
    }

    @Override
    public List<Article> getRelatedArticles(String category, Long excludeId) {
        return articleRepository.findTop3ByCategoryAndIdNotAndStatusOrderByCreatedAtDesc(category, excludeId, "PUBLISHED");
    }

    @Override
    public void addComment(Long articleId, String content, Long parentId, String username) {
        Article article = getArticleById(articleId);
        if (article == null || !"PUBLISHED".equals(article.getStatus())) {
            throw new IllegalArgumentException("Article not found or not published");
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        ArticleComment comment = new ArticleComment();
        comment.setArticle(article);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        if (parentId != null) {
            ArticleComment parent = articleCommentRepository.findById(parentId).orElse(null);
            if (parent != null) {
                comment.setParent(parent);
            }
        }

        articleCommentRepository.save(comment);
    }

    @Override
    public void incrementViewCount(Long articleId) {
        Article article = getArticleById(articleId);
        if (article != null) {
            if (article.getViewCount() == null) {
                article.setViewCount(1);
            } else {
                article.setViewCount(article.getViewCount() + 1);
            }
            articleRepository.save(article);
        }
    }
}
