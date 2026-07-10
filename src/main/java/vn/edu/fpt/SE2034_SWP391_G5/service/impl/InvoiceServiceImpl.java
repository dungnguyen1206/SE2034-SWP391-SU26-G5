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

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

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
        if (startDate != null && endDate != null & startDate.isAfter(endDate)) {
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
        if (startDate != null && endDate != null & startDate.isAfter(endDate)) {
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
        return invoiceRepository.findInvoicesWithFilter(keyword, paymentStatus, pageable)
                .map(invoice -> InvoiceListResponse.builder()
                        .id(invoice.getId())
                        .invoiceCode(invoice.getInvoiceCode())
                        .patientFullName(invoice.getMedicalRecord().getAppointment().getPatient().getLastName() + " " +
                                         (invoice.getMedicalRecord().getAppointment().getPatient().getMiddleName() != null ? invoice.getMedicalRecord().getAppointment().getPatient().getMiddleName() + " " : "") +
                                         invoice.getMedicalRecord().getAppointment().getPatient().getFirstName())
                        .appointmentCode(invoice.getMedicalRecord().getAppointment().getAppointmentCode())
                        .totalAmount(invoice.getTotalAmount())
                        .paymentStatus(invoice.getPaymentStatus())
                        .paymentMethod(invoice.getPaymentMethod())
                        .paidAt(invoice.getPaidAt())
                        .createdAt(invoice.getCreatedAt())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceDetail(Long id) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Không tìm thấy hóa đơn có ID: " + id));

        List<InvoiceItemResponse> itemResponses = Collections.emptyList();
        if (invoice.getInvoiceItems() != null) {
            itemResponses = invoice.getInvoiceItems().stream()
                    .map(item -> InvoiceItemResponse.builder()
                            .id(item.getId())
                            .itemName(item.getItemName())
                            .priceApplied(item.getPriceApplied())
                            .quantity(item.getQuantity())
                            .lineTotal(item.getLineTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        User patient = invoice.getMedicalRecord().getAppointment().getPatient();
        User doctor = invoice.getMedicalRecord().getAppointment().getDoctor();
        String patientFullName = patient != null ? patient.getLastName() + " " + (patient.getMiddleName() != null ? patient.getMiddleName() + " " : "") + patient.getFirstName() : "-";
        String doctorFullName = doctor != null ? doctor.getLastName() + " " + (doctor.getMiddleName() != null ? doctor.getMiddleName() + " " : "") + doctor.getFirstName() : "-";

        // Safe address extraction
        String address = "-";
        if (patient != null && patient.getAddresses() != null && !patient.getAddresses().isEmpty()) {
            UserAddress userAddress = patient.getAddresses().stream()
                    .filter(UserAddress::getIsDefault)
                    .findFirst()
                    .orElse(patient.getAddresses().iterator().next());
            address = userAddress.getAddressLine() + (userAddress.getProvince() != null ? ", " + userAddress.getProvince().getName() : "");
        }

        return InvoiceDetailResponse.builder()
                .id(invoice.getId())
                .invoiceCode(invoice.getInvoiceCode())
                .patientFullName(patientFullName)
                .patientPhone(patient.getPhone() != null ? patient.getPhone() : "-")
                .patientAddress(address)
                .appointmentCode(invoice.getMedicalRecord().getAppointment().getAppointmentCode())
                .doctorFullName(doctorFullName)
                .departmentName(doctor != null && doctor.getDepartment() != null ? doctor.getDepartment().getName() : "-")
                .roomNumber(invoice.getMedicalRecord().getAppointment().getSlot() != null && invoice.getMedicalRecord().getAppointment().getSlot().getSchedule() != null && invoice.getMedicalRecord().getAppointment().getSlot().getSchedule().getRoom() != null ? invoice.getMedicalRecord().getAppointment().getSlot().getSchedule().getRoom().getRoomNumber() : "-")
                .diagnosis(invoice.getMedicalRecord().getDiagnosis())
                .totalAmount(invoice.getTotalAmount())
                .paymentStatus(invoice.getPaymentStatus())
                .paymentMethod(invoice.getPaymentMethod())
                .paidAt(invoice.getPaidAt())
                .createdAt(invoice.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    @Override
    @Transactional
    public void processPayment(Long invoiceId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Không tìm thấy hóa đơn có ID: " + invoiceId));

        if ("PAID".equalsIgnoreCase(invoice.getPaymentStatus())) {
            throw new DataConflictException("Hóa đơn này đã được thanh toán.");
        }

        invoice.setPaymentStatus("PAID");
        invoice.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CASH");
        invoice.setPaidAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }
}
