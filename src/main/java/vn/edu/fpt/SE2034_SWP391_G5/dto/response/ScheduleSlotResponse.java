package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ScheduleSlotResponse {

    private Long scheduleId;
    private LocalDate workDate;
    private String shift;       // MORNING | AFTERNOON
    private String shiftLabel;  // Ca sáng | Ca chiều
    private String roomNumber;

    private List<SlotInfo> slots;

    @Getter
    @Setter
    @Builder
    public static class SlotInfo {
        private Long slotId;
        private LocalTime startTime;
        private LocalTime endTime;
        private int bookedCapacity;
        private int maxCapacity;
        private String status;   // AVAILABLE | FULL
        private boolean available;
    }
}
