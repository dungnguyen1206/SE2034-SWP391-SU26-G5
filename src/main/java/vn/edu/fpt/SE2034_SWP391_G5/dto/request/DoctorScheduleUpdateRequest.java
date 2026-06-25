package vn.edu.fpt.SE2034_SWP391_G5.dto.request;


import lombok.*;
import vn.edu.fpt.SE2034_SWP391_G5.enums.DoctorScheduleStatus;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorScheduleUpdateRequest {
        private Long scheduleId;
        private LocalDate workDate;
        private String scheduleShift;
        private Long roomId;
        private Integer maxCapacity;
        private String note;
        private String status;
    }
