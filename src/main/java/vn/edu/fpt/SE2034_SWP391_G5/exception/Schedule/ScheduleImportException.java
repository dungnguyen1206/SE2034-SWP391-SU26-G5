package vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule;

public class ScheduleImportException extends RuntimeException {
    public ScheduleImportException(String message) {
        super(message);
    }
    public ScheduleImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
