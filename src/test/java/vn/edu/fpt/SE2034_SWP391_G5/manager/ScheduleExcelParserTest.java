package vn.edu.fpt.SE2034_SWP391_G5.manager;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.ScheduleImportRow;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportError;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportParseResult;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleImportException;
import vn.edu.fpt.SE2034_SWP391_G5.util.excel.ScheduleExcelParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleExcelParserTest {

    private final ScheduleExcelParser parser = new ScheduleExcelParser();

    @Test
    void shouldParseValidWorkbook() throws IOException {
        try (XSSFWorkbook workbook = createWorkbookWithHeader()) {
            Sheet sheet = workbook.getSheet("Schedules");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(15);
            dataRow.createCell(1).setCellValue("20/07/2026");
            dataRow.createCell(2).setCellValue("MORNING");
            dataRow.createCell(3).setCellValue(3);
            dataRow.createCell(4).setCellValue(8);
            dataRow.createCell(5).setCellValue("Khám tổng quát");

            MockMultipartFile file = toMultipartFile(workbook);

            ScheduleImportParseResult result = parser.parse(file);

            assertEquals(1, result.getTotalRows());
            assertEquals(1, result.getRows().size());
            assertTrue(result.getErrors().isEmpty());

            ScheduleImportRow parsedRow = result.getRows().getFirst();

            assertEquals(2, parsedRow.getRowNumber());
            assertEquals(15L, parsedRow.getDoctorId());
            assertEquals(LocalDate.of(2026, 7, 20), parsedRow.getWorkDate());
            assertEquals(ScheduleShift.MORNING, parsedRow.getScheduleShift());
            assertEquals(3L, parsedRow.getRoomId());
            assertEquals(8, parsedRow.getMaxCapacity());
            assertEquals("Khám tổng quát", parsedRow.getNote());
        }
    }

    @Test
    void shouldRejectInvalidHeader() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Schedules");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("WRONG_DOCTOR");
            header.createCell(1).setCellValue("WORK_DATE");
            header.createCell(2).setCellValue("SHIFT");
            header.createCell(3).setCellValue("ROOM_ID");
            header.createCell(4).setCellValue("MAX_CAPACITY");
            header.createCell(5).setCellValue("NOTE");

            MockMultipartFile file = toMultipartFile(workbook);

            ScheduleImportException exception = assertThrows(
                    ScheduleImportException.class,
                    () -> parser.parse(file)
            );

            assertTrue(exception.getMessage().contains("DOCTOR_ID"));
        }
    }

    @Test
    void shouldCollectErrorsFromInvalidRow() throws IOException {
        try (XSSFWorkbook workbook = createWorkbookWithHeader()) {
            Sheet sheet = workbook.getSheet("Schedules");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("ABC");
            dataRow.createCell(1).setCellValue("31/02/2026");
            dataRow.createCell(2).setCellValue("NIGHT");
            dataRow.createCell(3).setCellValue(0);
            dataRow.createCell(4).setCellValue(8);

            MockMultipartFile file = toMultipartFile(workbook);

            ScheduleImportParseResult result = parser.parse(file);

            assertEquals(1, result.getTotalRows());
            assertTrue(result.getRows().isEmpty());
            assertFalse(result.getErrors().isEmpty());

            Set<String> errorFields = result.getErrors()
                    .stream()
                    .map(ScheduleImportError::getField)
                    .collect(Collectors.toSet());

            assertTrue(errorFields.contains("DOCTOR_ID"));
            assertTrue(errorFields.contains("WORK_DATE"));
            assertTrue(errorFields.contains("SHIFT"));
            assertTrue(errorFields.contains("ROOM_ID"));
        }
    }

    @Test
    void shouldRejectFormulaCell() throws IOException {
        try (XSSFWorkbook workbook = createWorkbookWithHeader()) {
            Sheet sheet = workbook.getSheet("Schedules");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellFormula("10+5");
            dataRow.createCell(1).setCellValue("20/07/2026");
            dataRow.createCell(2).setCellValue("MORNING");
            dataRow.createCell(3).setCellValue(3);
            dataRow.createCell(4).setCellValue(8);

            MockMultipartFile file = toMultipartFile(workbook);

            ScheduleImportParseResult result = parser.parse(file);

            assertTrue(result.getRows().isEmpty());
            assertEquals(1, result.getErrors().size());
            assertEquals(
                    "DOCTOR_ID",
                    result.getErrors().getFirst().getField()
            );
            assertTrue(
                    result.getErrors().getFirst()
                            .getErrorMessage()
                            .contains("công thức")
            );
        }
    }

    @Test
    void shouldIgnoreEmptyRows() throws IOException {
        try (XSSFWorkbook workbook = createWorkbookWithHeader()) {
            Sheet sheet = workbook.getSheet("Schedules");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(15);
            dataRow.createCell(1).setCellValue("2026-07-20");
            dataRow.createCell(2).setCellValue("AFTERNOON");
            dataRow.createCell(3).setCellValue(3);
            dataRow.createCell(4).setCellValue(10);

            // Dòng 3 tồn tại nhưng không có dữ liệu.
            sheet.createRow(2);

            MockMultipartFile file = toMultipartFile(workbook);

            ScheduleImportParseResult result = parser.parse(file);

            assertEquals(1, result.getTotalRows());
            assertEquals(1, result.getRows().size());
            assertTrue(result.getErrors().isEmpty());
        }
    }

    private XSSFWorkbook createWorkbookWithHeader() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Schedules");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("DOCTOR_ID");
        header.createCell(1).setCellValue("WORK_DATE");
        header.createCell(2).setCellValue("SHIFT");
        header.createCell(3).setCellValue("ROOM_ID");
        header.createCell(4).setCellValue("MAX_CAPACITY");
        header.createCell(5).setCellValue("NOTE");

        return workbook;
    }

    private MockMultipartFile toMultipartFile(
            XSSFWorkbook workbook
    ) throws IOException {
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        workbook.write(outputStream);

        return new MockMultipartFile(
                "file",
                "schedule-import.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
        );
    }
}