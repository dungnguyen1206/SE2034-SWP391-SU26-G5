package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
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

}
