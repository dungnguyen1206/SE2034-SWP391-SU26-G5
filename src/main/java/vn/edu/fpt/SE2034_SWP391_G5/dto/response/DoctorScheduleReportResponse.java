package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleReportResponse {
    private List<DoctorScheduleWeekResponse> weekSchedule;
    private String totalHoursStr;
    private String shiftCountStr;
    private String performance;
    private String prevWeekDate;
    private String nextWeekDate;
}
