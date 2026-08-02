package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateArticleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ArticleResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ArticleCategory;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.service.UserService;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

// Quản lý bài viết phía Hospital Manager
@Controller
@RequestMapping("/manager/articles")
@RequiredArgsConstructor
public class ManagerArticleController {

    private static final int PAGE_SIZE = 10;

    private final ArticleService articleService;
    private final UserService userService;

    // Hiển thị danh sách bài viết (có lọc, phân trang)
    @GetMapping
    public String listArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        // Giao diện đếm trang từ 1, Spring Data JPA đếm từ 0
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), PAGE_SIZE);
        Page<ArticleResponse> articlePage = articleService.getArticlesByFilters(keyword, category, status, pageable);

        int totalPages = articlePage.getTotalPages();

        model.addAttribute("articleList", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", articlePage.getTotalElements());

        // Dữ liệu cho thanh phân trang, tính sẵn để template chỉ việc hiển thị
        model.addAttribute("middlePages", buildMiddlePages(page, totalPages));
        model.addAttribute("showLeftDots", page > 3);
        model.addAttribute("showRightDots", page < totalPages - 2);

        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        model.addAttribute("categories", ArticleCategory.getAllDisplayNames());
        return "manager/articles/list";
    }

    // Các số trang hiện ở giữa thanh phân trang.
    // Trang đầu và trang cuối luôn hiện riêng nên không tính vào đây.
    private List<Integer> buildMiddlePages(int currentPage, int totalPages) {
        List<Integer> pages = new ArrayList<>();

        // Lấy 2 trang trước và 2 trang sau trang hiện tại, không vượt ra ngoài khoảng giữa
        int from = Math.max(currentPage - 2, 2);
        int to = Math.min(currentPage + 2, totalPages - 1);

        for (int page = from; page <= to; page++) {
            pages.add(page);
        }
        return pages;
    }

    // Xem chi tiết bài viết
    @GetMapping("/detail/{id}")
    public String detailArticle(@PathVariable Long id, Model model) {
        ArticleResponse article = articleService.getArticleById(id);

        // Bài không tồn tại hoặc đã xóa thì quay lại danh sách
        if (article == null) {
            return "redirect:/manager/articles";
        }

        model.addAttribute("article", article);
        return "manager/articles/detail";
    }

    // Hiển thị form tạo bài viết
    @GetMapping("/create")
    public String createArticleForm(Model model) {
        model.addAttribute("articleRequest", new CreateArticleRequest());
        addFormOptions(model);
        return "manager/articles/form";
    }

    // Xử lý tạo bài viết mới
    @PostMapping("/create")
    public String createArticleSubmit(
            @Valid @ModelAttribute("articleRequest") CreateArticleRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormOptions(model);
            return "manager/articles/form";
        }

        User currentUser = userDetails != null ? userDetails.getUser() : null;
        try {
            articleService.createArticle(request, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo bài viết thành công!");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/articles";
    }

    // Hiển thị form sửa bài viết
    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        ArticleResponse article = articleService.getArticleById(id);
        if (article == null) {
            return "redirect:/manager/articles";
        }

        // Đổ dữ liệu bài viết hiện tại vào form
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle(article.getTitle());
        request.setSummary(article.getSummary());
        request.setContent(article.getContent());
        request.setCategory(article.getCategory());
        request.setStatus(article.getStatus());
        request.setDoctorId(article.getDoctorId());
        request.setCurrentThumbnailUrl(article.getThumbnailUrl());

        model.addAttribute("articleRequest", request);
        model.addAttribute("articleId", id);
        addFormOptions(model);

        return "manager/articles/form";
    }

    // Xử lý cập nhật bài viết
    @PostMapping("/edit/{id}")
    public String editArticleSubmit(
            @PathVariable Long id,
            @Valid @ModelAttribute("articleRequest") CreateArticleRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("articleId", id);
            addFormOptions(model);
            return "manager/articles/form";
        }

        try {
            articleService.updateArticle(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài viết thành công!");
        } catch (BadRequestException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/articles";
    }

    // Xóa bài viết
    @PostMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/articles";
    }

    // Nạp danh sách bác sĩ và chuyên mục cho các ô chọn trong form
    private void addFormOptions(Model model) {
        model.addAttribute("doctors", userService.getDoctors());
        model.addAttribute("categories", ArticleCategory.getAllDisplayNames());
    }
}
