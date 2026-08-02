package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ArticleCommentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ArticleResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ArticleStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ArticleCommentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ArticleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ImageUploadService;
import vn.edu.fpt.SE2034_SWP391_G5.util.SlugUtil;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

// Xử lý nghiệp vụ bài viết: tạo, sửa, xóa mềm, bình luận, lượt xem
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ImageUploadService imageUploadService;

    // Lấy bài viết theo bộ lọc, có phân trang
    @Override
    public Page<ArticleResponse> getArticlesByFilters(String keyword, String category, String status, Pageable pageable) {
        Page<Article> articlePage = articleRepository.findByFiltersPageable(keyword, category, status, pageable);

        List<ArticleResponse> articles = toArticleResponseList(articlePage.getContent());
        return new PageImpl<>(articles, pageable, articlePage.getTotalElements());
    }

    // Lấy bài viết theo id
    @Override
    public ArticleResponse getArticleById(Long id) {
        Article article = findActiveArticle(id);
        if (article == null) {
            return null;
        }
        return toArticleResponse(article);
    }

    // Tạo bài viết mới từ dữ liệu form
    @Override
    public void createArticle(CreateArticleRequest request, User currentUser) {
        if (currentUser == null) {
            throw new BadRequestException("Người tạo bài viết không hợp lệ.");
        }

        Article article = new Article();
        applyRequestFields(article, request);

        article.setStatus(resolveStatus(request.getStatus()));
        article.setCreatedBy(currentUser);
        article.setViewCount(0);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        // Xuất bản ngay thì ghi lại thời điểm xuất bản
        if (ArticleStatus.PUBLISHED.name().equals(request.getStatus())) {
            article.setPublishedAt(LocalDateTime.now());
        }

        article.setDoctorAuthor(findDoctor(request.getDoctorId()));

        String thumbnailUrl = uploadThumbnail(request.getThumbnailFile());
        if (thumbnailUrl != null) {
            article.setThumbnailUrl(thumbnailUrl);
        }

        articleRepository.save(article);
    }

    // Cập nhật bài viết đã có
    @Override
    public void updateArticle(Long id, CreateArticleRequest request) {
        // Bài đã xóa thì không thể sửa lại
        Article article = findActiveArticle(id);
        if (article == null) {
            throw new ResourceNotFoundException("Không tìm thấy bài viết cần cập nhật.");
        }

        applyRequestFields(article, request);

        // Chuyển nháp sang xuất bản thì ghi thời điểm, ngược lại thì xóa đi
        boolean publishNow = ArticleStatus.PUBLISHED.name().equals(request.getStatus());
        boolean wasPublished = ArticleStatus.PUBLISHED.name().equals(article.getStatus());

        if (publishNow && !wasPublished) {
            article.setPublishedAt(LocalDateTime.now());
        } else if (ArticleStatus.DRAFT.name().equals(request.getStatus()) && wasPublished) {
            article.setPublishedAt(null);
        }

        article.setStatus(resolveStatus(request.getStatus()));
        article.setUpdatedAt(LocalDateTime.now());
        article.setDoctorAuthor(findDoctor(request.getDoctorId()));

        String thumbnailUrl = uploadThumbnail(request.getThumbnailFile());
        if (thumbnailUrl != null) {
            article.setThumbnailUrl(thumbnailUrl);
        }

        articleRepository.save(article);
    }

    // Xóa mềm bài viết: chỉ đổi trạng thái, vẫn giữ lại dữ liệu
    @Override
    public void deleteArticle(Long id) {
        Article article = findActiveArticle(id);
        if (article == null) {
            throw new ResourceNotFoundException("Không tìm thấy bài viết cần xóa.");
        }

        article.setStatus(ArticleStatus.DELETED.name());
        articleRepository.save(article);
    }

    // Lấy danh sách bình luận của bài viết, mới nhất lên đầu
    @Override
    public List<ArticleCommentResponse> getCommentsByArticleId(Long articleId) {
        List<ArticleComment> comments = articleCommentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
        List<ArticleCommentResponse> result = new ArrayList<>();
        for (ArticleComment comment : comments) {
            result.add(toCommentResponse(comment));
        }
        return result;
    }

    // Đếm số bình luận của bài viết
    @Override
    public long getCommentCountByArticleId(Long articleId) {
        return articleCommentRepository.countByArticleId(articleId);
    }

    // Lấy tối đa 3 bài viết cùng chuyên mục, trừ bài đang xem
    @Override
    public List<ArticleResponse> getRelatedArticles(String category, Long excludeId) {
        List<Article> articles = articleRepository
                .findTop3ByCategoryAndIdNotAndStatusOrderByCreatedAtDesc(category, excludeId, ArticleStatus.PUBLISHED.name());

        return toArticleResponseList(articles);
    }

    // Thêm bình luận vào bài viết đã xuất bản
    @Override
    public void addComment(Long articleId, String content, String username) {
        // Kiểm tra nội dung trước để dữ liệu sai thì khỏi phải truy vấn database
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException("Nội dung bình luận không được để trống.");
        }

        String trimmedContent = content.trim();
        if (trimmedContent.length() > 1000) {
            throw new BadRequestException("Bình luận không được vượt quá 1000 ký tự.");
        }

        Article article = findActiveArticle(articleId);
        if (article == null || !ArticleStatus.PUBLISHED.name().equals(article.getStatus())) {
            throw new BadRequestException("Bài viết không tồn tại hoặc chưa được xuất bản.");
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Không tìm thấy người dùng.");
        }

        ArticleComment comment = new ArticleComment();
        comment.setArticle(article);
        comment.setUser(user);
        comment.setContent(trimmedContent);
        comment.setCreatedAt(LocalDateTime.now());

        articleCommentRepository.save(comment);
    }

    // Tăng lượt xem mỗi lần mở bài viết
    @Override
    public void incrementViewCount(Long articleId) {
        Article article = findActiveArticle(articleId);
        if (article != null) {
            if (article.getViewCount() == null) {
                article.setViewCount(1);
            } else {
                article.setViewCount(article.getViewCount() + 1);
            }
            articleRepository.save(article);
        }
    }

    // Đổi bài viết trong database sang dữ liệu hiển thị
    private ArticleResponse toArticleResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setSummary(article.getSummary());
        response.setContent(article.getContent());
        response.setCategory(article.getCategory());
        response.setThumbnailUrl(article.getThumbnailUrl());
        response.setStatus(article.getStatus());
        response.setViewCount(article.getViewCount());
        response.setPublishedAt(article.getPublishedAt());
        response.setCreatedAt(article.getCreatedAt());
        response.setAuthorName(buildAuthorName(article));

        if (article.getDoctorAuthor() != null) {
            response.setDoctorId(article.getDoctorAuthor().getId());
        }

        return response;
    }

    // Đổi cả danh sách bài viết
    private List<ArticleResponse> toArticleResponseList(List<Article> articles) {
        List<ArticleResponse> result = new ArrayList<>();
        for (Article article : articles) {
            result.add(toArticleResponse(article));
        }
        return result;
    }

    // Ưu tiên tên bác sĩ đứng tên, không có thì lấy người tạo bài
    private String buildAuthorName(Article article) {
        if (article.getDoctorAuthor() != null) {
            return article.getDoctorAuthor().getFullName();
        }

        if (article.getCreatedBy() != null) {
            return article.getCreatedBy().getFullName();
        }

        return "Admin";
    }

    // Đổi bình luận sang dữ liệu hiển thị
    private ArticleCommentResponse toCommentResponse(ArticleComment comment) {
        ArticleCommentResponse response = new ArticleCommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setAuthorName(comment.getUser() != null ? comment.getUser().getFullName() : "Người dùng");
        response.setAuthorInitial(buildInitial(comment));
        return response;
    }

    // Chữ cái đầu của tên người bình luận, dùng cho ảnh đại diện tròn
    private String buildInitial(ArticleComment comment) {
        if (comment.getUser() == null) {
            return "U";
        }

        String firstName = comment.getUser().getFirstName();
        if (firstName == null || firstName.isEmpty()) {
            return "U";
        }

        return firstName.substring(0, 1).toUpperCase();
    }

    // Lấy bài viết còn hiệu lực, bài đã xóa mềm coi như không tồn tại
    private Article findActiveArticle(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article != null && ArticleStatus.DELETED.name().equals(article.getStatus())) {
            return null;
        }
        return article;
    }

    // Gán các trường người dùng nhập từ form vào bài viết
    private void applyRequestFields(Article article, CreateArticleRequest request) {
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());

        // Sinh đường dẫn thân thiện từ tiêu đề
        if (request.getTitle() != null) {
            article.setSlug(SlugUtil.generateSlug(request.getTitle()));
        }
    }

    // Không chọn trạng thái thì mặc định là bản nháp
    private String resolveStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ArticleStatus.DRAFT.name();
        }
        return status;
    }

    // Tìm bác sĩ đứng tên bài viết, không chọn thì trả về null
    private User findDoctor(Long doctorId) {
        if (doctorId == null) {
            return null;
        }

        User doctor = userRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            throw new BadRequestException("Không tìm thấy bác sĩ được chọn.");
        }

        // Chỉ tài khoản có vai trò DOCTOR mới được đứng tên bài viết
        if (!hasDoctorRole(doctor)) {
            throw new BadRequestException("Tài khoản được chọn không phải là bác sĩ.");
        }

        return doctor;
    }

    // Kiểm tra tài khoản có vai trò DOCTOR hay không
    private boolean hasDoctorRole(User user) {
        if (user.getUserRoles() == null) {
            return false;
        }

        for (UserRole userRole : user.getUserRoles()) {
            if ("DOCTOR".equals(userRole.getRole().getName())) {
                return true;
            }
        }
        return false;
    }

    // Tải ảnh bìa lên nếu người dùng có chọn, không có thì trả về null
    private String uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return imageUploadService.uploadImage(file);
    }
}
