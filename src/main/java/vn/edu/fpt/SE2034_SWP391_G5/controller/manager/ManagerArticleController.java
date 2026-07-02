package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService; // We need this or UserRepository to get doctors

import java.util.List;

@Controller
@RequestMapping("/manager/articles")
public class ManagerArticleController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Article> articlePage = articleService.getArticlesByFilters(keyword, category, status, pageable);
        
        model.addAttribute("articleList", articlePage.getContent());
        model.addAttribute("currentPage", articlePage.getNumber());
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("totalItems", articlePage.getTotalElements());
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
        List<User> doctors = userRepository.findByRoleName("DOCTOR");
        model.addAttribute("doctors", doctors);
        return "manager/articles/form";
    }

    @PostMapping("/create")
    public String createArticleSubmit(
            @ModelAttribute("articleRequest") CreateArticleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        User currentUser = userDetails != null ? userDetails.getUser() : null;
        articleService.createArticle(request, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo bài viết thành công!");
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
        
        List<User> doctors = userRepository.findByRoleName("DOCTOR");
        model.addAttribute("doctors", doctors);
        
        return "manager/articles/form";
    }

    @PostMapping("/edit/{id}")
    public String editArticleSubmit(
            @PathVariable Long id,
            @ModelAttribute("articleRequest") CreateArticleRequest request,
            RedirectAttributes redirectAttributes) {
        
        articleService.updateArticle(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài viết thành công!");
        return "redirect:/manager/articles";
    }

    @PostMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công!");
        return "redirect:/manager/articles";
    }
}
