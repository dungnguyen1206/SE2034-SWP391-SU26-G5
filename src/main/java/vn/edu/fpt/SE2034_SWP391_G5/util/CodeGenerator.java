package vn.edu.fpt.SE2034_SWP391_G5.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static String generateAppointmentCode() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int seq = counter.incrementAndGet() % 10000;
        return String.format("APT-%s%04d", year, seq);
    }
}
