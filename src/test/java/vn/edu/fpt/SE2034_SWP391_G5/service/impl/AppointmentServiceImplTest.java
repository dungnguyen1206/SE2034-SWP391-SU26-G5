package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Invoice;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceOrderRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private MedicalServiceOrderRepository medicalServiceOrderRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void testUpdateAppointmentStatusToCompleted_ShouldPayUnpaidInvoices() {
        // Arrange
        Long appointmentId = 1L;
        Long patientId = 2L;

        User patient = new User();
        patient.setId(patientId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setPatient(patient);
        appointment.setStatus("EXAMINING");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setStatus("FINALIZED");
        appointment.setMedicalRecord(medicalRecord);

        Invoice unpaidInvoice = new Invoice();
        unpaidInvoice.setId(10L);
        unpaidInvoice.setPaymentStatus("UNPAID");
        unpaidInvoice.setAppointment(appointment);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(invoiceRepository.findByPatientIdAndPaymentStatus(patientId, "UNPAID"))
                .thenReturn(List.of(unpaidInvoice));

        // Act
        appointmentService.updateAppointmentStatus(appointmentId, "COMPLETED");

        // Assert
        assertEquals("COMPLETED", appointment.getStatus());
        assertEquals("PAID", unpaidInvoice.getPaymentStatus());
        assertNotNull(unpaidInvoice.getPaidAt());
        assertNotNull(unpaidInvoice.getUpdatedAt());

        verify(invoiceRepository).save(unpaidInvoice);
        verify(appointmentRepository).save(appointment);
    }
}
