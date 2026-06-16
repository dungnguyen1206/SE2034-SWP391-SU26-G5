package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DashboardStatsResponse {
    private int totalAppointmentsToday;
    private int checkedInToday;
    private int waitingQueue;
    private int examiningQueue;
    private int paidInvoices;
    private int unpaidInvoices;
}     
