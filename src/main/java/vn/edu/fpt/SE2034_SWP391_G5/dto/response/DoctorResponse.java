package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String degree;
    private Integer experienceYears;
    private String bio;
}
