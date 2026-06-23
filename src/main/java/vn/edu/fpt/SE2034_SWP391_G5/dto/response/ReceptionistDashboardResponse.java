package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReceptionistDashboardResponse {
    private long totalAppointmentsToday;
    private long checkedInToday;
    private long waitingQueue;
    private long examiningQueue;
    private long paidInvoices;
    private long unpaidInvoices;
}     
