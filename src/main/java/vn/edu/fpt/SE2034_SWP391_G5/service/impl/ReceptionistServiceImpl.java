package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DashboardStatsResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service

public class ReceptionistServiceImpl implements ReceptionistService {

    private final UserRepository userRepository;
    private final AppointmentService appointmentService;

    public ReceptionistServiceImpl(UserRepository userRepository, AppointmentService appointmentService) {
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
    }

    //Find all receptionist
    public List<User> getAllReceptionist(String role) {
        return userRepository.findByRoleName(role);
    }

    //find all active receptionist
    public List<User> findByRoleNameAndStatus(String roleName, String status) {
        return userRepository.countByRoleNameAndStatus(roleName, status);
    }

    @Override
    public ReceptionistResponse getReceptionistByUsername(String email) {
        List<Object[]> result = userRepository.findReceptionistInfoByEmail(email);

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("Không tìm thấy nhân viên tiếp tân với email: " + email);
        }

        Object[] row = result.get(0);

        Long id = ((Number) row[0]).longValue();
        String fullName = (String) row[1];
        String avatarText = (String) row[2];

        return new ReceptionistResponse(id, fullName, avatarText);
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        List<AppointmentResponse> todayAppointments = getTodayAppointments();

        return DashboardStatsResponse.builder()
                .totalAppointmentsToday(todayAppointments.size())
                .checkedInToday(countCheckedIn(todayAppointments))
                .waitingQueue(countByStatus(todayAppointments, "WAITING"))
                .examiningQueue(countByStatus(todayAppointments, "EXAMINING"))
                .paidInvoices(0)
                .unpaidInvoices(0)
                .build();
    }

    @Override
    public List<AppointmentResponse> getTodayAppointments() {
        LocalDate today = LocalDate.now();

        return appointmentService.getAppointmentListForReceptionist()
                .stream()
                .filter(appointment -> today.equals(appointment.getBookingDate()))
                .toList();
    }

    @Override
    public List<AppointmentResponse> searchTodayAppointments(List<AppointmentResponse> appointments, String search) {
        List<AppointmentResponse> results = new ArrayList<>();
        String keyword = search.trim().toLowerCase();
        for(AppointmentResponse appointment : appointments){
            boolean matches = containsIgnoreCase(appointment.getPatientFullName(), keyword) || containsIgnoreCase(appointment.getPatientPhone(), keyword);
            if(matches){
                results.add(appointment);
            }
        }
        return results;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private int countByStatus(List<AppointmentResponse> appointments, String status) {
        int count = 0;
        for(AppointmentResponse appointment : appointments){
            if(status.equals(appointment.getStatus())){
                count++;
            }
        }
        return count;
    }

    private int countCheckedIn(List<AppointmentResponse> appointments){
        int count = 0;
        for(AppointmentResponse appointment : appointments){
            if("WAITING".equals(appointment.getStatus()) || "EXAMINING".equals(appointment.getStatus()) || "COMPLETED".equals(appointment.getStatus())){
                count++;
            }
        }
        return count;
    }
}