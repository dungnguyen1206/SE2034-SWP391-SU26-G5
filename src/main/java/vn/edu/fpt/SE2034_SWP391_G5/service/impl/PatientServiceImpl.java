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
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setBloodType(request.getBloodType());
        user.setUpdatedAt(LocalDateTime.now());
        user.setAvatar(request.getAvatar());

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException("Email đã được sử dụng bởi tài khoản khác.");
            }
            user.setEmail(request.getEmail());
            // Need to re-verify if they use a real email
            user.setEmailVerified(false);
        }

        userRepository.save(user);

        if (request.getAddressLine() != null && request.getProvinceId() != null) {
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

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getMedicalRecords(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByExaminationDateDesc(patientId)
                .stream()
                .filter(r -> "FINALIZED".equals(r.getStatus()))
                .map(this::toRecordResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordDetail(Long recordId, Long patientId) {
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

    @Override
    @Transactional
    public void updateNullProfileFields(Long patientId, UpdateUserRequest request) {
        User user = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
                user.setFirstName(request.getFirstName());
            }
        }
        if (user.getMiddleName() == null || user.getMiddleName().trim().isEmpty()) {
            if (request.getMiddleName() != null) {
                user.setMiddleName(request.getMiddleName());
            }
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
                user.setLastName(request.getLastName());
            }
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                user.setPhone(request.getPhone());
            }
        }
        if (user.getGender() == null || user.getGender().trim().isEmpty()) {
            if (request.getGender() != null && !request.getGender().trim().isEmpty()) {
                user.setGender(request.getGender());
            }
        }
        if (user.getDateOfBirth() == null) {
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
            }
        }
        if (user.getBloodType() == null || user.getBloodType().trim().isEmpty()) {
            if (request.getBloodType() != null && !request.getBloodType().trim().isEmpty()) {
                user.setBloodType(request.getBloodType());
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Address logic
        UserAddress address = user.getAddresses() != null
                ? user.getAddresses().stream()
                        .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                        .findFirst().orElse(null)
                : null;

        if (address == null) {
            if (request.getAddressLine() != null && !request.getAddressLine().trim().isEmpty() && request.getProvinceId() != null) {
                Province province = provinceRepository.findById(request.getProvinceId())
                        .orElseThrow(() -> new BadRequestException("Tỉnh/thành phố không hợp lệ"));
                address = new UserAddress();
                address.setUser(user);
                address.setIsDefault(true);
                address.setCreatedAt(LocalDateTime.now());
                address.setAddressLine(request.getAddressLine());
                address.setProvince(province);
                address.setUpdatedAt(LocalDateTime.now());
                userAddressRepository.save(address);
            }
        } else {
            if (address.getAddressLine() == null || address.getAddressLine().trim().isEmpty()) {
                if (request.getAddressLine() != null && !request.getAddressLine().trim().isEmpty()) {
                    address.setAddressLine(request.getAddressLine());
                    address.setUpdatedAt(LocalDateTime.now());
                    userAddressRepository.save(address);
                }
            }
            if (address.getProvince() == null) {
                if (request.getProvinceId() != null) {
                    Province province = provinceRepository.findById(request.getProvinceId())
                            .orElseThrow(() -> new BadRequestException("Tỉnh/thành phố không hợp lệ"));
                    address.setProvince(province);
                    address.setUpdatedAt(LocalDateTime.now());
                    userAddressRepository.save(address);
                }
            }
        }
    }
}
