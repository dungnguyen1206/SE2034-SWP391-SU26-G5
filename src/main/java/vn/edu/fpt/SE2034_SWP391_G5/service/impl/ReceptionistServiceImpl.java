package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistDashboardResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.time.LocalDate;
import java.util.List;

@Service

public class ReceptionistServiceImpl implements ReceptionistService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;

    public ReceptionistServiceImpl(UserRepository userRepository, AppointmentService appointmentService, AppointmentRepository appointmentRepository, InvoiceRepository invoiceRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
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
    public ReceptionistDashboardResponse getTodayDashboardStatistics() {
        LocalDate today = LocalDate.now();

        return ReceptionistDashboardResponse.builder()
                .totalAppointmentsToday(appointmentRepository.countTodayAppointments(today))
                .checkedInToday(appointmentRepository.countTodayCheckedInAppointments(today))
                .waitingQueue(appointmentRepository.countTodayWaitingAppointments(today))
                .examiningQueue(appointmentRepository.countTodayExaminingAppointments(today))
                .paidInvoices(invoiceRepository.countTodayPaidInvoices(today))
                .unpaidInvoices(invoiceRepository.countTodayUnpaidInvoices(today))
                .build();
    }

    @Override
    public List<AppointmentResponse> getTodayAppointmentsForDashboard(String search) {
        LocalDate today = LocalDate.now();
        String keyword = searchText(search);

        return appointmentRepository.findTodayAppointmentsForDashboard(today, keyword).stream().map(this::toDashboardAppointmentResponse).toList();
    }

    // Map dữ liệu cho bảng "Lịch hẹn hôm nay" trên Dashboard.
// Chỉ map các field đang hiển thị trên màn hình, không map địa chỉ, medical record, invoice.
    private AppointmentResponse toDashboardAppointmentResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                // Dùng cho nút Chi tiết
                .id(appointment.getId())

                // Mã lịch hẹn
                .appointmentCode(appointment.getAppointmentCode())

                // Slot
                .slotStartTime(appointment.getSlot().getStartTime())
                .slotEndTime(appointment.getSlot().getEndTime())

                // Bệnh nhân
                .patientFullName(getUserFullName(appointment.getPatient()))
                .patientPhone(appointment.getPatient().getPhone())

                // Khoa
                .departmentName(appointment.getService().getDepartment().getName())

                // Bác sĩ
                .doctorFullName(getUserFullName(appointment.getDoctor()))

                // Phòng
                .roomNumber(appointment.getSlot().getSchedule().getRoom().getRoomNumber())

                // Trạng thái
                .status(appointment.getStatus())

                // Ngày khám
                .bookingDate(appointment.getBookingDate())

                .build();
    }

    // Chuẩn hóa ô tìm kiếm.
// Nếu người dùng không nhập gì thì trả về null để query bỏ qua điều kiện search.
    private String searchText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    // Ghép họ tên theo chuẩn tiếng Việt: Họ + Tên đệm + Tên.
    private String getUserFullName(User user) {
        if (user == null) {
            return "";
        }

        String firstName = user.getFirstName() == null ? "" : user.getFirstName();
        String middleName = user.getMiddleName() == null ? "" : user.getMiddleName();
        String lastName = user.getLastName() == null ? "" : user.getLastName();

        return (lastName + " " + middleName + " " + firstName)
                .trim()
                .replaceAll("\\s+", " ");
    }


}