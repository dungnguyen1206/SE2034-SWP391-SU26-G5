package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import lombok.*;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleImportRow {
    private int rowNumber;

    private Long doctorId;

    private LocalDate workDate;

    private ScheduleShift scheduleShift;

    private Long roomId;

    private Integer maxCapacity;

    private String note;
}
