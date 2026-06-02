package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;

    @Override
    public List<DoctorResponse> getDoctorsByDepartment(Integer departmentId) {
        List<User> users = userRepository.findByDepartmentId(departmentId);
        return users.stream()
                .map(this::mapToDoctorResponse)
                .toList();
    }

    @Override
    public DoctorResponse getDoctorById(Long doctorId) {
        User user = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + doctorId));
        return mapToDoctorResponse(user);
    }

    private DoctorResponse mapToDoctorResponse(User user) {
        if (user == null) return null;
        String fullName = buildFullName(user.getLastName(), user.getMiddleName(), user.getFirstName());
        return DoctorResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .fullName(fullName)
                .degree(user.getDegree())
                .experienceYears(user.getExperienceYears())
                .bio(user.getBio())
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
