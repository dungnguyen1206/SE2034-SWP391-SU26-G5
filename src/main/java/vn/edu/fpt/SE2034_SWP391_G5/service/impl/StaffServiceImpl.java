package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.StaffService;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    public StaffServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //find all active staff
    public  List<StaffResponse> findStaff(String roleName, String filterKey) {
       return userRepository.findActiveStaffList(roleName,filterKey);
    }


}
