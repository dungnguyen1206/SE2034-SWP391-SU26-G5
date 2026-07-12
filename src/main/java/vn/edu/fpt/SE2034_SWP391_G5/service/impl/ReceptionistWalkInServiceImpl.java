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
        if (phone == null || phone.trim().isEmpty()) {
            return Map.of("error", "Vui lòng nhập số điện thoại.");
        }
        phone = phone.trim();
        
        if (!phone.matches("\\d+")) {
            return Map.of("error", "Số điện thoại chỉ được chứa chữ số, không chứa chữ cái hoặc ký tự đặc biệt.");
        }
        
        if (!phone.startsWith("0")) {
            return Map.of("error", "Số điện thoại phải bắt đầu bằng số 0.");
        }
        
        if (phone.length() != 10) {
            return Map.of("error", "Số điện thoại phải bao gồm đúng 10 chữ số (bạn đang nhập " + phone.length() + " số).");
        }

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
        List<TimeSlot> slots = timeSlotRepository.findSlotsByDepartmentAndDate(departmentId, date);

        LocalTime currentTime = LocalDate.now().equals(date) ? LocalTime.now() : LocalTime.MIN;

        // Group slots by startTime to merge multiple doctors' slots at the same time
        Map<LocalTime, List<TimeSlot>> slotsByTime = new TreeMap<>();
        for (TimeSlot ts : slots) {
            slotsByTime.computeIfAbsent(ts.getStartTime(), k -> new ArrayList<>()).add(ts);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalTime, List<TimeSlot>> entry : slotsByTime.entrySet()) {
            LocalTime startTime = entry.getKey();
            List<TimeSlot> timeSlots = entry.getValue();
            
            // Find an available slot at this time
            TimeSlot availableSlot = null;
            for (TimeSlot ts : timeSlots) {
                if (ts.getBookedCapacity() < ts.getMaxCapacity()) {
                    availableSlot = ts;
                    break;
                }
            }
            
            // If no slot is available, pick the first one to display
            TimeSlot displaySlot = availableSlot != null ? availableSlot : timeSlots.get(0);
            
            boolean isFull = availableSlot == null;
            boolean isPast = startTime.isBefore(currentTime) || startTime.equals(currentTime);
            boolean isAvailable = !isFull && !isPast;
            
            result.add(Map.<String, Object>of(
                    "id", displaySlot.getId(),
                    "time", displaySlot.getStartTime().toString() + " - " + displaySlot.getEndTime().toString(),
                    "available", isAvailable
            ));
        }
        return result;
    }

    @Override
    @Transactional
    public void createWalkInAppointment(WalkInBookingRequest request) {
        String phone = request.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập số điện thoại.");
        }
        phone = phone.trim();
        
        if (!phone.matches("\\d+")) {
            throw new RuntimeException("Số điện thoại chỉ được chứa chữ số, không chứa chữ cái hoặc ký tự đặc biệt.");
        }
        
        if (!phone.startsWith("0")) {
            throw new RuntimeException("Số điện thoại phải bắt đầu bằng số 0.");
        }
        
        if (phone.length() != 10) {
            throw new RuntimeException("Số điện thoại phải bao gồm đúng 10 chữ số (bạn đang nhập " + phone.length() + " số).");
        }

        // 1. Get or Create Patient
        User patient = userRepository.findByPhone(phone).orElse(null);
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

        // Check if patient already booked this exact slot
        boolean alreadyBooked = appointmentRepository.existsBySlotIdAndPatientIdAndStatusNotIn(
                selectedSlot.getId(), patient.getId(), Arrays.asList("CANCELLED", "NO_SHOW")
        );
        if (alreadyBooked) {
            throw new RuntimeException("Bệnh nhân này đã có lịch khám tại khung giờ này. Vui lòng chọn khung giờ khác.");
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
        appointment.setStatus("CONFIRMED"); // Offline booking always starts as CONFIRMED
        appointment.setCheckInTime(null); // Must be checked-in manually
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
