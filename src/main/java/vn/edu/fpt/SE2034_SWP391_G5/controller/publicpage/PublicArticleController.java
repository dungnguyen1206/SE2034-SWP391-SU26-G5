package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class PublicArticleController {

    private static final int PAGE_SIZE = 9; // 3 columns grid, 3 rows is nice

    private final ArticleService articleService;

    // The categories currently hardcoded in manager form
    private final List<String> CATEGORIES = Arrays.asList("Tim mạch", "Nhi khoa", "Da liễu", "Sức khỏe", "Dinh dưỡng");

    @GetMapping
    public String listArticles(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        // Only fetch PUBLISHED articles
        Page<Article> articlePage = articleService.getArticlesByFilters(null, category, "PUBLISHED", pageable);

        model.addAttribute("articleList", articlePage.getContent());
        model.addAttribute("currentPage", articlePage.getNumber());
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("category", category);
        model.addAttribute("categories", CATEGORIES);

        return "public/articles/list";
    }

    @GetMapping("/{id}")
    public String detailArticle(@PathVariable Long id, Model model, Principal principal) {
        Article article = articleService.getArticleById(id);
        if (article == null || !"PUBLISHED".equals(article.getStatus())) {
            return "redirect:/articles"; // Or a 404 page
        }

        // Increment view count
        articleService.incrementViewCount(id);
        
        // Load comments (only top-level, replies are loaded via entity relationship)
        List<ArticleComment> comments = articleService.getCommentsByArticleId(id);
        long commentCount = articleService.getCommentCountByArticleId(id);

        // Load related articles (same category, excluding current article)
        List<Article> relatedArticles = articleService.getRelatedArticles(article.getCategory(), article.getId());

        // Check if user is logged in
        boolean isLoggedIn = principal != null;

        model.addAttribute("article", article);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("relatedArticles", relatedArticles);
        model.addAttribute("isLoggedIn", isLoggedIn);

        return "public/articles/detail";
    }

    @PostMapping("/{id}/comment")
    public String postComment(@PathVariable Long id,
                              @RequestParam String content,
                              @RequestParam(required = false) Long parentId,
                              Principal principal) {
        // Must be logged in
        if (principal == null) {
            return "redirect:/login";
        }

        articleService.addComment(id, content, parentId, principal.getName());

        return "redirect:/articles/" + id;
    }
}
