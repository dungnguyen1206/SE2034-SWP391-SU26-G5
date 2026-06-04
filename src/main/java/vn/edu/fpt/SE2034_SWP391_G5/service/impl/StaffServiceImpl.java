package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import vn.edu.fpt.SE2034_SWP391_G5.config.PasswordEncoderConfig;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateStaffRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Role;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DepartmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.StaffService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public StaffServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    //find all active staff
    public List<StaffResponse> findStaff(String roleName, String filterKey) {
        return userRepository.findActiveStaffList(roleName, filterKey);
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
        doctorStaffDetailResponse.setAccountStatus(doctor.getStatus());
        doctorStaffDetailResponse.setCreatedBy(doctor.getCreatedBy().getFirstName() + " " + doctor.getCreatedBy().getMiddleName() + " " + doctor.getCreatedBy().getLastName());
        doctorStaffDetailResponse.setPhone(doctor.getPhone());
        doctorStaffDetailResponse.setDepartmentName(doctor.getDepartment().getName());
        doctorStaffDetailResponse.setFullName(doctor.getFirstName() + " " + doctor.getMiddleName() + " " + doctor.getLastName());
        doctorStaffDetailResponse.setExperienceYears(doctor.getExperienceYears());
        doctorStaffDetailResponse.setLicenseNumber(String.valueOf(doctor.getLicenseNumber()));
        doctorStaffDetailResponse.setRoleName("DOCTOR");
        doctorStaffDetailResponse.setRoleLabel("Bác sĩ");
        doctorStaffDetailResponse.setWorkingStatus(doctor.getStatus());
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
        receptionistStaffDetailResponse.setWorkingStatus("Đang hoạt động");
        receptionistStaffDetailResponse.setAvatar(receptionist.getAvatar());
        receptionistStaffDetailResponse.setGender(receptionist.getGender());
        receptionistStaffDetailResponse.setUpdatedAt(receptionist.getUpdatedAt());
        receptionistStaffDetailResponse.setBio(receptionist.getBio());
        return receptionistStaffDetailResponse;
    }

    public ReceptionistStaffDetailResponse findReceptionistStaffDetailById(Long id) {
        return toReceptionistStaffDetailResponse(findReceptionistById(id));
    }


    public Long countDoctorsAppointmentByAppointmentStatus(String appointmentStatus, Long doctorId) {
        return userRepository.countDoctorsAppointmentByAppointmentStatus(appointmentStatus, doctorId);
    }

    @Override
    @Transactional
    public User createStaff(CreateStaffRequest createStaff, BindingResult bindingResult) {
        String staffType = createStaff.getStaffType();

        boolean isDoctor = "doctor".equalsIgnoreCase(staffType);
        boolean isReceptionist = "receptionist".equalsIgnoreCase(staffType);

        if (!isDoctor && !isReceptionist) {
            bindingResult.rejectValue("staffType", "staffType.invalid", "Loại nhân viên không hợp lệ");
            return null;
        }

        if (userRepository.existsByEmail(createStaff.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "Email đã tồn tại");
            return null;
        }
        if (userRepository.existsByPhone(createStaff.getPhone())) {
            bindingResult.rejectValue("phone", "phone.exists", "Số điện thoại đã tồn tại");
            return null;
        }
        if (isDoctor) {
            if (createStaff.getDepartmentId() == null) {
                bindingResult.rejectValue("departmentId", "department.required", "Khoa không được bỏ trống");
            }
            if (!StringUtils.hasText(createStaff.getDegree())) {
                bindingResult.rejectValue("degree", "degree.required", "Bằng cấp không được bỏ trống");
            }
            if (!StringUtils.hasText(createStaff.getLicenseNumber())) {
                bindingResult.rejectValue("licenseNumber", "licenseNumber.required", "Số giấy phép hành nghề không được bỏ trống");
            } else if (userRepository.existsByLicenseNumber(createStaff.getLicenseNumber())) {
                bindingResult.rejectValue("licenseNumber", "licenseNumber.exists", "Số giấy phép hành nghề đã tồn tại");
            }
            if (createStaff.getExperienceYears() == null) {
                bindingResult.rejectValue("experienceYears", "experienceYears.required", "Số năm kinh nghiệm không được bỏ trống");
            }
            if (!StringUtils.hasText(createStaff.getDoctorStatus())) {
                bindingResult.rejectValue("doctorStatus", "doctorStatus.required", "Trạng thái bác sĩ không được bỏ trống");
            }
            if (bindingResult.hasErrors()) {
                return null;
            }
        }

        //check staff age must suitable with national law
        LocalDate dob = createStaff.getDateOfBirth();
        LocalDate today = LocalDate.now();
        LocalDate maxDOB = today.minusYears(18);
        LocalDate minDOB = today.minusYears(65);
        if (dob.isBefore(minDOB) || dob.isAfter(maxDOB)) {
            bindingResult.rejectValue("dateOfBirth", "invalid.age", "Tuổi phải từ 18 đến 65 tuổi");
            return null;
        }

        if (!createStaff.getPassword().equals(createStaff.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "confirmPassword.conflix", "Mật khẩu không trùng khớp");
            return null;
        }

        User savedUser = userRepository.save(toUser(createStaff));
        String roleName = isDoctor ? "DOCTOR" : "RECEPTIONIST";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("role not found: " + roleName));

        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleId(savedUser.getId(), role.getId()));
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRole.setAssignedAt(LocalDateTime.now());
        userRoleRepository.save(userRole);

        return savedUser;

    }

    private User toUser(CreateStaffRequest createUser) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        boolean isDoctor = "doctor".equalsIgnoreCase(createUser.getStaffType());
        User user = new User();
        user.setUsername(createUser.getEmail());
        user.setFirstName(createUser.getFirstName());
        user.setMiddleName(createUser.getMiddleName());
        user.setLastName(createUser.getLastName());
        user.setEmail(createUser.getEmail());
        user.setPhone(createUser.getPhone());
        user.setBio(createUser.getBio());
        user.setPasswordHash(passwordEncoder.encode(createUser.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedBy(currentUser);
        user.setStatus(createUser.getAccountStatus() != null ? createUser.getAccountStatus() : "ACTIVE");
        user.setEmailVerified(false);
        user.setExperienceYears(0);

        // update Cloudinary here when the configuration process in done
        user.setAvatar(createUser.getAvatar());

        user.setGender(createUser.getGender());
        user.setDateOfBirth(createUser.getDateOfBirth());
        if (isDoctor) {
            Department department = departmentRepository.getReferenceById(createUser.getDepartmentId());
            user.setDepartment(department);
            user.setDegree(createUser.getDegree());
            user.setExperienceYears(createUser.getExperienceYears());
            user.setLicenseNumber(createUser.getLicenseNumber());
            user.setDoctorStatus(createUser.getDoctorStatus());
        }
        return user;
    }
}
