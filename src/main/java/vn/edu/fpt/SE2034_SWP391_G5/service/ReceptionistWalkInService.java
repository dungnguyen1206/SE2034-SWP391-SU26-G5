package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.WalkInBookingRequest;

public interface ReceptionistWalkInService {
    Object searchPatientByPhone(String phone);
    void createWalkInAppointment(WalkInBookingRequest request);
    java.util.List<java.util.Map<String, Object>> getAvailableSlots(Integer departmentId, java.time.LocalDate date);
}
