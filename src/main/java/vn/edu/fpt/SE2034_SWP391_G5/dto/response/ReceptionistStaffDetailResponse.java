package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionistStaffDetailResponse {
    private Long id;
    private String staffCode;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String avatar;

    private String roleName;
    private String roleLabel;

    private String accountStatus;
    private String workingStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String bio;
}
