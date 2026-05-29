package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a from Appointment a WHere a.status=:appointmentStatus")
    List<Appointment> findAppointmentsByStatus(@Param("appointmentStatus") String appointmentStatus);

    @Query("SELECT a from Appointment a " +
            "JOIN fetch a.patient p " +
            "JOIN fetch a.doctor d " +
            "JOIN fetch a.service s " +
            "join fetch a.slot sl " +
            "join fetch sl.schedule ds " +
            "join fetch ds.room r " +
            "WHERE day(a.bookingDate) =:today " +
            "order by sl.startTime asc")
    List<Appointment> findAppointmentsByBookingDate(@Param("today") int today);
}
