package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;

public interface ReceptionistService {

    ReceptionistResponse getReceptionistByUsername(String username);
}