package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.WalkInBookingRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistWalkInService;
import vn.edu.fpt.SE2034_SWP391_G5.service.SmsService;

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
    private final SmsService smsService;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    // ======================== WALK-IN BOOKING RECEPTIONIST ========================
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
            // Cho phép nhân viên (chưa có role PATIENT) được phép dùng số điện thoại của mình để đặt lịch như bệnh nhân bình thường.
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
        if (date == null || date.isBefore(LocalDate.now())) {
            return List.of();
        }

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
                if ("AVAILABLE".equalsIgnoreCase(ts.getStatus())
                        && ts.getBookedCapacity() < ts.getMaxCapacity()) {
                    availableSlot = ts;
                    break;
                }
            }

            // If no slot is available, pick the first one to display
            TimeSlot displaySlot = availableSlot != null ? availableSlot : timeSlots.get(0);

            boolean isFull = availableSlot == null;
            boolean isPast = displaySlot.getEndTime().isBefore(currentTime) || displaySlot.getEndTime().equals(currentTime);
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
    public Long createWalkInAppointment(WalkInBookingRequest request) {
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
            patient.setEmail(request.getPhone() + "@walkin.local");
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setGender(request.getGender());

            // Generate a random, strong password
            String generatedPassword = "Wk" + java.util.UUID.randomUUID().toString().substring(0, 5);
            patient.setPasswordHash(passwordEncoder.encode(generatedPassword));

            patient.setStatus("ACTIVE");
            patient.setEmailVerified(true);
            patient.setCreatedAt(LocalDateTime.now());
            patient.setUpdatedAt(LocalDateTime.now());

            patient = userRepository.save(patient);

            Role patientRole = roleRepository.findByName("PATIENT").orElseThrow(() -> new RuntimeException("Role PATIENT not found"));
            UserRole userRole = new UserRole();

            vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId userRoleId = new vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId(patient.getId(), patientRole.getId());
            userRole.setId(userRoleId);

            userRole.setUser(patient);
            userRole.setRole(patientRole);
            userRole.setAssignedAt(LocalDateTime.now());
            userRoleRepository.save(userRole);

            smsService.sendWalkInAccountSms(request.getPhone(), generatedPassword);
        } else {
            // Đảm bảo user có sẵn (như nhân viên) cũng được cấp quyền PATIENT nếu chưa có
            boolean hasPatientRole = patient.getUserRoles().stream()
                    .anyMatch(ur -> ur.getRole().getName().equals("PATIENT"));
            if (!hasPatientRole) {
                Role patientRole = roleRepository.findByName("PATIENT")
                        .orElseThrow(() -> new RuntimeException("Role PATIENT not found"));
                UserRole userRole = new UserRole();
                vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId userRoleId = new vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId(patient.getId(), patientRole.getId());
                userRole.setId(userRoleId);
                userRole.setUser(patient);
                userRole.setRole(patientRole);
                userRole.setAssignedAt(LocalDateTime.now());
                userRoleRepository.save(userRole);
            }
        }

        // 2. Get Selected Medical Service
        MedicalService selectedService = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ khám."));

        if (selectedService.getDepartment() == null || !selectedService.getDepartment().getId().equals(request.getDepartmentId())) {
            throw new RuntimeException("Dịch vụ khám không thuộc khoa đã chọn.");
        }

        // 3. Xử lý Logic lấy Slot Khám (Tự động bốc số cho Hôm nay vs Chọn giờ cho Tương lai)
        TimeSlot selectedSlot = null;
        boolean isToday = request.getBookingDate().isEqual(LocalDate.now());

        if (isToday) {
            // [NGHIỆP VỤ HÔM NAY]: Bệnh nhân khám trong ngày không cần chọn ca khám.
            // Hệ thống sẽ tự động bốc số và xếp vào hàng đợi.
            java.util.List<TimeSlot> availableSlots = timeSlotRepository.findSlotsByDepartmentAndDate(request.getDepartmentId().intValue(), request.getBookingDate());
            
            // [TỐI ƯU HÓA HÀNG ĐỢI]: Không lọc bỏ ca khám đã qua giờ (isAfter(now))
            // Bệnh nhân khám trong ngày có thể đến bất cứ lúc nào miễn là Bác sĩ có lịch trực hôm nay.
            availableSlots = availableSlots.stream()
                    .filter(s -> !"CANCELLED".equalsIgnoreCase(s.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
            
            if (availableSlots.isEmpty()) {
                throw new RuntimeException("Hiện tại chưa có lịch làm việc của Bác sĩ nào được phê duyệt cho khoa này trong ngày hôm nay.");
            }
            
            // Nhóm danh sách các ca khám theo từng Bác sĩ
            java.util.Map<Long, java.util.List<TimeSlot>> slotsByDoctor = availableSlots.stream()
                    .collect(java.util.stream.Collectors.groupingBy(s -> s.getSchedule().getDoctor().getId()));
            
            Long bestDoctorId = null;
            long minQueueLength = Long.MAX_VALUE;
            
            // Tính toán hàng đợi THỰC TẾ của từng bác sĩ (đếm số lượng đang WAITING và EXAMINING)
            for (Long doctorId : slotsByDoctor.keySet()) {
                long queueLength = appointmentRepository.countByDoctorIdAndBookingDateAndStatusIn(
                        doctorId, request.getBookingDate(), java.util.Arrays.asList("WAITING", "EXAMINING"));
                
                if (queueLength < minQueueLength) {
                    minQueueLength = queueLength;
                    bestDoctorId = doctorId;
                }
            }
            
            // Lấy ca khám phù hợp với thời gian hiện tại
            java.util.List<TimeSlot> bestDoctorSlots = slotsByDoctor.get(bestDoctorId);
            bestDoctorSlots.sort(java.util.Comparator.comparing(TimeSlot::getStartTime));
            java.time.LocalTime now = java.time.LocalTime.now();
            
            // 1. Cố gắng tìm slot đang diễn ra
            selectedSlot = bestDoctorSlots.stream()
                    .filter(s -> !now.isBefore(s.getStartTime()) && !now.isAfter(s.getEndTime()))
                    .findFirst()
                    .orElse(null);
                    
            // 2. Nếu đang giờ nghỉ, lấy slot tiếp theo gần nhất
            if (selectedSlot == null) {
                selectedSlot = bestDoctorSlots.stream()
                        .filter(s -> s.getStartTime().isAfter(now))
                        .findFirst()
                        .orElse(null);
            }
            
            // 3. Nếu đã qua hết ca làm việc, lấy ca cuối cùng
            if (selectedSlot == null) {
                selectedSlot = bestDoctorSlots.get(bestDoctorSlots.size() - 1);
            }
            
            // LƯU Ý: Khám trong ngày (Offline) không bị giới hạn bởi maxCapacity và KHÔNG tăng bookedCapacity.
            // Điều này đảm bảo bookedCapacity chỉ dùng để đếm số lượng khách đã đặt xí chỗ trước (Online).
        } else {
            // [NGHIỆP VỤ TƯƠNG LAI]: Đặt lịch cho ngày mai hoặc xa hơn.
            // Lễ tân bắt buộc phải chọn Khung giờ khám cụ thể.
            if (request.getTimeSlotId() == null) {
                throw new RuntimeException("Vui lòng chọn ca khám.");
            }
            selectedSlot = timeSlotRepository.findByIdWithSchedule(request.getTimeSlotId())
                    .orElseThrow(() -> new RuntimeException("Khung giờ không tồn tại."));

            if (!selectedSlot.getSchedule().getWorkDate().equals(request.getBookingDate())) {
                throw new RuntimeException("Khung giờ không khớp với ngày khám.");
            }
            
            // Đặt cho tương lai hoạt động GIỐNG HỆT Đặt lịch Online:
            // 1. Phải bị giới hạn bởi maxCapacity để giữ công bằng cho người đặt Online.
            if (!"AVAILABLE".equalsIgnoreCase(selectedSlot.getStatus())
                    || selectedSlot.getBookedCapacity() >= selectedSlot.getMaxCapacity()) {
                
                // [TỐI ƯU HÓA HÀNG ĐỢI]: Logic Fallback - Tìm slot khác cùng giờ
                LocalTime targetStartTime = selectedSlot.getStartTime();
                java.util.List<TimeSlot> altSlots = timeSlotRepository.findSlotsByDepartmentAndDate(request.getDepartmentId().intValue(), request.getBookingDate());
                TimeSlot altSlot = altSlots.stream()
                        .filter(s -> s.getStartTime().equals(targetStartTime) && 
                                     "AVAILABLE".equalsIgnoreCase(s.getStatus()) && 
                                     s.getBookedCapacity() < s.getMaxCapacity())
                        .findFirst().orElse(null);
                
                if (altSlot != null) {
                    selectedSlot = altSlot; // Tự động gán sang Bác sĩ khác có cùng khung giờ
                } else {
                    throw new RuntimeException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác.");
                }
            }
            
            // 2. Phải tăng bookedCapacity lên 1 để xí chỗ thành công.
            selectedSlot.setBookedCapacity(selectedSlot.getBookedCapacity() + 1);
            if (selectedSlot.getBookedCapacity() >= selectedSlot.getMaxCapacity()) {
                selectedSlot.setStatus("FULL");
            }
            timeSlotRepository.save(selectedSlot);
        }

        DoctorSchedule selectedSchedule = selectedSlot.getSchedule();
        User selectedDoctor = selectedSchedule.getDoctor();
        Integer doctorDepartmentId = selectedDoctor == null || selectedDoctor.getDepartment() == null
                ? null : selectedDoctor.getDepartment().getId();
        Integer serviceDepartmentId = selectedService.getDepartment() == null
                ? null : selectedService.getDepartment().getId();

        String wsStatus = selectedSchedule.getWeekSchedule() != null ? selectedSchedule.getWeekSchedule().getStatus() : "";
        if (!"ACTIVE".equalsIgnoreCase(selectedSchedule.getStatus())
                || (!"FINALIZED".equalsIgnoreCase(wsStatus) && !"EXPIRED".equalsIgnoreCase(wsStatus) && !"PUBLISHED".equalsIgnoreCase(wsStatus))) {
            throw new RuntimeException("Lịch khám này chưa được công bố hoặc đã ngừng hoạt động.");
        }

        if (selectedDoctor == null
                || !"ACTIVE".equalsIgnoreCase(selectedDoctor.getStatus())
                || !"ACTIVE".equalsIgnoreCase(selectedDoctor.getDoctorStatus())
                || !Objects.equals(request.getDepartmentId(), doctorDepartmentId)
                || !Objects.equals(request.getDepartmentId(), serviceDepartmentId)) {
            throw new RuntimeException("Bác sĩ, dịch vụ và chuyên khoa không khớp hoặc không còn hoạt động.");
        }

        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Không thể đặt lịch khám cho ngày trong quá khứ.");
        }

        // Check if patient already booked this exact slot
        boolean alreadyBookedTimeSlot = appointmentRepository.existsBySlotIdAndPatientIdAndStatusNotIn(
                selectedSlot.getId(), patient.getId(), Arrays.asList("CANCELLED", "NO_SHOW")
        );
        if (alreadyBookedTimeSlot) {
            throw new RuntimeException("Bệnh nhân này đã đặt lịch trong khung giờ này rồi. Không thể đặt trùng khung giờ.");
        }

        // Check if patient already booked this department on this date
        boolean alreadyBookedDept = appointmentRepository.existsByPatientIdAndDepartmentIdAndBookingDateAndStatusNotIn(
                patient.getId(), request.getDepartmentId(), request.getBookingDate(), Arrays.asList("CANCELLED", "NO_SHOW")
        );
        if (alreadyBookedDept) {
            throw new RuntimeException("Bệnh nhân này đã có lịch khám tại khoa này trong ngày hôm nay. Không thể đặt thêm lịch cùng khoa.");
        }

        // Check if patient has any incomplete appointment ON THIS EXACT DATE (across all departments)
        boolean hasIncompleteAppointmentToday = appointmentRepository.existsByPatientIdAndBookingDateAndStatusIn(
                patient.getId(),
                request.getBookingDate(),
                Arrays.asList("CONFIRMED", "WAITING", "EXAMINING")
        );
        if (hasIncompleteAppointmentToday) {
            throw new RuntimeException("Bệnh nhân đang có một lịch khám chưa hoàn tất trong ngày này. Vui lòng hoàn thành lịch khám trước đó trước khi đặt lịch mới.");
        }



        // 4. Create Appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentCode("WI-" + System.currentTimeMillis());
        appointment.setPatient(patient);
        appointment.setDoctor(selectedDoctor);
        appointment.setService(selectedService);
        appointment.setSlot(selectedSlot);
        appointment.setBookingDate(request.getBookingDate());
        if (isToday) {
            appointment.setStatus("WAITING"); // Khám hôm nay: tự động check-in luôn
            appointment.setCheckInTime(LocalDateTime.now());
        } else {
            appointment.setStatus("CONFIRMED"); // Khám ngày mai: giữ trạng thái Đã xác nhận, mai mới check-in
            appointment.setCheckInTime(null);
        }
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        if (isToday) {
            // 5. Create Invoice (Chỉ tạo Hóa đơn khi đã Check-in, tức là khám hôm nay)
            Invoice invoice = new Invoice();
            String invCode = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + String.format("%04d", new java.util.Random().nextInt(10000));
            invoice.setInvoiceCode(invCode);
            invoice.setAppointment(appointment);
            invoice.setTotalAmount(selectedService.getReferencePrice());
            invoice.setPaymentMethod("CASH");
            invoice.setPaymentStatus("UNPAID");
            invoice.setCreatedAt(LocalDateTime.now());
            invoice.setUpdatedAt(LocalDateTime.now());
            invoice = invoiceRepository.save(invoice);

            // 6. Create InvoiceItem
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setService(selectedService);
            item.setItemName(selectedService.getName());
            item.setPriceApplied(selectedService.getReferencePrice());
            item.setQuantity(1);
            item.setLineTotal(selectedService.getReferencePrice());
            invoiceItemRepository.save(item);
        }

        return appointment.getId();
    }
    // ======================== END WALK-IN BOOKING RECEPTIONIST ========================
}
