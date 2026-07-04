package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceRowResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Invoice;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("Select coalesce(sum(i.totalAmount),0) From Invoice i where i.paymentStatus =:paymentStatus AND month(i.paidAt)=:month AND year(i.paidAt)= :year")
    BigDecimal getTotalAmount(@Param("paymentStatus") String paymentStatus, @Param("month") int month, @Param("year") int year);

    // Đếm hóa đơn đã thanh toán trong ngày hiện tại.
// Dựa theo ngày khám của appointment, không dựa theo ngày tạo hóa đơn.
    @Query("SELECT COUNT(i) FROM Invoice i " +
            "JOIN i.medicalRecord mr " +
            "JOIN mr.appointment a " +
            "WHERE a.bookingDate = :today " +
            "AND i.paymentStatus = 'PAID'")
    long countTodayPaidInvoices(@Param("today") LocalDate today);

    // Đếm hóa đơn chưa thanh toán trong ngày hiện tại.
// Dựa theo ngày khám của appointment, không lấy toàn bộ hóa đơn rồi đếm bằng Java.
    @Query("SELECT COUNT(i) FROM Invoice i " +
            "JOIN i.medicalRecord mr " +
            "JOIN mr.appointment a " +
            "WHERE a.bookingDate = :today " +
            "AND i.paymentStatus = 'UNPAID'")
    long countTodayUnpaidInvoices(@Param("today") LocalDate today);


    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse(i.paymentStatus,count(i),sum(i.totalAmount)) " +
            " FROM Invoice i" +
            " WHERE ((:startDate is null and :endDate is null) or i.createdAt between :startDate and :endDate  )" +
            " AND (:month is null or month(i.createdAt)=:month)" +
            " AND (:year is null or year(i.createdAt)=:year )" +
            " and i.paymentStatus=:paymentStatus" +
            " group by i.paymentStatus")
    Optional<InvoiceSummaryResponse> getTotalAmountByPaymentStatus(@Param("paymentStatus") String paymentStatus,
                                                                   @Param("startDate")LocalDateTime startDate,
                                                                   @Param("endDate") LocalDateTime endDate, @Param("month") Integer month, @Param("year") Integer year);


    @Query("SELECT i FROM Invoice i " +
           "JOIN i.medicalRecord mr " +
           "JOIN mr.appointment a " +
           "JOIN a.patient p " +
           "WHERE (:keyword IS NULL OR :keyword = '' " +
           "  OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:paymentStatus IS NULL OR :paymentStatus = '' OR i.paymentStatus = :paymentStatus)")
    Page<Invoice> findInvoicesWithFilter(@Param("keyword") String keyword,
                                                                         @Param("paymentStatus") String paymentStatus,
                                                                         Pageable pageable);

    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN FETCH i.medicalRecord mr " +
           "LEFT JOIN FETCH mr.appointment a " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.slot s " +
           "LEFT JOIN FETCH s.schedule sc " +
           "LEFT JOIN FETCH sc.room r " +
           "LEFT JOIN FETCH d.department dep " +
           "LEFT JOIN FETCH i.invoiceItems items " +
           "WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);
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
            " left join  i.medicalRecord m " +
            " left join  m.doctor d " +
            " left join  m.patient p" +
            " left join  d.department dpt" +
            " left join  m.appointment a" +
            " Where (:month is null or month(i.createdAt)=:month)" +
            " and (:year is null or year (i.createdAt)=:year)" +
            " and ((:startDate is null and :endDate is null) or i.createdAt between :startDate and :endDate)")
    Page<InvoiceRowResponse> getInvoiceInforByFilter(@Param("startDate")LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     @Param("month") Integer month,
                                                     @Param("year") Integer year, Pageable pageable);



}
