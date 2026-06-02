package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Invoice;

import java.math.BigDecimal;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("Select coalesce(sum(i.totalAmount),0) From Invoice i where i.paymentStatus =:paymentStatus AND month(i.paidAt)=:month AND year(i.paidAt)= :year")
    BigDecimal getTotalAmount(@Param("paymentStatus") String paymentStatus, @Param("month") int month, @Param("year") int year);
}
