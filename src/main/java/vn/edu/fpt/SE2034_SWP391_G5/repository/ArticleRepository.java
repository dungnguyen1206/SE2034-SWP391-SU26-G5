package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    @Query("SELECT a FROM Article a WHERE " +
           "a.status != 'DELETED' AND " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR a.category = :category) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status) " +
           "ORDER BY a.createdAt DESC")
    List<Article> findByFilters(@Param("keyword") String keyword, 
                                @Param("category") String category, 
                                @Param("status") String status);

    @Query("SELECT a FROM Article a WHERE " +
           "a.status != 'DELETED' AND " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR a.category = :category) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status) " +
           "ORDER BY a.createdAt DESC")
    Page<Article> findByFiltersPageable(@Param("keyword") String keyword,
                                       @Param("category") String category,
                                       @Param("status") String status,
                                       Pageable pageable);

    List<Article> findTop3ByCategoryAndIdNotAndStatusOrderByCreatedAtDesc(String category, Long id, String status);
}
