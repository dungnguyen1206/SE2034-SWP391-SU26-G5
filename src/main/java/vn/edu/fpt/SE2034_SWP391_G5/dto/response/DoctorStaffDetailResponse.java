package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStaffDetailResponse {
    private Long id;
    private String staffCode;
    private String fullName;
    private String email;
    private String phone;
    private String roleName;
    private String roleLabel;
    private String accountStatus;
    private String workingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String avatar;
    private String departmentName;
    private String licenseNumber;
    private String degree;
    private Integer experienceYears;
    private String bio;
}
