package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;

import java.util.List;

@Controller
@RequestMapping("/manager/articles")
public class ManagerArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public String listArticles(Model model) {
        List<Article> articleList = articleService.getAllArticles();
        model.addAttribute("articleList", articleList);
        return "manager/articles/list";
    }

    @GetMapping("/detail/{id}")
    public String detailArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "manager/articles/detail";
    }

    @GetMapping("/create")
    public String createArticleForm() {
        return "manager/articles/form";
    }

    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        // Fetch article and add to model to populate the form (to be implemented fully later)
        return "manager/articles/form";
    }
}
