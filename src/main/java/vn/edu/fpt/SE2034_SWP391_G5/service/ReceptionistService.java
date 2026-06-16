package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DashboardStatsResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;


import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface ReceptionistService {
    List<User> getAllReceptionist(String role);

    List<User> findByRoleNameAndStatus(String roleName, String status);

    ReceptionistResponse getReceptionistByUsername(String username);

    DashboardStatsResponse getDashboardStats();

    List<AppointmentResponse> getTodayAppointments();

    List<AppointmentResponse> searchTodayAppointments(List<AppointmentResponse> appointments, String search);
}


