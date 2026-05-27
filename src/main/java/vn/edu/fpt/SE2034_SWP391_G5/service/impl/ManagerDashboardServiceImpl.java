package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.repository.PatientRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ManagerDashboardService;

@Service
public class ManagerDashboardServiceImpl implements ManagerDashboardService {

    private final PatientRepository patientRepository;

    public ManagerDashboardServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    @Override
    public long getTotalPatients(){
        return patientRepository.count();
    };
}
