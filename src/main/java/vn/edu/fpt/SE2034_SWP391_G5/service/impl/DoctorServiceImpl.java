package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

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

    private DoctorResponse toResponse(User u) {
        return DoctorResponse.builder()
                .id(u.getId())
                .fullName(buildFullName(u.getLastName(), u.getMiddleName(), u.getFirstName()))
                .firstName(u.getFirstName())
                .middleName(u.getMiddleName())
                .lastName(u.getLastName())
                .degree(u.getDegree())
                .licenseNumber(u.getLicenseNumber())
                .bio(u.getBio())
                .avatar(u.getAvatar())
                .experienceYears(u.getExperienceYears())
                .departmentName(u.getDepartment() != null ? u.getDepartment().getName() : null)
                .departmentId(u.getDepartment() != null ? u.getDepartment().getId() : null)
                .doctorStatus(u.getDoctorStatus())
                .build();
    }

    private String buildFullName(String lastName, String middleName, String firstName) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (middleName != null) sb.append(middleName).append(" ");
        if (firstName != null) sb.append(firstName);
        return sb.toString().trim();
    }
}
