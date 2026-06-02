package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;


import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface ReceptionistService {
    List<User> getAllReceptionist(String role);

    List<User> findByRoleNameAndStatus(String roleName, String status);

    ReceptionistResponse getReceptionistByUsername(String username);
}


