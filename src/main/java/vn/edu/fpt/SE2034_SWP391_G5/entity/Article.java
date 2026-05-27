package vn.edu.fpt.SE2034_SWP391_G5.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String slug;

    private String summary;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private String category;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "doctor_author_id")
    private Long doctorAuthorId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "view_count")
    private Integer viewCount;

    private String status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
