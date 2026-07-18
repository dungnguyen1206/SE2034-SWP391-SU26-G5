package vn.edu.fpt.SE2034_SWP391_G5.util.excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportParseResult;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleImportException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.ScheduleImportRow;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportError;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ScheduleExcelParser {

    public static final String SCHEDULE_SHEET_NAME = "Schedules";

    private static final long MAX_FILE_SIZE = 2L * 1024 * 1024;

    private static final int MAX_DATA_ROWS = 100;

    private static final List<String> EXPECTED_HEADERS = List.of("DOCTOR_ID", "WORK_DATE", "SHIFT", "ROOM_ID", "MAX_CAPACITY", "NOTE");
    private static final DateTimeFormatter VIETNAM_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final DataFormatter dataFormatter = new DataFormatter();

    public ScheduleImportParseResult parse(MultipartFile file) {
        validateFile(file);

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet(SCHEDULE_SHEET_NAME);

            if (sheet == null) {
                throw new ScheduleImportException("Không tìm thấy sheet '" + SCHEDULE_SHEET_NAME + "'.");
            }

            validateHeader(sheet);

            int totalRows = countDataRows(sheet);

            if (totalRows == 0) {
                throw new ScheduleImportException("File Excel không có dòng dữ liệu nào.");
            }

            if (totalRows > MAX_DATA_ROWS) {
                throw new ScheduleImportException("File Excel chỉ được chứa tối đa " + MAX_DATA_ROWS + " dòng dữ liệu.");
            }

            /*
             *  đọc từng dòng và thêm ScheduleImportRow
             * vào danh sách rows.
             */
            List<ScheduleImportRow> parsedRows = new ArrayList<>();
            List<ScheduleImportError> errors = new ArrayList<>();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);

                if (isRowEmpty(row)) {
                    continue;
                }

                // Excel hiển thị số dòng bắt đầu từ 1.
                int excelRowNumber = rowIndex + 1;

                ScheduleImportRow parsedRow = parseRow(row, excelRowNumber, errors);

                if (parsedRow != null) {
                    parsedRows.add(parsedRow);
                }
            }

            return ScheduleImportParseResult.builder().totalRows(totalRows).rows(parsedRows).errors(errors).build();

        } catch (ScheduleImportException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw new ScheduleImportException("Không thể đọc file Excel. File có thể bị hỏng hoặc không đúng định dạng .xlsx.", exception);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ScheduleImportException("Vui lòng chọn file Excel để import.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ScheduleImportException("Dung lượng file Excel không được vượt quá 2 MB.");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new ScheduleImportException("Chỉ hỗ trợ file Excel có định dạng .xlsx.");
        }
    }

    private void validateHeader(Sheet sheet) {
        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
            throw new ScheduleImportException("File Excel không có dòng tiêu đề.");
        }

        for (int columnIndex = 0; columnIndex < EXPECTED_HEADERS.size(); columnIndex++) {

            String actualHeader = dataFormatter.formatCellValue(headerRow.getCell(columnIndex)).trim().toUpperCase(Locale.ROOT);

            String expectedHeader = EXPECTED_HEADERS.get(columnIndex);

            if (!expectedHeader.equals(actualHeader)) {
                throw new ScheduleImportException("Cột " + (columnIndex + 1) + " phải có tiêu đề '" + expectedHeader + "', nhưng nhận được '" + actualHeader + "'.");
            }
        }
    }

    private int countDataRows(Sheet sheet) {
        int totalRows = 0;

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

            Row row = sheet.getRow(rowIndex);

            if (!isRowEmpty(row)) {
                totalRows++;
            }
        }

        return totalRows;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int columnIndex = 0; columnIndex < EXPECTED_HEADERS.size(); columnIndex++) {

            String value = dataFormatter.formatCellValue(row.getCell(columnIndex)).trim();

            if (!value.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private ScheduleImportRow parseRow(Row row, int rowNumber, List<ScheduleImportError> errors) {
        int errorCountBeforeParsing = errors.size();

        Long doctorId = readRequiredLong(row.getCell(0), rowNumber, "DOCTOR_ID", errors);

        LocalDate workDate = readRequiredDate(row.getCell(1), rowNumber, "WORK_DATE", errors);

        ScheduleShift scheduleShift = readRequiredShift(row.getCell(2), rowNumber, "SHIFT", errors);

        Long roomId = readRequiredLong(row.getCell(3), rowNumber, "ROOM_ID", errors);

        Integer maxCapacity = readRequiredInteger(row.getCell(4), rowNumber, "MAX_CAPACITY", errors);

        String note = readOptionalText(row.getCell(5), rowNumber, "NOTE", errors);

        if (note != null && note.length() > 256) {
            addError(errors, rowNumber, "NOTE", "Ghi chú không được vượt quá 256 ký tự.");
        }

        // Dòng có lỗi thì không đưa vào danh sách hợp lệ.
        if (errors.size() > errorCountBeforeParsing) {
            return null;
        }

        return ScheduleImportRow.builder().rowNumber(rowNumber).doctorId(doctorId).workDate(workDate).scheduleShift(scheduleShift).roomId(roomId).maxCapacity(maxCapacity).note(note).build();
    }

    private Integer readRequiredInteger(Cell cell, int rowNumber, String field, List<ScheduleImportError> errors) {
        Long parsedValue = readRequiredLong(cell, rowNumber, field, errors);

        if (parsedValue == null) {
            return null;
        }

        if (parsedValue > Integer.MAX_VALUE) {
            addError(errors, rowNumber, field, field + " vượt quá giới hạn cho phép.");
            return null;
        }

        return parsedValue.intValue();
    }

    private LocalDate readRequiredDate(Cell cell, int rowNumber, String field, List<ScheduleImportError> errors) {
        if (isBlankCell(cell)) {
            addError(errors, rowNumber, field, "Ngày làm việc không được để trống.");
            return null;
        }

        if (cell.getCellType() == CellType.FORMULA) {
            addError(errors, rowNumber, field, "Ngày làm việc không được chứa công thức Excel.");
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            if (!DateUtil.isCellDateFormatted(cell)) {
                addError(errors, rowNumber, field, "WORK_DATE phải là kiểu ngày trong Excel.");
                return null;
            }

            try {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } catch (RuntimeException exception) {
                addError(errors, rowNumber, field, "Ngày làm việc không hợp lệ.");
                return null;
            }
        }

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();

            try {
                return LocalDate.parse(value, VIETNAM_DATE_FORMAT);
            } catch (DateTimeParseException ignored) {
                // Thử định dạng ISO: 2026-07-20
            }

            try {
                return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ignored) {
                addError(errors, rowNumber, field, "Ngày phải có định dạng dd/MM/yyyy hoặc yyyy-MM-dd.");
                return null;
            }
        }

        addError(errors, rowNumber, field, "Ngày làm việc không hợp lệ.");
        return null;
    }

    private ScheduleShift readRequiredShift(Cell cell, int rowNumber, String field, List<ScheduleImportError> errors) {
        if (isBlankCell(cell)) {
            addError(errors, rowNumber, field, "Ca làm việc không được để trống.");
            return null;
        }

        if (cell.getCellType() == CellType.FORMULA) {
            addError(errors, rowNumber, field, "Ca làm việc không được chứa công thức Excel.");
            return null;
        }

        String value = dataFormatter.formatCellValue(cell).trim().toUpperCase(Locale.ROOT);

        try {
            return ScheduleShift.valueOf(value);
        } catch (IllegalArgumentException exception) {
            addError(errors, rowNumber, field, "SHIFT chỉ nhận MORNING, AFTERNOON hoặc FULL_DAY.");
            return null;
        }
    }

    private String readOptionalText(Cell cell, int rowNumber, String field, List<ScheduleImportError> errors) {
        if (isBlankCell(cell)) {
            return null;
        }

        if (cell.getCellType() == CellType.FORMULA) {
            addError(errors, rowNumber, field, field + " không được chứa công thức Excel.");
            return null;
        }

        String value = dataFormatter.formatCellValue(cell).trim();

        return value.isEmpty() ? null : value;
    }

    private boolean isBlankCell(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return true;
        }

        return dataFormatter.formatCellValue(cell).trim().isEmpty();
    }

    private void addError(List<ScheduleImportError> errors, int rowNumber, String field, String message) {
        errors.add(ScheduleImportError.builder().rowNumber(rowNumber).field(field).errorMessage(message).build());
    }

    private Long readRequiredLong(Cell cell, int rowNumber, String field, List<ScheduleImportError> errors) {
        if (isBlankCell(cell)) {
            addError(errors, rowNumber, field, field + " không được để trống.");
            return null;
        }

        if (cell.getCellType() == CellType.FORMULA) {
            addError(errors, rowNumber, field, field + " không được chứa công thức Excel.");
            return null;
        }

        try {
            long parsedValue;

            if (cell.getCellType() == CellType.NUMERIC) {
                double numericValue = cell.getNumericCellValue();

                if (!Double.isFinite(numericValue) || numericValue != Math.rint(numericValue) || numericValue > Long.MAX_VALUE) {

                    addError(errors, rowNumber, field, field + " phải là số nguyên.");
                    return null;
                }

                parsedValue = (long) numericValue;

            } else if (cell.getCellType() == CellType.STRING) {
                parsedValue = Long.parseLong(cell.getStringCellValue().trim());

            } else {
                addError(errors, rowNumber, field, field + " phải là số nguyên.");
                return null;
            }

            if (parsedValue <= 0) {
                addError(errors, rowNumber, field, field + " phải lớn hơn 0.");
                return null;
            }

            return parsedValue;

        } catch (NumberFormatException exception) {
            addError(errors, rowNumber, field, field + " phải là số nguyên.");
            return null;
        }
    }


}