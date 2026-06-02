package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String degree;
    private String licenseNumber;
    private String bio;
    private String avatar;
    private Integer experienceYears;
    private String departmentName;
    private Integer departmentId;
    private String doctorStatus;
}