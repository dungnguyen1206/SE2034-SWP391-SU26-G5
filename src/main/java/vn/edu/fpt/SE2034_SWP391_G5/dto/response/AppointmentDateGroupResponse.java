package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppointmentDateGroupResponse {

    private LocalDate bookingDate;
    private boolean today;
    private int totalAppointments;
    private List<AppointmentResponse> appointments;
}