package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final DoctorScheduleRepository doctorScheduleRepository;
    public ScheduleServiceImpl(DoctorScheduleRepository doctorScheduleRepository) {
        this.doctorScheduleRepository = doctorScheduleRepository;

    }

    //Find all doctor work today
   public List<DoctorOnDutyResponse> findDoctorScheduleByDate(@Param("date") LocalDate date){
            List<DoctorSchedule> doctorSchedules = doctorScheduleRepository.findByDate(date);
            List<DoctorOnDutyResponse> doctorOnDutyResponses = new ArrayList<>();
            doctorSchedules.forEach(doctorSchedule -> {
                DoctorOnDutyResponse response = new DoctorOnDutyResponse();
                response.setDoctorName(doctorSchedule.getDoctor().getFirstName()+" "+ doctorSchedule.getDoctor().getMiddleName()+" "+doctorSchedule.getDoctor().getLastName());
                response.setDepartmentName(doctorSchedule.getDoctor().getDepartment().getName());
                response.setShift(String.valueOf(doctorSchedule.getShift()));
                response.setStatus(doctorSchedule.getDoctor().getStatus());
                response.setRoomNumber(doctorSchedule.getRoom().getRoomNumber());
                doctorOnDutyResponses.add(response);
            });
            return doctorOnDutyResponses;
    };

}
