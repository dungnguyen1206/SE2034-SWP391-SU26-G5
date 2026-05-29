package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface DoctorService {
    List<User> findByDoctorStatus(String doctorStatus);

}
