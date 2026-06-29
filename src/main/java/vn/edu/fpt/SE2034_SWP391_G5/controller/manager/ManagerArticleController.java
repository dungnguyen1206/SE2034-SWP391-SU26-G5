package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService; // We need this or UserRepository to get doctors

import java.util.List;

@Controller
@RequestMapping("/manager/articles")
public class ManagerArticleController {

    @Autowired
    private ArticleService articleService;

    // Optional: @Autowired private DoctorService doctorService;
    // For now we will just assume we can pass doctors or we fetch users with DOCTOR role.

    @GetMapping
    public String listArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            Model model) {
        
        List<Article> articleList = articleService.getArticlesByFilters(keyword, category, status);
        model.addAttribute("articleList", articleList);
        // Retain filter state in view
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        return "manager/articles/list";
    }

    @GetMapping("/detail/{id}")
    public String detailArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "manager/articles/detail";
    }

    @GetMapping("/create")
    public String createArticleForm(Model model) {
        model.addAttribute("articleRequest", new CreateArticleRequest());
        // TODO: Pass actual doctor list from DB. Right now we use static HTML or need a DoctorService
        return "manager/articles/form";
    }

    @PostMapping("/create")
    public String createArticleSubmit(
            @ModelAttribute("articleRequest") CreateArticleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        User currentUser = userDetails != null ? userDetails.getUser() : null;
        articleService.createArticle(request, currentUser);
        return "redirect:/manager/articles";
    }

    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return "redirect:/manager/articles";
        }
        
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle(article.getTitle());
        request.setSummary(article.getSummary());
        request.setContent(article.getContent());
        request.setCategory(article.getCategory());
        request.setStatus(article.getStatus());
        if (article.getDoctorAuthor() != null) {
            request.setDoctorId(article.getDoctorAuthor().getId());
        }
        
        model.addAttribute("articleRequest", request);
        model.addAttribute("articleId", id);
        return "manager/articles/form";
    }

    @PostMapping("/edit/{id}")
    public String editArticleSubmit(
            @PathVariable Long id,
            @ModelAttribute("articleRequest") CreateArticleRequest request) {
        
        articleService.updateArticle(id, request);
        return "redirect:/manager/articles";
    }

    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/manager/articles";
    }
}
