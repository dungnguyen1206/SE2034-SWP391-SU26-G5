package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalServiceOrder;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceOrderRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceOrderService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceOrderServiceImpl implements MedicalServiceOrderService {

    private final AppointmentService appointmentService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final MedicalServiceOrderRepository medicalServiceOrderRepository;

    @Override
    @Transactional
    public void saveServices(Long appointmentId, List<Long> serviceIds, Long doctorId) {
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(appointmentId);

        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật dịch vụ cho lịch hẹn này");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new AccessDeniedException("Cuộc hẹn đã hoàn thành, không thể thêm dịch vụ khám");
        }

        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElse(null);

        if (medicalRecord == null) {
            throw new BadRequestException("Không thể thêm dịch vụ. Vui lòng tạo hồ sơ bệnh án trước.");
        }

        if (serviceIds != null && !serviceIds.isEmpty()) {
            for (Long sId : serviceIds) {
                // Check if this service is already ordered
                boolean alreadyExists = medicalRecord.getMedicalServiceOrders() != null &&
                        medicalRecord.getMedicalServiceOrders().stream()
                                .anyMatch(o -> o.getMedicalService().getId().equals(sId));

                if (alreadyExists) {
                    continue;
                }

                MedicalService service = medicalServiceRepository.findById(sId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với ID: " + sId));

                MedicalServiceOrder order = MedicalServiceOrder.builder()
                        .medicalRecord(medicalRecord)
                        .medicalService(service)
                        .priceReference(service.getReferencePrice())
                        .status("PENDING_PAYMENT")
                        .note(null)
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .build();

                medicalServiceOrderRepository.save(order);
            }
        } else {
            throw new BadRequestException("Vui lòng chọn ít nhất một dịch vụ");
        }
    }

    @Override
    @Transactional
    public void saveServiceResult(Long appointmentId, Long orderId, String result, String note, Long doctorId) {
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(appointmentId);

        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật kết quả cho lịch hẹn này");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new AccessDeniedException("Cuộc hẹn đã hoàn thành, không thể cập nhật kết quả dịch vụ");
        }

        MedicalServiceOrder order = medicalServiceOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chỉ định dịch vụ với ID: " + orderId));

        // Ensure this order belongs to the correct medical record of this appointment
        if (!order.getMedicalRecord().getAppointment().getId().equals(appointmentId)) {
            throw new AccessDeniedException("Chỉ định dịch vụ này không thuộc về lịch hẹn");
        }

        // Ensure the service order is paid (status is not PENDING_PAYMENT and not CANCELLED)
        if ("PENDING_PAYMENT".equals(order.getStatus())) {
            throw new AccessDeniedException("Dịch vụ chưa được thanh toán, không thể cập nhật kết quả");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            throw new AccessDeniedException("Dịch vụ đã bị hủy, không thể cập nhật kết quả");
        }

        // Update result, note and status
        order.setNote(note);
        order.setResult(result);
        if (result != null && !result.trim().isEmpty()) {
            if (!"PENDING_PAYMENT".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus())) {
                order.setStatus("COMPLETED");
            }
        }
        order.setUpdateAt(LocalDateTime.now());

        medicalServiceOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteService(Long appointmentId, Long orderId, Long doctorId) {
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(appointmentId);

        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new AccessDeniedException("Bạn không có quyền xóa dịch vụ cho lịch hẹn này");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new AccessDeniedException("Cuộc hẹn đã hoàn thành, không thể xóa dịch vụ khám");
        }

        MedicalServiceOrder order = medicalServiceOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chỉ định dịch vụ với ID: " + orderId));

        // Ensure this order belongs to the correct medical record of this appointment
        if (!order.getMedicalRecord().getAppointment().getId().equals(appointmentId)) {
            throw new AccessDeniedException("Chỉ định dịch vụ này không thuộc về lịch hẹn");
        }

        // Only allow deletion if status is PENDING_PAYMENT (unpaid)
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BadRequestException("Dịch vụ đã được thanh toán hoặc đã thực hiện, không thể xóa");
        }

        medicalServiceOrderRepository.delete(order);
    }
}
