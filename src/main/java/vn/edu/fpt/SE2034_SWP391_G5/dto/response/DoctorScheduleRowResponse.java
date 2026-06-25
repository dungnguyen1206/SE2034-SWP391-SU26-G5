package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleRowResponse {
    private DoctorResponse doctor;
    private Map<LocalDate, DoctorScheduleResponse> scheduleByDate;
    public DoctorScheduleRowResponse(DoctorResponse doctor) {
        this.doctor = doctor;
    }
}
