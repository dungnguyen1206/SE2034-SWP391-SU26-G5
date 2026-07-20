package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private UserRepository userRepository;

    public DoctorServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> findByDoctorStatus(String doctorStatus){
        return  userRepository.findByDoctorStatus(doctorStatus);
    }

    //Count active doctor
    public List<User> findByRoleNameAndStatus(String roleName, String status) {
        return userRepository.countByRoleNameAndStatus(roleName, status);
    }


    @Override
    public List<DoctorResponse> getDoctorsByDepartment(Integer departmentId) {
        return userRepository.findActiveDoctorsByDepartmentId(departmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DoctorResponse getDoctorById(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với id: " + doctorId));
        return toResponse(doctor);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<User> users = userRepository.countByRoleNameAndStatus("DOCTOR","ACTIVE");
        List<DoctorResponse> doctorResponses = new ArrayList<>();
        users.forEach(user -> doctorResponses.add(toResponse(user)));
        return doctorResponses;
    }

    @Override
    public  DoctorResponse toResponse(User u) {
        // Tính experienceYears từ licenseIssueDate
        Integer experienceYears = 0;
        if (u.getLicenseIssueDate() != null) {
            experienceYears = LocalDate.now().getYear() - u.getLicenseIssueDate().getYear();
            experienceYears = Math.max(0, experienceYears); // Đảm bảo không âm
        }
        
        return DoctorResponse.builder()
                .id(u.getId())
                .fullName(buildFullName(u.getFirstName(), u.getMiddleName(), u.getLastName()))
                .firstName(u.getFirstName())
                .middleName(u.getMiddleName())
                .lastName(u.getLastName())
                .degree(u.getDegree())
                .licenseNumber(u.getLicenseNumber())
                .bio(u.getBio())
                .avatar(u.getAvatar())
                .experienceYears(experienceYears)
                .departmentName(u.getDepartment() != null ? u.getDepartment().getName() : null)
                .departmentId(u.getDepartment() != null ? u.getDepartment().getId() : null)
                .doctorStatus(u.getDoctorStatus())
                .build();
    }

    @Override
    public String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName).append(" ");
        if (middleName != null) sb.append(middleName).append(" ");
        if (lastName != null) sb.append(lastName);
        return sb.toString().trim();
    }

    @Override
    public Page<DoctorResponse> getActiveDoctorsPaginated(Integer departmentId, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findActiveDoctorsPaginated(departmentId, search, pageable);
        return userPage.map(this::toResponse);
    }
}
