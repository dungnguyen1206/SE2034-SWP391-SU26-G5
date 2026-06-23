package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistDashboardResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;


import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface ReceptionistService {
    List<User> getAllReceptionist(String role);

    List<User> findByRoleNameAndStatus(String roleName, String status);

    ReceptionistResponse getReceptionistByUsername(String username);

    // Lấy các số liệu thống kê trên màn hình Dashboard của receptionist.
    ReceptionistDashboardResponse getTodayDashboardStatistics();

    // Lấy danh sách lịch hẹn hôm nay trên Dashboard, có tìm kiếm theo tên hoặc SĐT bệnh nhân.
    List<AppointmentResponse> getTodayAppointmentsForDashboard(String search);
}


