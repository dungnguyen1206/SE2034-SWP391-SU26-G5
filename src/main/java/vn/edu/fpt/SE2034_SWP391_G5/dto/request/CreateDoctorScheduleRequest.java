package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorScheduleRequest {

        @NotNull(message = "Khoa không được bỏ trống")
        private Integer departmentId;

        @NotNull(message = "Bác sĩ không được bỏ trống")
        private Long doctorId;

        @NotNull(message = "Ngày làm việc không được bỏ trống")
        private LocalDate workDate;

        @NotNull(message = "Ca làm việc không được bỏ trống")
        private ScheduleShift scheduleShift;

        @NotNull(message = "Phải chọn phòng khám bệnh")
        private Long roomId;

        @NotNull(message = "Số lượng bệnh nhân không được bỏ trống")
        private Integer maxCapacity;

        private String note;


}
