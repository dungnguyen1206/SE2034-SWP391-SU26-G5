package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class DoctorScheduleWeekResponse {
    private String dayOfWeekLabel;
    private int dayOfMonth;
    private String dayAndMonth;
    private LocalDate date;
    private List<ShiftDetail> shifts;

    @Getter
    @Setter
    @Builder
    public static class ShiftDetail {
        private Long id;
        private String shift;
        private String shiftClass;
        private String badgeText;
        private String title;
        private String timeRange;
        private String roomName;
    }
}
