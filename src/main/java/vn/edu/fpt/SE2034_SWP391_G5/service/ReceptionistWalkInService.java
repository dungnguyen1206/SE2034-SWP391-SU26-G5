package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.WalkInBookingRequest;

public interface ReceptionistWalkInService {
    // ======================== WALK-IN BOOKING RECEPTIONIST ========================
    Object searchPatientByPhone(String phone);
    Long createWalkInAppointment(WalkInBookingRequest request);
    java.util.List<java.util.Map<String, Object>> getAvailableSlots(Integer departmentId, java.time.LocalDate date);
    // ======================== END WALK-IN BOOKING RECEPTIONIST ========================
}
