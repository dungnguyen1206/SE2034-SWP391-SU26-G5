package vn.edu.fpt.SE2034_SWP391_G5.service;

import java.util.List;

public interface MedicalServiceOrderService {
    void createServiceOrders(Long appointmentId, Long doctorId, List<Long> serviceIds);
    void updateServiceOrderResult(Long appointmentId, Long doctorId, Long orderId, String result, String note);
    void deleteServiceOrder(Long appointmentId, Long doctorId, Long orderId);
}
