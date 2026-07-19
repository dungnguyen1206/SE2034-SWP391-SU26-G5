package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.ArticleComment;

import java.util.List;

@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    List<ArticleComment> findByArticleIdAndParentIsNullOrderByCreatedAtDesc(Long articleId);

    long countByArticleId(Long articleId);
}
