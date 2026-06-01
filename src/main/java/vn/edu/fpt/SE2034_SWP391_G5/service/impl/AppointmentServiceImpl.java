package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentResponse> getAppointmentListForReceptionist() {
        return appointmentRepository.findAllForReceptionistList();
    }

    @Override
    public long countByStatus(List<AppointmentResponse> appointments, String status) {
        long count = 0;
        for (AppointmentResponse appointment : appointments) {
            if (status.equals(appointment.getStatus())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<AppointmentResponse> filterAppointments(List<AppointmentResponse> appointments, String search, String status) {
        List<AppointmentResponse> result = new ArrayList<>();

        String keyword = "";
        if (search != null) {
            keyword = search.trim().toLowerCase();
        }

        String selectedStatus = "";
        if (status != null) {
            selectedStatus = status.trim();
        }

        for (AppointmentResponse appointment : appointments) {
            boolean matchesSearch = true;
            boolean matchesStatus = true;
            if (!keyword.isEmpty()) {
                matchesSearch =
                        containsIgnoreCase(appointment.getAppointmentCode(), keyword)
                                || containsIgnoreCase(appointment.getPatientName(), keyword)
                                || containsIgnoreCase(appointment.getPatientPhone(), keyword)
                                || containsIgnoreCase(appointment.getDoctorName(), keyword)
                                || containsIgnoreCase(appointment.getDepartmentName(), keyword)
                                || containsIgnoreCase(appointment.getRoomNumber(), keyword);
            }
            if (!selectedStatus.isEmpty()) {
                matchesStatus = selectedStatus.equals(appointment.getStatus());
            }
            if (matchesSearch && matchesStatus) {
                result.add(appointment);
            }
        }

        return result;
    }

    @Override
    public AppointmentPrintResponse getCheckInTicket(Long appointmentId) {
        AppointmentPrintResponse ticket = appointmentRepository.findCheckInTicketById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lịch hẹn"));

        if (ticket.getCheckInTime() != null) {
            Long queueNumber = appointmentRepository.countQueueNumberForTicket(
                    ticket.getId(),
                    ticket.getBookingDate(),
                    ticket.getCheckInTime()
            );

            ticket.setQueueNumber(queueNumber);
        }

        return ticket;
    }

    @Override
    @Transactional
    public void confirmCheckInAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw new BadRequestException("Chỉ lịch hẹn đã xác nhận mới được check-in");
        }

        if (!LocalDate.now().equals(appointment.getBookingDate())) {
            throw new BadRequestException("Chỉ được check-in lịch hẹn trong ngày khám");
        }

        LocalDateTime now = LocalDateTime.now();

        appointment.setStatus("WAITING");
        appointment.setCheckInTime(now);
        appointment.setUpdatedAt(now);

        appointmentRepository.save(appointment);
    }

    private boolean containsIgnoreCase(String value, String keyword){
        if(value == null){
            return false;
        }
        return value.toLowerCase().contains(keyword);
    }

}