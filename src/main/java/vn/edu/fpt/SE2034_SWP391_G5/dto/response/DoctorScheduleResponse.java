package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleResponse {
    private Long id;
    private String doctorName;
    private String shift;
    private String roomName;
    private LocalDate workDate;
    private int maxSlots;
    private String status;
    private String note;
}