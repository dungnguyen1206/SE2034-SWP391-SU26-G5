package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ArticleCategory;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ArticleStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;

import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class PublicArticleController {

    private static final int PAGE_SIZE = 9; // 3 cột x 3 hàng

    private final ArticleService articleService;

    // Hiển thị danh sách bài viết công khai
    @GetMapping
    public String listArticles(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        // Chỉ lấy bài viết đã xuất bản
        Page<Article> articlePage = articleService.getArticlesByFilters(null, category, ArticleStatus.PUBLISHED.name(), pageable);

        model.addAttribute("articleList", articlePage.getContent());
        model.addAttribute("currentPage", articlePage.getNumber());
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("category", category);
        model.addAttribute("categories", ArticleCategory.getAllDisplayNames());

        return "public/articles/list";
    }

    // Xem chi tiết bài viết
    @GetMapping("/{id}")
    public String detailArticle(@PathVariable Long id, Model model, Principal principal) {
        Article article = articleService.getArticleById(id);
        if (article == null || !ArticleStatus.PUBLISHED.name().equals(article.getStatus())) {
            return "redirect:/articles"; // Hoặc trang 404
        }

        // Tăng lượt xem
        articleService.incrementViewCount(id);
        
        // Lấy bình luận
        List<ArticleComment> comments = articleService.getCommentsByArticleId(id);
        long commentCount = articleService.getCommentCountByArticleId(id);

        // Lấy bài viết cùng chuyên mục
        List<Article> relatedArticles = articleService.getRelatedArticles(article.getCategory(), article.getId());

        // Kiểm tra đã đăng nhập chưa
        boolean isLoggedIn = principal != null;

        model.addAttribute("article", article);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("relatedArticles", relatedArticles);
        model.addAttribute("isLoggedIn", isLoggedIn);

        return "public/articles/detail";
    }

    // Gửi bình luận
    @PostMapping("/{id}/comment")
    public String postComment(@PathVariable Long id,
                              @RequestParam String content,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        // Phải đăng nhập
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            articleService.addComment(id, content, principal.getName());
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("commentError", e.getMessage());
        }

        return "redirect:/articles/" + id;
    }
}
