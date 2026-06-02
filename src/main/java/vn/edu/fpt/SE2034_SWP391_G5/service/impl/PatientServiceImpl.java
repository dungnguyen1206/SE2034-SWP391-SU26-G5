package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateProfileRequest;
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
    public void updateProfile(Long patientId, UpdateProfileRequest request) {
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
    public List<MedicalRecordResponse> getMedicalRecords(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByExaminationDateDesc(patientId)
                .stream()
                .map(this::toRecordResponse)
                .toList();
    }

    @Override
    public MedicalRecordResponse getMedicalRecordDetail(Long recordId, Long patientId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ bệnh án"));

        if (!record.getPatient().getId().equals(patientId)) {
            throw new BadRequestException("Bạn không có quyền xem hồ sơ này");
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

}
