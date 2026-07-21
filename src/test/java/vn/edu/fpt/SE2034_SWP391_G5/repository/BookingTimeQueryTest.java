package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class BookingTimeQueryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Test
    void shouldCompareSqlServerTimeWithBookingParameters() {
        boolean hasConflict = appointmentRepository.existsActiveAppointmentBefore(
                Long.MAX_VALUE,
                LocalDate.now().plusYears(10),
                LocalTime.of(9, 30),
                List.of("CONFIRMED", "WAITING", "EXAMINING"));

        assertFalse(hasConflict);

        assertTrue(timeSlotRepository.findAvailableSlotsByDepartmentAndDate(
                Integer.MAX_VALUE,
                LocalDate.now().plusYears(10),
                LocalTime.of(8, 0)).isEmpty());
    }
}
