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

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

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
        return articleRepository.findById(id).orElse(null);
    }

    @Override
    public void createArticle(CreateArticleRequest request, User currentUser) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        article.setStatus(request.getStatus());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setCreatedBy(currentUser);
        article.setViewCount(0);
        
        // Generate slug
        if (request.getTitle() != null) {
            article.setSlug(generateSlug(request.getTitle()));
        }

        if ("PUBLISHED".equals(request.getStatus())) {
            article.setPublishedAt(LocalDateTime.now());
        }

        if (request.getDoctorId() != null) {
            User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            article.setDoctorAuthor(doctor);
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
        }
        article.setStatus(request.getStatus());
        article.setUpdatedAt(LocalDateTime.now());
        
        if (request.getTitle() != null) {
            article.setSlug(generateSlug(request.getTitle()));
        }

        if (request.getDoctorId() != null) {
            User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            article.setDoctorAuthor(doctor);
        } else {
            article.setDoctorAuthor(null);
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

    private String generateSlug(String input) {
        String noWhiteSpace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]").matcher(normalized).replaceAll("");
        return slug.toLowerCase().replaceAll("[^a-z0-9-]", "").replaceAll("-+", "-");
    }
}
