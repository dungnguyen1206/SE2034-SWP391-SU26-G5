package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceRowResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // ======================== DASHBOARD RECEPTIONIST ========================
    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistDashboardResponse(" +
            "0L, 0L, 0L, 0L, " +
            "COALESCE(SUM(CASE WHEN i.paymentStatus = 'PAID' THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN i.paymentStatus = 'UNPAID' THEN 1L ELSE 0L END), 0L)) " +
            "FROM Invoice i JOIN i.appointment a WHERE a.bookingDate = :today")
    vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistDashboardResponse getTodayInvoiceDashboardCounts(@Param("today") LocalDate today);
    // ======================== END DASHBOARD RECEPTIONIST ========================

    @Query("Select coalesce(sum(i.totalAmount),0) From Invoice i where i.paymentStatus =:paymentStatus AND month(i.paidAt)=:month AND year(i.paidAt)= :year")
    BigDecimal getTotalAmount(@Param("paymentStatus") String paymentStatus, @Param("month") int month, @Param("year") int year);


    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse(i.paymentStatus,count(i),sum(i.totalAmount)) " +
            " FROM Invoice i" +
            " WHERE ((:startDate is null and :endDate is null) or i.createdAt between :startDate and :endDate  )" +
            " AND (:month is null or month(i.createdAt)=:month)" +
            " AND (:year is null or year(i.createdAt)=:year )" +
            " and i.paymentStatus=:paymentStatus" +
            " group by i.paymentStatus")
    Optional<InvoiceSummaryResponse> getTotalAmountByPaymentStatus(@Param("paymentStatus") String paymentStatus,
                                                                   @Param("startDate") LocalDateTime startDate,
                                                                   @Param("endDate") LocalDateTime endDate, @Param("month") Integer month, @Param("year") Integer year);

    @Query("select new vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceRowResponse(" +
            " i.id," +
            " i.invoiceCode," +
            " a.appointmentCode," +
            " concat(p.firstName,' ',coalesce(p.middleName,''),' ',p.lastName)," +
            " concat(d.firstName,' ',coalesce(d.middleName,''),' ',d.lastName)," +
            " dpt.name," +
            " i.totalAmount," +
            " i.paymentStatus," +
            " i.createdAt) " +
            " From Invoice i " +
            " left join  i.appointment a " +
            " left join  a.doctor d " +
            " left join  a.patient p" +
            " left join  d.department dpt" +
            " Where (:month is null or month(i.createdAt)=:month)" +
            " and (:year is null or year (i.createdAt)=:year)" +
            " and ((:startDate is null and :endDate is null) or i.createdAt between :startDate and :endDate)")
    Page<InvoiceRowResponse> getInvoiceInforByFilter(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     @Param("month") Integer month,
                                                     @Param("year") Integer year, Pageable pageable);
}
