package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.WalkInBookingRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistWalkInService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReceptionistWalkInServiceImpl implements ReceptionistWalkInService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MedicalServiceRepository medicalServiceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    @Override
    public Object searchPatientByPhone(String phone) {
        Optional<User> patientOpt = userRepository.findByPhone(phone);
        if (patientOpt.isPresent()) {
            User p = patientOpt.get();
            // check if user has PATIENT role
            boolean isPatient = p.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("PATIENT"));
            if (!isPatient) {
                return Map.of("error", "Số điện thoại này đã được sử dụng cho một tài khoản nhân viên.");
            }
            return Map.of(
                    "found", true,
                    "firstName", p.getFirstName() == null ? "" : p.getFirstName(),
                    "lastName", p.getLastName() == null ? "" : p.getLastName(),
                    "gender", p.getGender() == null ? "" : p.getGender()
            );
        }
        return Map.of("found", false);
    }

    @Override
    public List<Map<String, Object>> getAvailableSlots(Integer departmentId, LocalDate date) {
        LocalTime currentTime = LocalDate.now().equals(date) ? LocalTime.now() : LocalTime.MIN;
        List<TimeSlot> slots = timeSlotRepository.findAvailableSlotsByDepartmentAndDate(departmentId, date, currentTime);

        Map<LocalTime, TimeSlot> distinctSlots = new TreeMap<>();
        for (TimeSlot ts : slots) {
            distinctSlots.putIfAbsent(ts.getStartTime(), ts);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (TimeSlot ts : distinctSlots.values()) {
            result.add(Map.<String, Object>of(
                    "id", ts.getId(),
                    "time", ts.getStartTime().toString() + " - " + ts.getEndTime().toString()
            ));
        }
        return result;
    }

    @Override
    @Transactional
    public void createWalkInAppointment(WalkInBookingRequest request) {
        // 1. Get or Create Patient
        User patient = userRepository.findByPhone(request.getPhone()).orElse(null);
        if (patient == null) {
            patient = new User();
            patient.setUsername(request.getPhone());
            patient.setPhone(request.getPhone());
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setGender(request.getGender());
            patient.setPasswordHash(passwordEncoder.encode("Walkin@123"));
            patient.setStatus("ACTIVE");
            patient.setEmailVerified(true);
            patient.setCreatedAt(LocalDateTime.now());
            patient.setUpdatedAt(LocalDateTime.now());
            
            patient = userRepository.save(patient);
            
            Role patientRole = roleRepository.findByName("PATIENT").orElseThrow(() -> new RuntimeException("Role PATIENT not found"));
            UserRole userRole = new UserRole();
            userRole.setUser(patient);
            userRole.setRole(patientRole);
            userRoleRepository.save(userRole);
            
            System.out.println("Gửi SMS đến " + request.getPhone() + " với mật khẩu: Walkin@123");
        }

        // 2. Get Department's initial Medical Service
        List<MedicalService> services = medicalServiceRepository.findByDepartmentIdAndStatus(request.getDepartmentId(), "ACTIVE");
        if (services.isEmpty()) {
            throw new RuntimeException("Không tìm thấy dịch vụ khám cho khoa này.");
        }
        MedicalService initialService = services.stream()
                .filter(s -> s.getName().toLowerCase().contains("khám"))
                .findFirst()
                .orElse(services.get(0));

        // 3. Validate and Get Slot
        TimeSlot selectedSlot = timeSlotRepository.findByIdWithSchedule(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Khung giờ không tồn tại."));

        if (!selectedSlot.getSchedule().getWorkDate().equals(request.getBookingDate())) {
            throw new RuntimeException("Khung giờ không khớp với ngày khám.");
        }

        if (LocalDate.now().equals(request.getBookingDate()) && selectedSlot.getStartTime().isBefore(LocalTime.now())) {
            throw new RuntimeException("Không thể đặt lịch vào khung giờ trong quá khứ.");
        }

        if (selectedSlot.getBookedCapacity() >= selectedSlot.getMaxCapacity()) {
            throw new RuntimeException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác.");
        }

        // Increase booked capacity
        selectedSlot.setBookedCapacity(selectedSlot.getBookedCapacity() + 1);
        timeSlotRepository.save(selectedSlot);

        User selectedDoctor = selectedSlot.getSchedule().getDoctor();

        // 4. Create Appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentCode("WI-" + System.currentTimeMillis());
        appointment.setPatient(patient);
        appointment.setDoctor(selectedDoctor);
        appointment.setService(initialService);
        appointment.setSlot(selectedSlot);
        appointment.setBookingDate(request.getBookingDate());
        appointment.setStatus("WAITING"); // Walk-in is ready to examine
        appointment.setCheckInTime(LocalDateTime.now());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        // 5. Create Invoice
        Invoice invoice = new Invoice();
        String invCode = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + String.format("%04d", new Random().nextInt(10000));
        invoice.setInvoiceCode(invCode);
        invoice.setAppointment(appointment);
        invoice.setTotalAmount(initialService.getReferencePrice());
        invoice.setPaymentMethod("CASH");
        invoice.setPaymentStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice = invoiceRepository.save(invoice);

        // 6. Create InvoiceItem
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setService(initialService);
        item.setItemName(initialService.getName());
        item.setPriceApplied(initialService.getReferencePrice());
        item.setQuantity(1);
        item.setLineTotal(initialService.getReferencePrice());
        invoiceItemRepository.save(item);
    }
}
