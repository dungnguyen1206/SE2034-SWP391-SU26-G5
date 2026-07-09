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
            // Random password or something default
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
            
            // TODO: send SMS logic goes here (mocked for now)
            System.out.println("Gửi SMS đến " + request.getPhone() + " với mật khẩu: Walkin@123");
        }

        // 2. Get Department's initial Medical Service
        // Usually the one with lowest price or a specific name like "Khám bệnh"
        List<MedicalService> services = medicalServiceRepository.findByDepartmentIdAndStatus(request.getDepartmentId(), "ACTIVE");
        if (services.isEmpty()) {
            throw new RuntimeException("Không tìm thấy dịch vụ khám cho khoa này.");
        }
        // Just pick the first one, or the one with "Khám" in name if possible
        MedicalService initialService = services.stream()
                .filter(s -> s.getName().toLowerCase().contains("khám"))
                .findFirst()
                .orElse(services.get(0));

        // 3. Auto Assign Doctor and Slot for TODAY
        LocalDate today = LocalDate.now();
        // find all active doctors in this department
        List<User> doctors = userRepository.findActiveDoctorsByDepartmentId(request.getDepartmentId());
        if (doctors.isEmpty()) {
            throw new RuntimeException("Không có bác sĩ nào trong khoa này.");
        }

        User selectedDoctor = null;
        TimeSlot selectedSlot = null;
        long minAppointments = Long.MAX_VALUE;

        for (User doctor : doctors) {
            // Count today's appointments
            long count = appointmentRepository.countByDoctorIdAndBookingDate(doctor.getId(), today);
            
            // Find available slots for this doctor today
            // We need a custom query or just fetch schedule
            List<TimeSlot> availableSlots = timeSlotRepository.findAvailableSlotsByDoctorAndDate(doctor.getId(), today);
            
            if (!availableSlots.isEmpty() && count < minAppointments) {
                minAppointments = count;
                selectedDoctor = doctor;
                selectedSlot = availableSlots.get(0); // pick earliest
            }
        }

        if (selectedDoctor == null || selectedSlot == null) {
            throw new RuntimeException("Hệ thống không tìm thấy lịch trống cho bác sĩ nào trong ngày hôm nay ở khoa này.");
        }

        // 4. Create Appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentCode("WI-" + System.currentTimeMillis());
        appointment.setPatient(patient);
        appointment.setDoctor(selectedDoctor);
        appointment.setService(initialService);
        appointment.setSlot(selectedSlot);
        appointment.setBookingDate(today);
        appointment.setStatus("WAITING"); // Walk-in is ready to examine
        appointment.setCheckInTime(LocalDateTime.now());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        // 5. Create Invoice
        Invoice invoice = new Invoice();
        String invCode = "INV-" + today.format(DateTimeFormatter.ofPattern("yyyyMM")) + String.format("%04d", new Random().nextInt(10000));
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
