package vn.edu.fpt.SE2034_SWP391_G5.util;

import java.util.UUID;

public class CodeGenerator {
    public static String generateAppointmentCode() {
        return "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
