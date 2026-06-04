package vn.edu.fpt.SE2034_SWP391_G5.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserRoleId implements Serializable {

    private Long userId;

    private Integer roleId;
}
