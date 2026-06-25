package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

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
}
