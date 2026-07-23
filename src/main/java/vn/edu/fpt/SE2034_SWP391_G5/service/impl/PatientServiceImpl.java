package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateProfileRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateUserRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Province;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserAddress;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ProvinceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserAddressRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.List;
import java.time.LocalDateTime;




@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProvinceRepository provinceRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public PatientResponse getProfile(Long patientId) {
        User user = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        UserAddress defaultAddress = user.getAddresses() != null
                ? user.getAddresses().stream()
                        .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                        .findFirst().orElse(null)
                : null;

        return PatientResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(buildFullName(user.getLastName(), user.getMiddleName(), user.getFirstName()))
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .bloodType(user.getBloodType())
                .status(user.getStatus())
                .addressLine(defaultAddress != null ? defaultAddress.getAddressLine() : null)
                .provinceName(defaultAddress != null && defaultAddress.getProvince() != null
                        ? defaultAddress.getProvince().getName() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }


    @Override
    @Transactional
    public void updateProfile(Long patientId, UpdateUserRequest request) {
        User user = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        // Số điện thoại không được thay đổi khi chỉnh sửa profile
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setBloodType(request.getBloodType());
        user.setUpdatedAt(LocalDateTime.now());
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException("Email đã được sử dụng bởi tài khoản khác.");
            }
            user.setEmail(request.getEmail());
            // Need to re-verify if they use a real email
            user.setEmailVerified(false);
        }

        userRepository.save(user);

        if (request.getAddressLine() != null && !request.getAddressLine().trim().isEmpty()) {
            if (request.getProvinceId() == null) {
                throw new BadRequestException("Tỉnh/thành phố không được để trống");
            }
            Province province = provinceRepository.findById(request.getProvinceId())
                    .orElseThrow(() -> new BadRequestException("Tỉnh/thành phố không hợp lệ"));

            UserAddress address = user.getAddresses() != null
                    ? user.getAddresses().stream()
                            .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                            .findFirst().orElse(null)
                    : null;

            if (address == null) {
                address = new UserAddress();
                address.setUser(user);
                address.setIsDefault(true);
                address.setCreatedAt(LocalDateTime.now());
            }
            address.setAddressLine(request.getAddressLine());
            address.setProvince(province);
            address.setUpdatedAt(LocalDateTime.now());
            userAddressRepository.save(address);
        }
    }

    private MedicalRecordResponse getMockRecord() {
        List<MedicalRecordResponse.ServiceOrderInfo> orders = List.of(
            MedicalRecordResponse.ServiceOrderInfo.builder()
                .id(999991L)
                .serviceName("Chụp X-quang phổi thẳng (Kỹ thuật số)")
                .result("Hình ảnh bóng tim bình thường. Vùng phổi hai bên sáng, không thấy tổn thương nhu mô phổi tiến triển.")
                .status("COMPLETED")
                .note("Không phát hiện bất thường.")
                .price(java.math.BigDecimal.valueOf(150000))
                .build(),
            MedicalRecordResponse.ServiceOrderInfo.builder()
                .id(999992L)
                .serviceName("Xét nghiệm tổng phân tích tế bào máu ngoại vi")
                .result("Số lượng bạch cầu tăng nhẹ (10.5 G/L), các chỉ số hồng cầu và tiểu cầu trong giới hạn bình thường.")
                .status("COMPLETED")
                .note("Theo dõi tình trạng viêm nhẹ.")
                .price(java.math.BigDecimal.valueOf(200000))
                .build()
        );

        return MedicalRecordResponse.builder()
                .id(999999L)
                .appointmentId(999999L)
                .appointmentCode("APT-DEMO-9999")
                .doctorFullName("Nguyễn Việt Anh")
                .doctorDegree("ThS. BS")
                .departmentName("Khoa Hô hấp")
                .serviceName("Khám nội hô hấp")
                .examinationDate(java.time.LocalDateTime.now().minusDays(1))
                .symptoms("Ho khạc đờm trắng đục kéo dài 3 ngày, sốt nhẹ về chiều (38 độ C), đau rát họng.")
                .diagnosis("Viêm phế quản cấp tính")
                .conclusion("Bệnh nhân nghỉ ngơi hợp lý, tránh tiếp xúc khói bụi và nước lạnh. Uống nhiều nước ấm.")
                .prescriptionText("1. Kháng sinh Augmentin 1g - 14 viên (Ngày uống 2 lần, mỗi lần 1 viên sau ăn sáng/tối)\n2. Thuốc ho Acemuc 200mg - 20 gói (Ngày uống 3 lần, mỗi lần 1 gói hòa tan trong nước)\n3. Giảm đau hạ sốt Paracetamol 500mg - 10 viên (Uống 1 viên khi sốt trên 38.5 độ C, cách nhau tối thiểu 4-6 giờ)")
                .notes("Tái khám sau 7 ngày hoặc khi có dấu hiệu khó thở tăng dần.")
                .heartRate(82)
                .bloodPressure("120/80")
                .bloodGlucose(java.math.BigDecimal.valueOf(5.6))
                .weight(java.math.BigDecimal.valueOf(68.5))
                .status("FINALIZED")
                .serviceOrders(orders)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getMedicalRecords(Long patientId) {
        List<MedicalRecord> list = medicalRecordRepository.findByPatientIdOrderByExaminationDateDesc(patientId);
        List<MedicalRecordResponse> responses = new java.util.ArrayList<>(list.stream()
                .filter(r -> "FINALIZED".equals(r.getStatus()))
                .map(this::toRecordResponse)
                .toList());
        responses.add(0, getMockRecord());
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordDetail(Long recordId, Long patientId) {
        if (recordId != null && recordId.equals(999999L)) {
            return getMockRecord();
        }
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ bệnh án"));

        if (!record.getPatient().getId().equals(patientId)) {
            throw new BadRequestException("Bạn không có quyền xem hồ sơ này");
        }
        if (!"FINALIZED".equals(record.getStatus())) {
            throw new BadRequestException("Hồ sơ bệnh án chưa được hoàn tất");
        }
        return toRecordResponse(record);
    }

    private MedicalRecordResponse toRecordResponse(MedicalRecord r) {
        User doctor = r.getDoctor();
        String doctorFullName = doctor != null
                ? buildFullName(doctor.getLastName(), doctor.getMiddleName(), doctor.getFirstName())
                : "";
        String appointmentCode = r.getAppointment() != null
                ? r.getAppointment().getAppointmentCode() : null;
        String serviceName = r.getAppointment() != null && r.getAppointment().getService() != null
                ? r.getAppointment().getService().getName() : null;

        List<MedicalRecordResponse.ServiceOrderInfo> serviceOrders = r.getMedicalServiceOrders() != null
                ? r.getMedicalServiceOrders().stream()
                        .map(mso -> MedicalRecordResponse.ServiceOrderInfo.builder()
                                .id(mso.getId())
                                .serviceName(mso.getMedicalService() != null ? mso.getMedicalService().getName() : "")
                                .result(mso.getResult())
                                .status(mso.getStatus())
                                .note(mso.getNote())
                                .price(mso.getPriceReference())
                                .build())
                        .toList()
                : List.of();

        return MedicalRecordResponse.builder()
                .id(r.getId())
                .appointmentId(r.getAppointment() != null ? r.getAppointment().getId() : null)
                .appointmentCode(appointmentCode)
                .doctorFullName(doctorFullName)
                .doctorDegree(doctor != null ? doctor.getDegree() : null)
                .departmentName(doctor != null && doctor.getDepartment() != null
                        ? doctor.getDepartment().getName() : null)
                .serviceName(serviceName)
                .examinationDate(r.getExaminationDate())
                .symptoms(r.getSymptoms())
                .diagnosis(r.getDiagnosis())
                .conclusion(r.getConclusion())
                .prescriptionText(r.getPrescriptionText())
                .notes(r.getNotes())
                .heartRate(r.getHeartRate())
                .bloodPressure(r.getBloodPressure())
                .bloodGlucose(r.getBloodGlucose())
                .weight(r.getWeight())
                .status(r.getStatus())
                .serviceOrders(serviceOrders)
                .build();
    }

    private String buildFullName(String lastName, String middleName, String firstName) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (middleName != null) sb.append(middleName).append(" ");
        if (firstName != null) sb.append(firstName);
        return sb.toString().trim();
    }

    // Removed duplicate field and constructor injected from merge conflict (main branch)
    // private UserRepository userRepository;
    // public PatientServiceImpl(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public List<User> findUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProfileComplete(Long patientId) {
        User user = userRepository.findById(patientId).orElse(null);
        if (user == null) {
            return false;
        }
        if (user.getFirstName() == null || user.getFirstName().strip().isEmpty()) return false;
        if (user.getLastName() == null || user.getLastName().strip().isEmpty()) return false;
        if (user.getPhone() == null || user.getPhone().strip().isEmpty()) return false;
        if (user.getGender() == null || user.getGender().strip().isEmpty()) return false;
        if (user.getDateOfBirth() == null) return false;
        if (user.getEmail() == null || user.getEmail().strip().isEmpty()) return false;

        if (user.getAddresses() == null || user.getAddresses().isEmpty()) return false;
        return user.getAddresses().stream()
                .anyMatch(addr -> addr.getAddressLine() != null && !addr.getAddressLine().strip().isEmpty()
                        && addr.getProvince() != null);
    }
}
