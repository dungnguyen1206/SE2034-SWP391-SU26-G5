package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.TimeSlotRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.EmailService;
import vn.edu.fpt.SE2034_SWP391_G5.service.NotificationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentBookingServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorScheduleRepository doctorScheduleRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private UserRepository userRepository;
    @Mock private MedicalServiceRepository medicalServiceRepository;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User patient;
    private User doctor;
    private MedicalService medicalService;
    private TimeSlot slot;
    private DoctorSchedule schedule;
    private CreateAppointmentRequest request;

    @BeforeEach
    void setUp() {
        Department department = new Department();
        department.setId(1);
        department.setName("Khoa Nội");
        department.setStatus("ACTIVE");

        patient = new User();
        patient.setId(10L);
        patient.setFirstName("An");

        doctor = new User();
        doctor.setId(20L);
        doctor.setFirstName("Bình");
        doctor.setStatus("ACTIVE");
        doctor.setDoctorStatus("ACTIVE");
        doctor.setDepartment(department);

        WeekSchedule weekSchedule = new WeekSchedule();
        weekSchedule.setId(30L);
        weekSchedule.setStatus("FINALIZED");

        schedule = new DoctorSchedule();
        schedule.setId(40L);
        schedule.setDoctor(doctor);
        schedule.setWorkDate(LocalDate.now().plusDays(1));
        schedule.setStatus("ACTIVE");
        schedule.setWeekSchedule(weekSchedule);

        slot = new TimeSlot();
        slot.setId(50L);
        slot.setSchedule(schedule);
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(9, 30));
        slot.setBookedCapacity(0);
        slot.setMaxCapacity(5);
        slot.setStatus("AVAILABLE");
        slot.setVersion(0L);

        medicalService = new MedicalService();
        medicalService.setId(60L);
        medicalService.setName("Khám tổng quát");
        medicalService.setDepartment(department);
        medicalService.setStatus("ACTIVE");

        request = new CreateAppointmentRequest();
        request.setDoctorId(doctor.getId());
        request.setDepartmentId(department.getId());
        request.setServiceId(medicalService.getId());
        request.setSlotId(slot.getId());

        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(userRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(medicalServiceRepository.findById(medicalService.getId())).thenReturn(Optional.of(medicalService));
        when(timeSlotRepository.findByIdWithSchedule(slot.getId())).thenReturn(Optional.of(slot));
    }

    @Test
    void shouldRejectSlotThatDoesNotBelongToSelectedDoctor() {
        User anotherDoctor = new User();
        anotherDoctor.setId(21L);
        schedule.setDoctor(anotherDoctor);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> appointmentService.bookAppointment(patient.getId(), request));

        assertTrue(exception.getMessage().contains("không thuộc bác sĩ"));
        verify(appointmentRepository, never()).existsActiveAppointmentBefore(
                any(), any(), any(), any());
    }

    @Test
    void shouldRejectPastScheduleBeforeChangingCapacity() {
        schedule.setWorkDate(LocalDate.now().minusDays(1));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> appointmentService.bookAppointment(patient.getId(), request));

        assertTrue(exception.getMessage().contains("đã qua"));
        assertEquals(0, slot.getBookedCapacity());
        verify(appointmentRepository, never()).existsActiveAppointmentBefore(
                any(), any(), any(), any());
    }

    @Test
    void shouldBookValidSlotAndUseSlotEndTimeForConflictCheck() {
        when(appointmentRepository.existsActiveAppointmentBefore(
                patient.getId(), schedule.getWorkDate(), slot.getEndTime(),
                List.of("CONFIRMED", "WAITING", "EXAMINING")))
                .thenReturn(false);
        when(appointmentRepository.existsBySlotIdAndPatientIdAndStatusNotIn(
                slot.getId(), patient.getId(), List.of("CANCELLED", "NO_SHOW")))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(70L);
            return appointment;
        });

        AppointmentResponse response = appointmentService.bookAppointment(patient.getId(), request);

        assertEquals(70L, response.getId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(1, slot.getBookedCapacity());
        verify(appointmentRepository).existsActiveAppointmentBefore(
                patient.getId(), schedule.getWorkDate(), slot.getEndTime(),
                List.of("CONFIRMED", "WAITING", "EXAMINING"));
        verify(timeSlotRepository).save(slot);
    }
}
