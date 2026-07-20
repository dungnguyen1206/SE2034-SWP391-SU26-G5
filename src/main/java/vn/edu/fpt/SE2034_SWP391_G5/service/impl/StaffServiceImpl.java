package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateUserRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserAddress;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DepartmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ImageUploadService;
import vn.edu.fpt.SE2034_SWP391_G5.service.StaffService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ImageUploadService imageUploadService;

    public StaffServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, ImageUploadService imageUploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.imageUploadService = imageUploadService;
    }

    //find all active staff
    @Override
    public Page<StaffResponse> findStaff(String roleName, String filterKey, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findActiveStaffList(roleName, filterKey, pageable);
    }

    public StaffResponse findStaffById(Long id) {
        return userRepository.selectStaffById(id);
    }

    @Override
    public User findDoctorById(Long id) {
        User doctor = userRepository.findDoctorStaffDetailById(id).orElseThrow(() -> new ResourceNotFoundException("doctor not found"));
        return doctor;
    }

    @Override
    public User findReceptionistById(Long id) {
        User receptionist = userRepository.findReceptionistStaffDetailById(id).orElseThrow(() -> new ResourceNotFoundException("receptionist not found"));
        return receptionist;
    }

    @Override
    public DoctorStaffDetailResponse findDoctorStaffDetailById(Long id) {
        return toDoctorStaffDetailResponse(findDoctorById(id));
    }

    private DoctorStaffDetailResponse toDoctorStaffDetailResponse(User doctor) {
        DoctorStaffDetailResponse doctorStaffDetailResponse = new DoctorStaffDetailResponse();
        doctorStaffDetailResponse.setId(doctor.getId());
        doctorStaffDetailResponse.setStaffCode("STF-" + doctor.getId());
        doctorStaffDetailResponse.setBio(doctor.getBio());
        doctorStaffDetailResponse.setDegree(doctor.getDegree());
        doctorStaffDetailResponse.setEmail(doctor.getEmail());
        doctorStaffDetailResponse.setCreatedAt(doctor.getCreatedAt());
        doctorStaffDetailResponse.setCreatedBy(doctor.getCreatedBy().getFirstName() + " " + doctor.getCreatedBy().getMiddleName() + " " + doctor.getCreatedBy().getLastName());
        doctorStaffDetailResponse.setPhone(doctor.getPhone());
        doctorStaffDetailResponse.setDepartmentName(doctor.getDepartment().getName());
        doctorStaffDetailResponse.setFullName(doctor.getFirstName() + " " + doctor.getMiddleName() + " " + doctor.getLastName());
        doctorStaffDetailResponse.setExperienceYears(0); // Tạm thời set 0
        // doctorStaffDetailResponse.setLicenseIssueDate(doctor.getLicenseIssueDate()); // Đã xóa
        doctorStaffDetailResponse.setLicenseNumber(String.valueOf(doctor.getLicenseNumber()));
        doctorStaffDetailResponse.setRoleName("DOCTOR");
        doctorStaffDetailResponse.setRoleLabel("Bác sĩ");
        if (doctor.getDoctorStatus().equals("INACTIVE")) {
            doctorStaffDetailResponse.setAccountStatus("INACTIVE");
            doctorStaffDetailResponse.setWorkingStatus("INACTIVE");
        } else {
            doctorStaffDetailResponse.setWorkingStatus(doctor.getDoctorStatus());
            doctorStaffDetailResponse.setAccountStatus(doctor.getStatus());
        }
        doctorStaffDetailResponse.setAvatar(doctor.getAvatar());
        return doctorStaffDetailResponse;
    }

    private ReceptionistStaffDetailResponse toReceptionistStaffDetailResponse(User receptionist) {
        ReceptionistStaffDetailResponse receptionistStaffDetailResponse = new ReceptionistStaffDetailResponse();
        receptionistStaffDetailResponse.setId(receptionist.getId());
        receptionistStaffDetailResponse.setStaffCode("STF-" + receptionist.getId());
        receptionistStaffDetailResponse.setEmail(receptionist.getEmail());
        receptionistStaffDetailResponse.setPhone(receptionist.getPhone());
        receptionistStaffDetailResponse.setCreatedBy(receptionist.getCreatedBy().getFirstName() + "  " + receptionist.getCreatedBy().getMiddleName() + " " + receptionist.getCreatedBy().getLastName());
        receptionistStaffDetailResponse.setCreatedAt(receptionist.getCreatedAt());
        receptionistStaffDetailResponse.setAccountStatus(receptionist.getStatus());
        receptionistStaffDetailResponse.setFullName(receptionist.getFirstName() + " " + receptionist.getMiddleName() + " " + receptionist.getLastName());
        receptionistStaffDetailResponse.setRoleName("RECEPTIONIST");
        receptionistStaffDetailResponse.setRoleLabel("Lễ tân");
        receptionistStaffDetailResponse.setWorkingStatus("Đang làm việc");
        receptionistStaffDetailResponse.setAvatar(receptionist.getAvatar());
        receptionistStaffDetailResponse.setGender(receptionist.getGender());
        receptionistStaffDetailResponse.setUpdatedAt(receptionist.getUpdatedAt());
        receptionistStaffDetailResponse.setBio(receptionist.getBio());
        return receptionistStaffDetailResponse;
    }

    @Override
    public ReceptionistStaffDetailResponse findReceptionistStaffDetailById(Long id) {
        return toReceptionistStaffDetailResponse(findReceptionistById(id));
    }


    public Long countDoctorsAppointmentByAppointmentStatus(String appointmentStatus, Long doctorId) {
        return userRepository.countDoctorsAppointmentByAppointmentStatus(appointmentStatus, doctorId);
    }

    @Override
    public UpdateUserRequest getReceptionistToUpdate(Long id) {
        return toUpdateUserRequest(findReceptionistById(id), "RECEPTIONIST");
    }

    @Override
    public UpdateUserRequest getDoctorToUpdate(Long id) {
        return toUpdateUserRequest(findDoctorById(id), "DOCTOR");
    }

    @Override
    public UpdateUserRequest getPatientToUpdate(Long id) {
        User patient = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("patient not found"));
        return toUpdateUserRequest(patient, "PATIENT");
    }

    private UpdateUserRequest toUpdateUserRequest(User user, String staffRole) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(user.getId());
        updateUserRequest.setProfileType(staffRole);
        updateUserRequest.setFirstName(user.getFirstName());
        updateUserRequest.setLastName(user.getLastName());
        updateUserRequest.setMiddleName(user.getMiddleName());
        updateUserRequest.setEmail(user.getEmail());
        updateUserRequest.setPhone(user.getPhone());
        updateUserRequest.setBio(user.getBio());
        updateUserRequest.setCreatedAt(user.getCreatedAt());
        updateUserRequest.setCreatedBy(user.getCreatedBy());
        updateUserRequest.setAccountStatus(user.getStatus());
        updateUserRequest.setGender(user.getGender());
        updateUserRequest.setStaffRole(staffRole);
        updateUserRequest.setUpdatedAt(user.getUpdatedAt());
        updateUserRequest.setDateOfBirth(user.getDateOfBirth());
        updateUserRequest.setAvatar(user.getAvatar());
        updateUserRequest.setBloodType(user.getBloodType());

        if (user.getAddresses() != null) {
            UserAddress defaultAddress = user.getAddresses().stream()
                    .filter(address -> Boolean.TRUE.equals(address.getIsDefault()))
                    .findFirst()
                    .orElse(null);

            if (defaultAddress != null) {
                updateUserRequest.setAddressLine(defaultAddress.getAddressLine());
                updateUserRequest.setDefaultAddress(defaultAddress.getIsDefault());
                updateUserRequest.setProvinceId(defaultAddress.getProvince() != null
                        ? defaultAddress.getProvince().getId()
                        : null);
            }
        }

        if ("DOCTOR".equalsIgnoreCase(staffRole)) {
            updateUserRequest.setDoctorStatus(user.getDoctorStatus());
            updateUserRequest.setDepartmentId(user.getDepartment() != null ? user.getDepartment().getId() : null);
            updateUserRequest.setDegree(user.getDegree());
            // updateUserRequest.setLicenseIssueDate(user.getLicenseIssueDate()); // Đã xóa
            updateUserRequest.setLicenseNumber(user.getLicenseNumber());
        }

        return updateUserRequest;
    }

    @Override
    @Transactional
    public void updateStaffProfile(Long id, UpdateUserRequest request) {

        User staff = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if (!request.getProfileType().equalsIgnoreCase("DOCTOR")
                && !request.getProfileType().equalsIgnoreCase("RECEPTIONIST")) {
            throw new BadRequestException("Chỉ được truy cập hồ sơ của lễ tân và bác sĩ");
        }
        // 2. Set common fields
        staff.setFirstName(request.getFirstName());
        staff.setMiddleName(request.getMiddleName());
        staff.setLastName(request.getLastName());
        //validate phone
        if (!staff.getPhone().equals(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại đã được đăng kí trước đó");
        }
        staff.setPhone(request.getPhone());
        staff.setGender(request.getGender());

        if (!vertifyDOB(request.getDateOfBirth(), request.getStaffRole())) {
            throw new BadRequestException("Ngày sinh phải hợp lệ theo quy định của pháp luật về độ tuổi lao động");
        }
        // Validation về licenseIssueDate đã bỏ vì field không còn nữa
        // else if(Period.between(request.getDateOfBirth(), request.getLicenseIssueDate()).getYears() < 25) {
        //     throw new BadRequestException("Ngày sinh xung đột với ngày cấp giấy phép hành nghề !" +"   Bác sĩ chưa đủ tuổi để được cấp giấy phép hành nghề");
        // }
        staff.setDateOfBirth(request.getDateOfBirth());

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            if (staff.getAvatar() != null && !staff.getAvatar().isEmpty() && !request.getAvatar().equals(staff.getAvatar())) {
                imageUploadService.deleteImage(staff.getAvatar());
            }
            staff.setAvatar(request.getAvatar());
        }

        staff.setBio(request.getBio());
        if (request.getDoctorStatus() != null && !request.getDoctorStatus().isEmpty()) {
            staff.setStatus(request.getDoctorStatus());
        } else {
            staff.setStatus(request.getAccountStatus());
        }
        staff.setUpdatedAt(LocalDateTime.now());
        staff.setCreatedBy(request.getCreatedBy());

        if (request.getStaffRole().equalsIgnoreCase("DOCTOR")) {
            Department department = departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Phòng ban không hợp lệ"));
            staff.setDepartment(department);
            staff.setDegree(request.getDegree());
            // Validation licenseIssueDate đã bỏ
            // if (request.getLicenseIssueDate().getYear()  - request.getDateOfBirth().getYear() <=25 ) {
            //     throw new BadRequestException("Số năm kinh nghiệm phải nhỏ hơn hoặc bằng số năm từ lúc lấy giấy phép hành nghê lần đầu tiên");
            // } else {
            //     staff.setLicenseIssueDate(request.getLicenseIssueDate());
            // }
            staff.setLicenseNumber(request.getLicenseNumber());
            if (!staff.getLicenseNumber().equalsIgnoreCase(request.getLicenseNumber()) && userRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new BadRequestException("Mã giấy phép đã tồn tại");
            }
            staff.setDoctorStatus(request.getDoctorStatus());
        } else {
            staff.setDepartment(null);
            staff.setDegree(null);
            staff.setLicenseNumber(null);
            // staff.setLicenseIssueDate(null); // Đã xóa
            staff.setDoctorStatus(null);
        }
        userRepository.save(staff);
    }

    private boolean vertifyDOB(LocalDate dob, String role) {
        if (dob == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate minRange4Receptionist = today.minusYears(60);
        LocalDate maxRange4Receptionist = today.minusYears(18);
        LocalDate minRange4Doctor = today.minusYears(75);
        LocalDate maxRange4Doctor = today.minusYears(25);
        if (role.equalsIgnoreCase("DOCTOR")) {
            return dob.isAfter(minRange4Doctor) && dob.isBefore(maxRange4Doctor);
        } else if (role.equalsIgnoreCase("RECEPTIONIST")) {
            return dob.isAfter(minRange4Receptionist) && dob.isBefore(maxRange4Receptionist);
        }
        return false;
    }
}
