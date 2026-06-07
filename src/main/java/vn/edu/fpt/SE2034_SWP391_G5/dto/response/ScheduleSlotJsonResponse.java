package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.util.List;

/**
 * DTO dùng để serialize sang JSON cho Thymeleaf inline JavaScript.
 * Tất cả date/time đều là String để tránh vấn đề serialize LocalDate/LocalTime.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotJsonResponse {

    private Long scheduleId;
    private String workDate;    // "yyyy-MM-dd"
    private String shift;       // MORNING | AFTERNOON
    private String shiftLabel;  // Ca sáng | Ca chiều
    private String roomNumber;

    private List<SlotInfo> slots;

    @Getter
    @Setter
    @Builder
    public static class SlotInfo {
        private Long slotId;
        private String startTime;   // "HH:mm"
        private String endTime;     // "HH:mm"
        private int bookedCapacity;
        private int maxCapacity;
        private String status;
        private boolean available;
    }
}
