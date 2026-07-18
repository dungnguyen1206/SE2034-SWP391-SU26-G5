package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.ScheduleImportRow;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportParseResult;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportResult;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleImportException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoomRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.WeekScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.util.excel.ScheduleExcelParser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleImportServiceImplTest {

    @Mock
    private ScheduleExcelParser scheduleExcelParser;

    @Mock
    private WeekScheduleRepository weekScheduleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ScheduleImportServiceImpl scheduleImportService;

    @Test
    void shouldRejectImportWhenWeekIsNotDraft() {
        WeekSchedule weekSchedule = createWeek("FINALIZED");

        when(weekScheduleRepository.findWeekScheduleById(1L)).thenReturn(weekSchedule);

        ScheduleImportException exception = assertThrows(ScheduleImportException.class, () -> scheduleImportService.validate(file, 1L));

        assertTrue(exception.getMessage().contains("DRAFT"));

        verifyNoInteractions(scheduleExcelParser);
    }

    @Test
    void shouldAcceptValidRow() {
        LocalDate workDate = LocalDate.of(2026, 7, 20);

        WeekSchedule weekSchedule = createWeek("DRAFT");
        Department department = createDepartment(1);
        User doctor = createDoctor(15L, department);
        Room room = createRoom(3, department);

        ScheduleImportRow importRow = createImportRow(2, 15L, workDate, ScheduleShift.MORNING, 3L, 8);

        mockParsedRows(List.of(importRow));

        when(weekScheduleRepository.findWeekScheduleById(1L)).thenReturn(weekSchedule);

        when(userRepository.findDoctorStaffDetailById(15L)).thenReturn(Optional.of(doctor));

        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));

        when(doctorScheduleRepository.existsByDoctorAndWorkDate(doctor, workDate)).thenReturn(false);

        when(doctorScheduleRepository.existsByRoomAndWorkDateAndShift(eq(room), eq(workDate), anyList())).thenReturn(false);

        ScheduleImportResult result = scheduleImportService.validate(file, 1L);

        assertTrue(result.isSuccessful());
        assertEquals(1, result.getTotalRows());
        assertEquals(1, result.getValidRows());
        assertEquals(0, result.getImportedRows());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void shouldCollectBasicBusinessErrors() {
        Department doctorDepartment = createDepartment(1);
        Department roomDepartment = createDepartment(2);

        User doctor = createDoctor(15L, doctorDepartment);
        Room room = createRoom(3, roomDepartment);

        ScheduleImportRow importRow = createImportRow(2, 15L, LocalDate.of(2026, 7, 30), ScheduleShift.MORNING, 3L, 6);

        mockParsedRows(List.of(importRow));

        when(weekScheduleRepository.findWeekScheduleById(1L)).thenReturn(createWeek("DRAFT"));

        when(userRepository.findDoctorStaffDetailById(15L)).thenReturn(Optional.of(doctor));

        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));

        ScheduleImportResult result = scheduleImportService.validate(file, 1L);

        assertFalse(result.isSuccessful());
        assertEquals(0, result.getValidRows());
        assertEquals(0, result.getImportedRows());

        Set<String> errorFields = result.getErrors().stream().map(error -> error.getField()).collect(Collectors.toSet());

        assertTrue(errorFields.contains("WORK_DATE"));
        assertTrue(errorFields.contains("MAX_CAPACITY"));
        assertTrue(errorFields.contains("ROOM_ID"));
    }

    @Test
    void shouldDetectInternalFileConflicts() {
        LocalDate workDate = LocalDate.of(2026, 7, 20);

        WeekSchedule weekSchedule = createWeek("DRAFT");
        Department department = createDepartment(1);
        User doctor = createDoctor(15L, department);
        Room room = createRoom(3, department);

        ScheduleImportRow firstRow = createImportRow(2, 15L, workDate, ScheduleShift.MORNING, 3L, 8);

        ScheduleImportRow secondRow = createImportRow(3, 15L, workDate, ScheduleShift.FULL_DAY, 3L, 8);

        mockParsedRows(List.of(firstRow, secondRow));

        when(weekScheduleRepository.findWeekScheduleById(1L)).thenReturn(weekSchedule);

        when(userRepository.findDoctorStaffDetailById(15L)).thenReturn(Optional.of(doctor));

        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));

        ScheduleImportResult result = scheduleImportService.validate(file, 1L);

        assertFalse(result.isSuccessful());
        assertEquals(2, result.getTotalRows());
        assertEquals(1, result.getValidRows());
        assertEquals(0, result.getImportedRows());

        List<String> rowThreeFields = result.getErrors().stream().filter(error -> error.getRowNumber() == 3).map(error -> error.getField()).toList();

        assertTrue(rowThreeFields.contains("DOCTOR_ID"));
        assertTrue(rowThreeFields.contains("ROOM_ID"));
    }

    @Test
    void shouldDetectExistingDatabaseConflicts() {
        LocalDate workDate = LocalDate.of(2026, 7, 20);

        WeekSchedule weekSchedule = createWeek("DRAFT");
        Department department = createDepartment(1);
        User doctor = createDoctor(15L, department);
        Room room = createRoom(3, department);

        ScheduleImportRow importRow = createImportRow(2, 15L, workDate, ScheduleShift.MORNING, 3L, 8);

        mockParsedRows(List.of(importRow));

        when(weekScheduleRepository.findWeekScheduleById(1L)).thenReturn(weekSchedule);

        when(userRepository.findDoctorStaffDetailById(15L)).thenReturn(Optional.of(doctor));

        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));

        when(doctorScheduleRepository.existsByDoctorAndWorkDate(doctor, workDate)).thenReturn(true);

        when(doctorScheduleRepository.existsByRoomAndWorkDateAndShift(eq(room), eq(workDate), anyList())).thenReturn(true);

        ScheduleImportResult result = scheduleImportService.validate(file, 1L);

        assertFalse(result.isSuccessful());
        assertEquals(0, result.getValidRows());

        Set<String> errorFields = result.getErrors().stream().map(error -> error.getField()).collect(Collectors.toSet());

        assertTrue(errorFields.contains("DOCTOR_ID"));
        assertTrue(errorFields.contains("ROOM_ID"));
    }

    private void mockParsedRows(List<ScheduleImportRow> rows) {
        ScheduleImportParseResult parseResult = ScheduleImportParseResult.builder().totalRows(rows.size()).rows(rows).build();

        when(scheduleExcelParser.parse(file)).thenReturn(parseResult);
    }

    private WeekSchedule createWeek(String status) {
        WeekSchedule weekSchedule = new WeekSchedule();
        weekSchedule.setId(1L);
        weekSchedule.setStartDate(LocalDate.of(2026, 7, 20));
        weekSchedule.setEndDate(LocalDate.of(2026, 7, 26));
        weekSchedule.setStatus(status);
        return weekSchedule;
    }

    private Department createDepartment(Integer id) {
        Department department = new Department();
        department.setId(id);
        department.setName("Khoa " + id);
        department.setStatus("ACTIVE");
        return department;
    }

    private User createDoctor(Long id, Department department) {
        User doctor = new User();
        doctor.setId(id);
        doctor.setStatus("ACTIVE");
        doctor.setDoctorStatus("ACTIVE");
        doctor.setDepartment(department);
        return doctor;
    }

    private Room createRoom(Integer id, Department department) {
        Room room = new Room();
        room.setId(id);
        room.setStatus("ACTIVE");
        room.setDepartment(department);
        return room;
    }

    private ScheduleImportRow createImportRow(int rowNumber, Long doctorId, LocalDate workDate, ScheduleShift shift, Long roomId, Integer maxCapacity) {
        return ScheduleImportRow.builder().rowNumber(rowNumber).doctorId(doctorId).workDate(workDate).scheduleShift(shift).roomId(roomId).maxCapacity(maxCapacity).note(null).build();
    }
}