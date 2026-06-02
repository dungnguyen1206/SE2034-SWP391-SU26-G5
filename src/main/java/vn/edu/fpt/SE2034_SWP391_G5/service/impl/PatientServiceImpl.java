package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.List;
@Service
public class PatientServiceImpl implements PatientService {

    private UserRepository userRepository;
    public PatientServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   public List<User> findUsersByRoleName(String roleName){
        return userRepository.findByRoleName(roleName);
    };

}
