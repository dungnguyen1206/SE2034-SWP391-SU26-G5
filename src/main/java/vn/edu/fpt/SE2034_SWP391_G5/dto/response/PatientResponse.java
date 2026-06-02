package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PatientResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String gender;
    private String avatar;
    private LocalDate dateOfBirth;
    private String bloodType;
    private String status;

    private String addressLine;
    private String provinceName;

    private LocalDateTime createdAt;
}
