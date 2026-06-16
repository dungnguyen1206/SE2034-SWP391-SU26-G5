package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorOnDutyResponse {
    private Long doctorId;
    private String doctorName;
    private String departmentName;
    private String status;
    private String roomNumber;
    private String shift;

}
