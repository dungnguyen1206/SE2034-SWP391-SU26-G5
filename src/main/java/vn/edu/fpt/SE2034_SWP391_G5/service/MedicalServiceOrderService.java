package vn.edu.fpt.SE2034_SWP391_G5.service;

import java.util.List;

public interface MedicalServiceOrderService {
    void saveServices(Long appointmentId, List<Long> serviceIds, Long doctorId);
    void saveServiceResult(Long appointmentId, Long orderId, String result, String note, Long doctorId);
    void deleteService(Long appointmentId, Long orderId, Long doctorId);
}
