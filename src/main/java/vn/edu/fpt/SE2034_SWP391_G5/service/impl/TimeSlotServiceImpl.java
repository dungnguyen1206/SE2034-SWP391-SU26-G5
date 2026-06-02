package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;
import vn.edu.fpt.SE2034_SWP391_G5.repository.TimeSlotRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.TimeSlotService;

import java.time.LocalDate;
import java.util.List;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }


}
