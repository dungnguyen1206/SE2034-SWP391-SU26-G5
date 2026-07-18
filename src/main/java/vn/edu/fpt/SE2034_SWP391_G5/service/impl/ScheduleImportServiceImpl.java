package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.ScheduleImportRow;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportError;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportParseResult;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportResult;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.enums.WeekScheduleStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleImportException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoomRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.WeekScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleImportService;
import vn.edu.fpt.SE2034_SWP391_G5.util.excel.ScheduleExcelParser;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleImportServiceImpl implements ScheduleImportService {

    private final ScheduleExcelParser scheduleExcelParser;

    private final WeekScheduleRepository weekScheduleRepository;
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    private final RoomRepository roomRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    @Override
    @Transactional
    public ScheduleImportResult validate(MultipartFile file, Long weekScheduleId) {
        WeekSchedule weekSchedule = findDraftWeekSchedule(weekScheduleId);

        ScheduleImportParseResult parseResult = scheduleExcelParser.parse(file);

        List<ScheduleImportError> errors = new ArrayList<>(parseResult.getErrors());

        for (ScheduleImportRow row : parseResult.getRows()) {
            validateRow(row, weekSchedule, errors);
        }
        validateInternalFileConflicts(parseResult.getRows(), errors);

        Set<Integer> invalidRowNumbers = new HashSet<>();

        for (ScheduleImportError error : errors) {
            if (error.getRowNumber() > 0) {
                invalidRowNumbers.add(error.getRowNumber());
            }
        }

        int validRows = parseResult.getTotalRows() - invalidRowNumbers.size();

        return ScheduleImportResult.builder().totalRows(parseResult.getTotalRows()).validRows(Math.max(validRows, 0)).importedRows(0).errors(errors).build();
    }

    @Override
    @Transactional
    public ScheduleImportResult importSchedules(MultipartFile file, Long weekScheduleId, Long managerId) {
        if (managerId == null) {
            throw new ScheduleImportException("Không xác định được Manager thực hiện import.");
        }

        // Bước 1: kiểm tra toàn bộ file.
        ScheduleImportResult validationResult = validate(file, weekScheduleId);

        // Có bất kỳ lỗi nào thì không lưu.
        if (!validationResult.isSuccessful()) {
            validationResult.setImportedRows(0);
            return validationResult;
        }

        /*
         * File nhỏ, tối đa 100 dòng nên việc parse lần hai
         * chấp nhận được và giúp code đơn giản hơn.
         */
        ScheduleImportParseResult parseResult = scheduleExcelParser.parse(file);

        int importedRows = 0;

        for (ScheduleImportRow row : parseResult.getRows()) {
            User doctor = userRepository.findDoctorStaffDetailById(row.getDoctorId()).orElseThrow(() -> new ScheduleImportException("Không tìm thấy bác sĩ tại dòng " + row.getRowNumber() + "."));

            if (doctor.getDepartment() == null) {
                throw new ScheduleImportException("Bác sĩ tại dòng " + row.getRowNumber() + " chưa được phân khoa.");
            }

            CreateDoctorScheduleRequest request = CreateDoctorScheduleRequest.builder()
                    .departmentId(doctor.getDepartment().getId())
                    .doctorId(row.getDoctorId())
                    .workDate(row.getWorkDate())
                    .scheduleShift(row.getScheduleShift())
                    .roomId(row.getRoomId())
                    .maxCapacity(row.getMaxCapacity())
                    .note(row.getNote()).build();
            /*
             * Tái sử dụng chức năng tạo lịch hiện tại.
             * Method này sẽ tự:
             * - kiểm tra conflict lần cuối;
             * - tạo doctor_schedules;
             * - sinh time_slots.
             */
            scheduleService.createDoctorSchedule(request, managerId, weekScheduleId);

            importedRows++;
        }

        return ScheduleImportResult.builder().totalRows(parseResult.getTotalRows()).validRows(parseResult.getRows().size()).importedRows(importedRows).errors(new ArrayList<>()).build();
    }

    private WeekSchedule findDraftWeekSchedule(Long weekScheduleId) {
        if (weekScheduleId == null) {
            throw new ScheduleImportException("Không xác định được tuần làm việc.");
        }

        WeekSchedule weekSchedule = weekScheduleRepository.findWeekScheduleById(weekScheduleId);

        if (weekSchedule == null) {
            throw new ScheduleImportException("Tuần làm việc không tồn tại.");
        }

        if (!WeekScheduleStatus.DRAFT.toString().equalsIgnoreCase(weekSchedule.getStatus())) {

            throw new ScheduleImportException("Chỉ được import Excel khi lịch tuần đang ở trạng thái DRAFT.");
        }

        return weekSchedule;
    }

    private void validateRow(ScheduleImportRow row, WeekSchedule weekSchedule, List<ScheduleImportError> errors) {
        validateWorkDate(row, weekSchedule, errors);
        validateCapacity(row, errors);

        Optional<User> doctorOptional = userRepository.findDoctorStaffDetailById(row.getDoctorId());

        Optional<Room> roomOptional = roomRepository.findById(row.getRoomId());
        User doctor = validateDoctor(row, doctorOptional, errors);

        Room room = validateRoom(row, roomOptional, errors);
        if (doctor != null) {
            validateDoctorDatabaseConflict(row, doctor, errors);
        }

        if (room != null) {
            validateRoomDatabaseConflict(row, room, errors);
        }
        if (doctor != null && room != null) {
            validateDepartment(row, doctor, room, errors);
        }
    }

    private void validateWorkDate(ScheduleImportRow row, WeekSchedule weekSchedule, List<ScheduleImportError> errors) {
        boolean beforeWeek = row.getWorkDate().isBefore(weekSchedule.getStartDate());

        boolean afterWeek = row.getWorkDate().isAfter(weekSchedule.getEndDate());

        if (beforeWeek || afterWeek) {
            addError(errors, row.getRowNumber(), "WORK_DATE", "Ngày làm việc phải nằm trong tuần từ " + weekSchedule.getStartDate() + " đến " + weekSchedule.getEndDate() + ".");
        }
    }

    private void validateCapacity(ScheduleImportRow row, List<ScheduleImportError> errors) {
        int maxCapacity = row.getMaxCapacity();

        if (maxCapacity != 8 && maxCapacity != 10) {
            addError(errors, row.getRowNumber(), "MAX_CAPACITY", "MAX_CAPACITY chỉ nhận giá trị 8 hoặc 10.");
        }
    }

    private User validateDoctor(ScheduleImportRow row, Optional<User> doctorOptional, List<ScheduleImportError> errors) {
        if (doctorOptional.isEmpty()) {
            addError(errors, row.getRowNumber(), "DOCTOR_ID", "Không tìm thấy bác sĩ có ID " + row.getDoctorId() + ".");
            return null;
        }

        User doctor = doctorOptional.get();

        if (!"ACTIVE".equalsIgnoreCase(doctor.getStatus())) {
            addError(errors, row.getRowNumber(), "DOCTOR_ID", "Tài khoản bác sĩ không hoạt động.");
        }

        if (!"ACTIVE".equalsIgnoreCase(doctor.getDoctorStatus())) {
            addError(errors, row.getRowNumber(), "DOCTOR_ID", "Bác sĩ hiện không ở trạng thái làm việc.");
        }

        if (doctor.getDepartment() == null) {
            addError(errors, row.getRowNumber(), "DOCTOR_ID", "Bác sĩ chưa được phân khoa.");
        }

        return doctor;
    }

    private Room validateRoom(ScheduleImportRow row, Optional<Room> roomOptional, List<ScheduleImportError> errors) {
        if (roomOptional.isEmpty()) {
            addError(errors, row.getRowNumber(), "ROOM_ID", "Không tìm thấy phòng có ID " + row.getRoomId() + ".");
            return null;
        }

        Room room = roomOptional.get();

        if (!"ACTIVE".equalsIgnoreCase(room.getStatus())) {
            addError(errors, row.getRowNumber(), "ROOM_ID", "Phòng khám hiện không hoạt động.");
        }

        if (room.getDepartment() == null) {
            addError(errors, row.getRowNumber(), "ROOM_ID", "Phòng khám chưa được phân khoa.");
        }

        return room;
    }

    private void validateDepartment(ScheduleImportRow row, User doctor, Room room, List<ScheduleImportError> errors) {
        if (doctor.getDepartment() == null || room.getDepartment() == null) {
            return;
        }

        Integer doctorDepartmentId = doctor.getDepartment().getId();

        Integer roomDepartmentId = room.getDepartment().getId();

        if (!doctorDepartmentId.equals(roomDepartmentId)) {
            addError(errors, row.getRowNumber(), "ROOM_ID", "Phòng khám không cùng khoa với bác sĩ.");
        }
    }

    private void addError(List<ScheduleImportError> errors, int rowNumber, String field, String message) {
        errors.add(ScheduleImportError.builder().rowNumber(rowNumber).field(field).errorMessage(message).build());
    }

    private void validateDoctorDatabaseConflict(ScheduleImportRow row, User doctor, List<ScheduleImportError> errors) {
        boolean hasConflict = doctorScheduleRepository.existsByDoctorAndWorkDate(doctor, row.getWorkDate());

        if (hasConflict) {
            addError(errors, row.getRowNumber(), "DOCTOR_ID", "Bác sĩ đã có lịch làm việc vào ngày " + row.getWorkDate() + ".");
        }
    }

    private void validateRoomDatabaseConflict(ScheduleImportRow row, Room room, List<ScheduleImportError> errors) {
        List<String> conflictShifts = getConflictShifts(row.getScheduleShift());

        boolean hasConflict = doctorScheduleRepository.existsByRoomAndWorkDateAndShift(room, row.getWorkDate(), conflictShifts);

        if (hasConflict) {
            addError(errors, row.getRowNumber(), "ROOM_ID", "Phòng đã có bác sĩ trực trong ca này vào ngày " + row.getWorkDate() + ".");
        }
    }

    private List<String> getConflictShifts(ScheduleShift shift) {
        return switch (shift) {
            case MORNING -> List.of(ScheduleShift.MORNING.toString(), ScheduleShift.FULL_DAY.toString());

            case AFTERNOON -> List.of(ScheduleShift.AFTERNOON.toString(), ScheduleShift.FULL_DAY.toString());

            case FULL_DAY ->
                    List.of(ScheduleShift.MORNING.toString(), ScheduleShift.AFTERNOON.toString(), ScheduleShift.FULL_DAY.toString());
        };
    }

    private void validateInternalFileConflicts(List<ScheduleImportRow> rows, List<ScheduleImportError> errors) {
        for (int currentIndex = 0; currentIndex < rows.size(); currentIndex++) {

            ScheduleImportRow currentRow = rows.get(currentIndex);

            for (int previousIndex = 0; previousIndex < currentIndex; previousIndex++) {

                ScheduleImportRow previousRow = rows.get(previousIndex);


                validateInternalDoctorConflict(previousRow, currentRow, errors);

                validateInternalRoomConflict(previousRow, currentRow, errors);
            }
        }
    }

    private void validateInternalDoctorConflict(ScheduleImportRow previousRow, ScheduleImportRow currentRow, List<ScheduleImportError> errors) {
        boolean sameDoctor = previousRow.getDoctorId().equals(currentRow.getDoctorId());

        boolean sameDate = previousRow.getWorkDate().equals(currentRow.getWorkDate());

        if (sameDoctor && sameDate) {
            addError(errors, currentRow.getRowNumber(), "DOCTOR_ID", "Bác sĩ đã được xếp lịch ở dòng " + previousRow.getRowNumber() + " trong cùng ngày.");
        }
    }

    private void validateInternalRoomConflict(ScheduleImportRow previousRow, ScheduleImportRow currentRow, List<ScheduleImportError> errors) {
        boolean sameRoom = previousRow.getRoomId().equals(currentRow.getRoomId());

        boolean sameDate = previousRow.getWorkDate().equals(currentRow.getWorkDate());

        boolean overlappingShift = shiftsOverlap(previousRow.getScheduleShift(), currentRow.getScheduleShift());

        if (sameRoom && sameDate && overlappingShift) {
            addError(errors, currentRow.getRowNumber(), "ROOM_ID", "Phòng bị trùng ca với dòng " + previousRow.getRowNumber() + " trong file Excel.");
        }
    }

    private boolean shiftsOverlap(ScheduleShift firstShift, ScheduleShift secondShift) {
        if (firstShift == ScheduleShift.FULL_DAY || secondShift == ScheduleShift.FULL_DAY) {
            return true;
        }

        return firstShift == secondShift;
    }
}