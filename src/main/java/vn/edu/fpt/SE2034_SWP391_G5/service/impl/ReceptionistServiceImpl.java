package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.util.List;

@Service

public class ReceptionistServiceImpl implements ReceptionistService {

    private final UserRepository userRepository;

    public ReceptionistServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Find all receptionist
    public  List<User> getAllReceptionist(String role){
        return userRepository.findByRoleName(role);
    };


    //find all active receptionist
    public  List<User> findByRoleNameAndStatus(String roleName, String status) {
        return userRepository.countByRoleNameAndStatus(roleName, status);
    }

    @Override
    public ReceptionistResponse getReceptionistByUsername(String email) {
        List<Object[]> result = userRepository.findReceptionistInfoByEmail(email);

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("Không tìm thấy nhân viên tiếp tân với email: " + email);
        }

        Object[] row = result.get(0);

        Long id = ((Number) row[0]).longValue();
        String fullName = (String) row[1];
        String avatarText = (String) row[2];

        return new ReceptionistResponse(id, fullName, avatarText);
    }
}