package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportResult;

public interface ScheduleImportService {

    /**
     * Chỉ đọc và validate, chưa lưu database.
     */
    ScheduleImportResult validate(MultipartFile file, Long weekScheduleId);

    ScheduleImportResult importSchedules(MultipartFile file, Long weekScheduleId, Long managerId);
}