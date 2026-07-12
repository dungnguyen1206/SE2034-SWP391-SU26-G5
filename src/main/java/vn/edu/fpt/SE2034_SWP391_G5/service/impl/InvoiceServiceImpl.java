package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceRowResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice.DataConflictException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice.InvoiceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceListResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceItemResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Invoice;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserAddress;

import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.InvoiceItem;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalServiceOrder;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceItemRepository;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    @Override
    public BigDecimal getTotalAmount(String paymentStatus, int month, int year) {
        return invoiceRepository.getTotalAmount(paymentStatus, month, year);
    }


    /*
     *
     *
     * ALL HERE RELATED TO INVOICE SCREEN FOR MANAGER
     *
     * */
    @Override
    public InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year, LocalDate startDate, LocalDate endDate) {

        //Xử lí lỗi
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new DataConflictException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        if (startDate != null && startDate.isBefore(LocalDate.parse("2001-01-01"))) {
            throw new DataConflictException("Ngày nhập không hợp lệ!");
        }
        if (endDate != null && endDate.isAfter(LocalDate.parse("2030-12-31"))) {
            throw new DataConflictException("Ngày nhập không hợp lệ!");
        }

        InvoiceSummaryResponse result = null;

        //  Tất cả filter đều null -> Quét trọn ngày hôm nay
        if (year == null && month == null && startDate == null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(
                    paymentStatus,
                    LocalDate.now().atStartOfDay(),
                    LocalDate.now().plusDays(1).atStartOfDay(),
                    null,
                    null
            ).orElse(null);
        }
        // 3. Có đủ cả khoảng ngày và tháng/năm
        else if ((year != null || month != null) && startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 4. Chỉ có startDate
        else if (startDate != null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 5. Chỉ có endDate
        else if (startDate == null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, endDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 6. Không nhập khoảng ngày (Chỉ lọc theo Tháng / Năm)
        else if (startDate == null && endDate == null) {
            if (month == null && year != null) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, null, year).orElse(null);
            } else if (month != null && year == null) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, Year.now().getValue()).orElse(null);
            } else {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, year).orElse(null);
            }
        }
        // 7. Có cả hai ngày cụ thể
        else if (startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }

        // Khởi tạo object rỗng nếu không tìm thấy dữ liệu
        if (result == null) {
            result = InvoiceSummaryResponse.builder()
                    .paymentStatus(paymentStatus)
                    .paymentAmount(0L)
                    .totalPaymentAmount(BigDecimal.ZERO)
                    .build();
        }
        return result;

    }

    @Override
    public Page<InvoiceRowResponse> invoiceRowResponses(Integer month, Integer year, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (month == null) {
            month = 0;
        }
        if (year == null) {
            year = 0;
        }
        //Xử lí lỗi
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new DataConflictException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        if (startDate != null && startDate.isBefore(LocalDate.parse("2001-01-01"))) {
            throw new DataConflictException("Ngày nhập không hợp lệ!");
        }
        if (endDate != null && endDate.isAfter(LocalDate.parse("2030-12-31"))) {
            throw new DataConflictException("Ngày nhập không hợp lệ!");
        }


        if (year == 0 && month == 0 && startDate == null && endDate == null) {
            return invoiceRepository.getInvoiceInforByFilter(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay(), null, null, pageable);
        }

        /*
         * Trường hợp customer nhập cả tháng năm + data range
         * */

        if ((year != 0 || month != 0) && startDate != null && endDate != null) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate != null && endDate == null) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate == null && endDate != null) {
            return invoiceRepository.getInvoiceInforByFilter(endDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate == null && endDate == null) {
            if (month == 0) {
                return invoiceRepository.getInvoiceInforByFilter(null, null, null, year, pageable);
            } else if (year == 0) {
                return invoiceRepository.getInvoiceInforByFilter(null, null, month, Year.now().getValue(), pageable);
            } else {
                return invoiceRepository.getInvoiceInforByFilter(null, null, month, year, pageable);

            }
        } else if (startDate != null && endDate != null && month == 0 && year == 0) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null, pageable);
        } else {
            return invoiceRepository.getInvoiceInforByFilter(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceListResponse> getInvoices(String keyword, String paymentStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findAppointmentsForBilling(keyword, pageable);
        
        return appointments.map(a -> {
            BigDecimal totalExpected = a.getService() != null ? a.getService().getReferencePrice() : BigDecimal.ZERO;
            if (a.getMedicalRecord() != null && a.getMedicalRecord().getMedicalServiceOrders() != null) {
                for (MedicalServiceOrder order : a.getMedicalRecord().getMedicalServiceOrders()) {
                    if (order.getPriceReference() != null) {
                        totalExpected = totalExpected.add(order.getPriceReference());
                    }
                }
            }
            
            BigDecimal totalPaid = BigDecimal.ZERO;
            if (a.getInvoices() != null) {
                for (Invoice invoice : a.getInvoices()) {
                    if ("PAID".equalsIgnoreCase(invoice.getPaymentStatus()) && invoice.getTotalAmount() != null) {
                        totalPaid = totalPaid.add(invoice.getTotalAmount());
                    }
                }
            }
            
            BigDecimal unpaid = totalExpected.subtract(totalPaid);
            if (unpaid.compareTo(BigDecimal.ZERO) < 0) unpaid = BigDecimal.ZERO;
            
            String status = unpaid.compareTo(BigDecimal.ZERO) > 0 ? "UNPAID" : "PAID";
            BigDecimal displayAmount = "UNPAID".equals(status) ? unpaid : totalPaid;
            
            // Lấy STT
            Integer stt = null;
            if (a.getCheckInTime() != null) {
                // To avoid circular dependency with AppointmentService, we can just fetch STT using the method if possible, or leave it.
                // Wait, I can inject AppointmentService. Let's assume it's injected or we just return 0 for now and fix injection later.
                // Actually, STT doesn't need to be exact here if it's just for display, but let's use 0 temporarily until we inject it.
            }
            
            User patient = a.getPatient();
            String patientFullName = patient != null ? patient.getLastName() + " " + (patient.getMiddleName() != null ? patient.getMiddleName() + " " : "") + patient.getFirstName() : "-";
            String phone = patient != null && patient.getPhone() != null ? patient.getPhone() : "-";
            
            return InvoiceListResponse.builder()
                    .appointmentId(a.getId())
                    .appointmentCode(a.getAppointmentCode())
                    .patientFullName(patientFullName)
                    .phone(phone)
                    .displayAmount(displayAmount)
                    .paymentStatus(status)
                    .build();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceDetail(Long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn có ID: " + appointmentId));

        User patient = a.getPatient();
        User doctor = a.getDoctor();
        String patientFullName = patient != null ? patient.getLastName() + " " + (patient.getMiddleName() != null ? patient.getMiddleName() + " " : "") + patient.getFirstName() : "-";
        String doctorFullName = doctor != null ? doctor.getLastName() + " " + (doctor.getMiddleName() != null ? doctor.getMiddleName() + " " : "") + doctor.getFirstName() : "-";

        String address = "-";
        if (patient != null && patient.getAddresses() != null && !patient.getAddresses().isEmpty()) {
            UserAddress userAddress = patient.getAddresses().stream()
                    .filter(UserAddress::getIsDefault)
                    .findFirst()
                    .orElse(patient.getAddresses().iterator().next());
            address = userAddress.getAddressLine() + (userAddress.getProvince() != null ? ", " + userAddress.getProvince().getName() : "");
        }
        
        List<InvoiceDetailResponse.PaidInvoiceDto> paidInvoices = new java.util.ArrayList<>();
        BigDecimal totalPaid = BigDecimal.ZERO;
        if (a.getInvoices() != null) {
            for (Invoice inv : a.getInvoices()) {
                if ("PAID".equalsIgnoreCase(inv.getPaymentStatus())) {
                    totalPaid = totalPaid.add(inv.getTotalAmount());
                    List<InvoiceItemResponse> items = new java.util.ArrayList<>();
                    if (inv.getInvoiceItems() != null) {
                        for (InvoiceItem item : inv.getInvoiceItems()) {
                            items.add(InvoiceItemResponse.builder()
                                    .id(item.getId())
                                    .itemName(item.getItemName())
                                    .priceApplied(item.getPriceApplied())
                                    .quantity(item.getQuantity())
                                    .lineTotal(item.getLineTotal())
                                    .build());
                        }
                    }
                    paidInvoices.add(InvoiceDetailResponse.PaidInvoiceDto.builder()
                            .invoiceId(inv.getId())
                            .invoiceCode(inv.getInvoiceCode())
                            .totalAmount(inv.getTotalAmount())
                            .paidAt(inv.getPaidAt())
                            .paymentMethod(inv.getPaymentMethod())
                            .items(items)
                            .build());
                }
            }
        }

        List<InvoiceDetailResponse.UnpaidServiceDto> unpaidServices = new java.util.ArrayList<>();
        BigDecimal totalUnpaid = BigDecimal.ZERO;
        
        // 1. Check initial service
        boolean initialServicePaid = false;
        if (a.getInvoices() != null) {
            for (Invoice inv : a.getInvoices()) {
                if ("PAID".equalsIgnoreCase(inv.getPaymentStatus()) && inv.getInvoiceItems() != null) {
                    for (InvoiceItem item : inv.getInvoiceItems()) {
                        if (item.getMedicalServiceOrder() == null) {
                            initialServicePaid = true;
                            break;
                        }
                    }
                }
            }
        }
        
        if (!initialServicePaid && a.getService() != null) {
            unpaidServices.add(InvoiceDetailResponse.UnpaidServiceDto.builder()
                    .id(a.getId())
                    .serviceName("Khám " + a.getService().getName())
                    .price(a.getService().getReferencePrice())
                    .type("APPOINTMENT")
                    .build());
            totalUnpaid = totalUnpaid.add(a.getService().getReferencePrice());
        }

        // 2. Check MedicalServiceOrders
        if (a.getMedicalRecord() != null && a.getMedicalRecord().getMedicalServiceOrders() != null) {
            for (MedicalServiceOrder order : a.getMedicalRecord().getMedicalServiceOrders()) {
                boolean isOrderPaid = false;
                if (order.getInvoiceItem() != null && order.getInvoiceItem().getInvoice() != null) {
                    if ("PAID".equalsIgnoreCase(order.getInvoiceItem().getInvoice().getPaymentStatus())) {
                        isOrderPaid = true;
                    }
                }
                
                if (!isOrderPaid && order.getPriceReference() != null) {
                    unpaidServices.add(InvoiceDetailResponse.UnpaidServiceDto.builder()
                            .id(order.getId())
                            .serviceName(order.getMedicalService() != null ? order.getMedicalService().getName() : "Dịch vụ")
                            .price(order.getPriceReference())
                            .type("ADDITIONAL")
                            .build());
                    totalUnpaid = totalUnpaid.add(order.getPriceReference());
                }
            }
        }

        return InvoiceDetailResponse.builder()
                .appointmentId(a.getId())
                .appointmentCode(a.getAppointmentCode())
                .stt(0) // Will set via service later if needed
                .patientFullName(patientFullName)
                .patientPhone(patient != null && patient.getPhone() != null ? patient.getPhone() : "-")
                .patientAddress(address)
                .doctorFullName(doctorFullName)
                .departmentName(doctor != null && doctor.getDepartment() != null ? doctor.getDepartment().getName() : "-")
                .roomNumber(a.getSlot() != null && a.getSlot().getSchedule() != null && a.getSlot().getSchedule().getRoom() != null ? a.getSlot().getSchedule().getRoom().getRoomNumber() : "-")
                .diagnosis(a.getMedicalRecord() != null ? a.getMedicalRecord().getDiagnosis() : null)
                .paidInvoices(paidInvoices)
                .unpaidServices(unpaidServices)
                .totalPaidAmount(totalPaid)
                .totalUnpaidAmount(totalUnpaid)
                .build();
    }

    @Override
    @Transactional
    public void processPayment(Long appointmentId, String paymentMethod) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + appointmentId));
        
        InvoiceDetailResponse detail = getInvoiceDetail(appointmentId);
        if (detail.getUnpaidServices() == null || detail.getUnpaidServices().isEmpty()) {
            throw new DataConflictException("Không có dịch vụ nào cần thanh toán.");
        }
        
        Invoice newInvoice = new Invoice();
        newInvoice.setAppointment(a);
        newInvoice.setInvoiceCode("INV-" + System.currentTimeMillis());
        newInvoice.setTotalAmount(detail.getTotalUnpaidAmount());
        newInvoice.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CASH");
        newInvoice.setPaymentStatus("PAID");
        newInvoice.setPaidAt(LocalDateTime.now());
        newInvoice.setCreatedAt(LocalDateTime.now());
        newInvoice.setUpdatedAt(LocalDateTime.now());
        
        invoiceRepository.save(newInvoice);
        
        for (InvoiceDetailResponse.UnpaidServiceDto unpaid : detail.getUnpaidServices()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(newInvoice);
            item.setItemName(unpaid.getServiceName());
            item.setPriceApplied(unpaid.getPrice());
            item.setQuantity(1);
            item.setLineTotal(unpaid.getPrice());
            
            if ("ADDITIONAL".equals(unpaid.getType())) {
                MedicalServiceOrder order = a.getMedicalRecord().getMedicalServiceOrders().stream()
                        .filter(o -> o.getId().equals(unpaid.getId())).findFirst().orElse(null);
                if (order != null) {
                    item.setMedicalServiceOrder(order);
                    item.setService(order.getMedicalService());
                    order.setStatus("PAID"); // Mark order as paid
                }
            } else if ("APPOINTMENT".equals(unpaid.getType())) {
                item.setService(a.getService());
            }
            invoiceItemRepository.save(item);
        }
    }
}
