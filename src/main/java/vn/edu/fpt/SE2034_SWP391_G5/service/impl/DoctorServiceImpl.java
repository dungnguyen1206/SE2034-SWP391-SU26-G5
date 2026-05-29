package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
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

}
