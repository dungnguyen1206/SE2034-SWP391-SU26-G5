package vn.edu.fpt.SE2034_SWP391_G5.util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenerator {

    // Dùng timestamp (nanoseconds) để tránh trùng mã khi server restart
    // Trước đây: private static final AtomicInteger counter = new AtomicInteger(0);
    //            return String.format("APT-%s%04d", year, counter.incrementAndGet() % 10000);
    public static String generateAppointmentCode() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        // Lấy 6 chữ số cuối của epoch millis để đảm bảo unique
        long seq = System.currentTimeMillis() % 1_000_000L;
        return String.format("APT-%s%06d", year, seq);
    }
}
