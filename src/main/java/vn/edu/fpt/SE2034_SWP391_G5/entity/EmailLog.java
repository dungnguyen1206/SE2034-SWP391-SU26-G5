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
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_type")
    private String emailType;

    private String recipient;

    private String subject;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    private String status;

    @Column(name = "error_message", columnDefinition = "NVARCHAR(MAX)")
    private String errorMessage;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "related_entity_type")
    private String relatedEntityType;
}
