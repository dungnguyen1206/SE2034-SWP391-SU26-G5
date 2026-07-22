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
import java.util.Collections;
import java.util.Objects;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoicePageWithStatsResponse;
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
    private final vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceOrderRepository medicalServiceOrderRepository;

    @Override
    public BigDecimal getTotalAmount(String paymentStatus, int month, int year) {
        return invoiceRepository.getTotalAmount(paymentStatus, month, year);
    }

    /*
     *
     *
     * ALL HERE RELATED TO INVOICE SCREEN FOR MANAGER
     *
     */
    @Override
    public InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year,
            LocalDate startDate, LocalDate endDate) {

        // Xử lí lỗi
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

        // Tất cả filter đều null -> Quét trọn ngày hôm nay
        if (year == null && month == null && startDate == null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(
                    paymentStatus,
                    LocalDate.now().atStartOfDay(),
                    LocalDate.now().plusDays(1).atStartOfDay(),
                    null,
                    null).orElse(null);
        }
        // 3. Có đủ cả khoảng ngày và tháng/năm
        else if ((year != null || month != null) && startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 4. Chỉ có startDate
        else if (startDate != null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(),
                    LocalDate.now().atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 5. Chỉ có endDate
        else if (startDate == null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, endDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 6. Không nhập khoảng ngày (Chỉ lọc theo Tháng / Năm)
        else if (startDate == null && endDate == null) {
            if (month == null && year != null) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, null, year)
                        .orElse(null);
            } else if (month != null && year == null) {
                result = invoiceRepository
                        .getTotalAmountByPaymentStatus(paymentStatus, null, null, month, Year.now().getValue())
                        .orElse(null);
            } else {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, year)
                        .orElse(null);
            }
        }
        // 7. Có cả hai ngày cụ thể
        else if (startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX), null, null).orElse(null);
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
    public Page<InvoiceRowResponse> invoiceRowResponses(Integer month, Integer year, LocalDate startDate,
            LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (month == null) {
            month = 0;
        }
        if (year == null) {
            year = 0;
        }
        // Xử lí lỗi
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
            return invoiceRepository.getInvoiceInforByFilter(LocalDate.now().atStartOfDay(),
                    LocalDate.now().plusDays(1).atStartOfDay(), null, null, pageable);
        }

        /*
         * Trường hợp customer nhập cả tháng năm + data range
         */

        if ((year != 0 || month != 0) && startDate != null && endDate != null) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    null, null, pageable);
        } else if (startDate != null && endDate == null) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(),
                    LocalDate.now().atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate == null && endDate != null) {
            return invoiceRepository.getInvoiceInforByFilter(endDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    null, null, pageable);
        } else if (startDate == null && endDate == null) {
            if (month == 0) {
                return invoiceRepository.getInvoiceInforByFilter(null, null, null, year, pageable);
            } else if (year == 0) {
                return invoiceRepository.getInvoiceInforByFilter(null, null, month, Year.now().getValue(), pageable);
            } else {
                return invoiceRepository.getInvoiceInforByFilter(null, null, month, year, pageable);

            }
        } else if (startDate != null && endDate != null && month == 0 && year == 0) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    null, null, pageable);
        } else {
            return invoiceRepository.getInvoiceInforByFilter(LocalDate.now().atStartOfDay(),
                    LocalDate.now().atTime(LocalTime.MAX), null, null, pageable);
        }
    }

    // ======================== LIST INVOICE RECEPTIONIST ========================
    @Override
    @Transactional(readOnly = true)
    public InvoicePageWithStatsResponse getInvoices(String keyword, String paymentStatus, int page, int size) {
        List<Appointment> allAppointments = appointmentRepository.findAllAppointmentsForBilling(keyword);

        List<InvoiceListResponse> allResponses = new java.util.ArrayList<>();

        long totalPaidCount = 0;
        long totalUnpaidCount = 0;

        for (Appointment a : allAppointments) {
            BigDecimal totalExpected = a.getService() != null ? a.getService().getReferencePrice() : BigDecimal.ZERO;
            if (a.getMedicalRecord() != null && a.getMedicalRecord().getMedicalServiceOrders() != null) {
                for (MedicalServiceOrder order : a.getMedicalRecord().getMedicalServiceOrders()) {
                    if (order.getPriceReference() != null && !"CANCELLED".equalsIgnoreCase(order.getStatus())) {
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
            if (unpaid.compareTo(BigDecimal.ZERO) < 0)
                unpaid = BigDecimal.ZERO;

            String status = unpaid.compareTo(BigDecimal.ZERO) > 0 ? "UNPAID" : "PAID";

            // Calculate stats before filtering by status
            if ("PAID".equals(status)) {
                totalPaidCount++;
            } else {
                totalUnpaidCount++;
            }

            // Filter by paymentStatus if provided
            if (paymentStatus != null && !paymentStatus.trim().isEmpty() && !paymentStatus.equalsIgnoreCase(status)) {
                continue;
            }

            BigDecimal displayAmount = "UNPAID".equals(status) ? unpaid : totalPaid;

            User patient = a.getPatient();
            String patientFullName = patient != null ? patient.getLastName() + " "
                    + (patient.getMiddleName() != null ? patient.getMiddleName() + " " : "") + patient.getFirstName()
                    : "-";
            String phone = patient != null && patient.getPhone() != null ? patient.getPhone() : "-";

            LocalDateTime createdAt = a.getCreatedAt();
            if (a.getInvoices() != null && !a.getInvoices().isEmpty()) {
                for (Invoice inv : a.getInvoices()) {
                    if (inv.getCreatedAt() != null && (createdAt == null || inv.getCreatedAt().isAfter(createdAt))) {
                        createdAt = inv.getCreatedAt();
                    }
                }
            } else if (a.getMedicalRecord() != null && a.getMedicalRecord().getMedicalServiceOrders() != null
                    && !a.getMedicalRecord().getMedicalServiceOrders().isEmpty()) {
                for (MedicalServiceOrder mso : a.getMedicalRecord().getMedicalServiceOrders()) {
                    if (mso.getCreateAt() != null && (createdAt == null || mso.getCreateAt().isAfter(createdAt))) {
                        createdAt = mso.getCreateAt();
                    }
                }
            } else if (a.getUpdatedAt() != null) {
                createdAt = a.getUpdatedAt();
            }

            allResponses.add(InvoiceListResponse.builder()
                    .appointmentId(a.getId())
                    .appointmentCode(a.getAppointmentCode())
                    .patientFullName(patientFullName)
                    .phone(phone)
                    .displayAmount(displayAmount)
                    .paymentStatus(status)
                    .createdAt(createdAt)
                    .build());
        }

        allResponses.sort((r1, r2) -> {
            // UNPAID trước, PAID sau
            int statusCompare = 0;
            if ("UNPAID".equals(r1.getPaymentStatus()) && "PAID".equals(r2.getPaymentStatus())) {
                statusCompare = -1;
            } else if ("PAID".equals(r1.getPaymentStatus()) && "UNPAID".equals(r2.getPaymentStatus())) {
                statusCompare = 1;
            }
            if (statusCompare != 0) {
                return statusCompare;
            }
            // Trong cùng nhóm trạng thái: thời gian mới nhất xếp lên đầu (gần hiện tại
            // nhất)
            if (r1.getCreatedAt() != null && r2.getCreatedAt() != null) {
                return r2.getCreatedAt().compareTo(r1.getCreatedAt());
            }
            return 0;
        });

        Pageable pageable = PageRequest.of(page - 1, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allResponses.size());

        List<InvoiceListResponse> pagedResponses = new java.util.ArrayList<>();
        if (start <= end && start < allResponses.size()) {
            pagedResponses = allResponses.subList(start, end);
        }

        Page<InvoiceListResponse> pagedResult = new org.springframework.data.domain.PageImpl<>(pagedResponses, pageable,
                allResponses.size());

        return InvoicePageWithStatsResponse.builder()
                .page(pagedResult)
                .totalPaidCount(totalPaidCount)
                .totalUnpaidCount(totalUnpaidCount)
                .build();
    }
    // ======================== END LIST INVOICE RECEPTIONIST
    // ========================

    // ======================== VIEW INVOICE DETAIL RECEPTIONIST
    // ========================
    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceDetail(Long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn có ID: " + appointmentId));

        User patient = a.getPatient();
        User doctor = a.getDoctor();
        String patientFullName = patient != null
                ? patient.getLastName() + " " + (patient.getMiddleName() != null ? patient.getMiddleName() + " " : "")
                        + patient.getFirstName()
                : "-";
        String doctorFullName = doctor != null
                ? doctor.getLastName() + " " + (doctor.getMiddleName() != null ? doctor.getMiddleName() + " " : "")
                        + doctor.getFirstName()
                : "-";

        String address = "-";
        if (patient != null && patient.getAddresses() != null && !patient.getAddresses().isEmpty()) {
            UserAddress userAddress = patient.getAddresses().stream()
                    .filter(UserAddress::getIsDefault)
                    .findFirst()
                    .orElse(patient.getAddresses().iterator().next());
            address = userAddress.getAddressLine()
                    + (userAddress.getProvince() != null ? ", " + userAddress.getProvince().getName() : "");
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

                if (!isOrderPaid && order.getPriceReference() != null && !"CANCELLED".equalsIgnoreCase(order.getStatus())) {
                    unpaidServices.add(InvoiceDetailResponse.UnpaidServiceDto.builder()
                            .id(order.getId())
                            .serviceName(
                                    order.getMedicalService() != null ? order.getMedicalService().getName() : "Dịch vụ")
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
                .departmentName(
                        doctor != null && doctor.getDepartment() != null ? doctor.getDepartment().getName() : "-")
                .roomNumber(a.getSlot() != null && a.getSlot().getSchedule() != null
                        && a.getSlot().getSchedule().getRoom() != null
                                ? a.getSlot().getSchedule().getRoom().getRoomNumber()
                                : "-")
                .diagnosis(a.getMedicalRecord() != null ? a.getMedicalRecord().getDiagnosis() : null)
                .paidInvoices(paidInvoices)
                .unpaidServices(unpaidServices)
                .totalPaidAmount(totalPaid)
                .totalUnpaidAmount(totalUnpaid)
                .build();
    }

    @Override
    @Transactional
    public void processPayment(Long appointmentId, String paymentMethod, boolean includeInitialFee,
            List<Long> selectedOrderIds) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + appointmentId));

        if (selectedOrderIds == null) {
            selectedOrderIds = Collections.emptyList();
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<InvoiceDetailResponse.UnpaidServiceDto> allUnpaid = getInvoiceDetail(appointmentId).getUnpaidServices();

        // Determine which items are selected for payment
        List<InvoiceDetailResponse.UnpaidServiceDto> selectedItems = new java.util.ArrayList<>();
        List<InvoiceDetailResponse.UnpaidServiceDto> rejectedItems = new java.util.ArrayList<>();

        for (InvoiceDetailResponse.UnpaidServiceDto unpaid : allUnpaid) {
            if ("APPOINTMENT".equals(unpaid.getType())) {
                // Initial exam fee
                if (includeInitialFee) {
                    selectedItems.add(unpaid);
                }
                // If not included - we just skip (no fee charged, bệnh nhân không đặt online)
            } else if ("ADDITIONAL".equals(unpaid.getType())) {
                // Additional service ordered by doctor
                if (selectedOrderIds.contains(unpaid.getId())) {
                    selectedItems.add(unpaid);
                } else {
                    rejectedItems.add(unpaid); // bệnh nhân từ chối làm dịch vụ này
                }
            }
        }

        if (selectedItems.isEmpty()) {
            throw new DataConflictException("Vui lòng chọn ít nhất một dịch vụ để thanh toán.");
        }

        // Calculate total of selected items only
        for (InvoiceDetailResponse.UnpaidServiceDto item : selectedItems) {
            if (item.getPrice() != null) {
                totalAmount = totalAmount.add(item.getPrice());
            }
        }

        // Create or reuse Invoice for selected items
        Invoice newInvoice = null;
        if (includeInitialFee && a.getInvoices() != null) {
            newInvoice = a.getInvoices().stream()
                    .filter(i -> "UNPAID".equalsIgnoreCase(i.getPaymentStatus()))
                    .findFirst()
                    .orElse(null);

            if (newInvoice != null && newInvoice.getInvoiceItems() != null) {
                invoiceItemRepository.deleteAll(newInvoice.getInvoiceItems());
                newInvoice.getInvoiceItems().clear();
            }
        }

        if (newInvoice == null) {
            newInvoice = new Invoice();
            newInvoice.setAppointment(a);
            newInvoice.setInvoiceCode("INV-" + System.currentTimeMillis());
            newInvoice.setCreatedAt(LocalDateTime.now());
        }

        newInvoice.setTotalAmount(totalAmount);
        newInvoice.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CASH");
        newInvoice.setPaymentStatus("PAID");
        newInvoice.setPaidAt(LocalDateTime.now());
        newInvoice.setUpdatedAt(LocalDateTime.now());
        invoiceRepository.save(newInvoice);

        // Create InvoiceItems for selected services
        for (InvoiceDetailResponse.UnpaidServiceDto selected : selectedItems) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(newInvoice);
            item.setItemName(selected.getServiceName());
            item.setPriceApplied(selected.getPrice());
            item.setQuantity(1);
            item.setLineTotal(selected.getPrice());

            if ("ADDITIONAL".equals(selected.getType()) && a.getMedicalRecord() != null) {
                MedicalServiceOrder order = a.getMedicalRecord().getMedicalServiceOrders().stream()
                        .filter(o -> o.getId().equals(selected.getId())).findFirst().orElse(null);
                if (order != null) {
                    item.setMedicalServiceOrder(order);
                    item.setService(order.getMedicalService());
                    order.setStatus("PAID"); // Đánh dấu dịch vụ đã thanh toán
                    medicalServiceOrderRepository.save(order);
                }
            } else if ("APPOINTMENT".equals(selected.getType())) {
                item.setService(a.getService());
            }
            invoiceItemRepository.save(item);
        }

        // Cancel rejected additional services (bệnh nhân đổi ý, không muốn làm)
        // Lễ tân không tự hủy dịch vụ khi thanh toán, để dự phòng bệnh nhân đổi ý
        // Dịch vụ chưa thanh toán sẽ tự động bị hủy khi Bác sĩ Hoàn thành lịch hẹn

    }
    // ======================== END VIEW INVOICE DETAIL RECEPTIONIST
    // ========================
}
