package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class PublicArticleController {

    private static final int PAGE_SIZE = 9; // 3 columns grid, 3 rows is nice

    @Autowired
    private ArticleService articleService;

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
    public String detailArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null || !"PUBLISHED".equals(article.getStatus())) {
            return "redirect:/articles"; // Or a 404 page
        }
        
        // TODO: Implement view count increment
        
        model.addAttribute("article", article);
        return "public/articles/detail";
    }
}
