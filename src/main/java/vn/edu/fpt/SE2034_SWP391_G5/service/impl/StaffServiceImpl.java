package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.StaffService;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    public StaffServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //find all active staff
    public  List<StaffResponse> findStaff(String roleName, String filterKey) {
       return userRepository.findActiveStaffList(roleName,filterKey);
    }

   public  StaffResponse findStaffById(Long id){
        return userRepository.selectStaffById(id);
    }

    @Override
    public User findDoctorById(Long id) {
       User doctor = userRepository.findDoctorStaffDetailById(id).orElseThrow(() -> new ResourceNotFoundException("doctor not found"));
       return doctor;
    }

    @Override
    public User findReceptionistById(Long id) {
          User receptionist = userRepository.findReceptionistStaffDetailById(id).orElseThrow(() -> new ResourceNotFoundException("receptionist not found"));
          return receptionist;
        }

    public DoctorStaffDetailResponse findDoctorStaffDetailById(Long id){
        return toDoctorStaffDetailResponse(findDoctorById(id));
    }

    private DoctorStaffDetailResponse toDoctorStaffDetailResponse(User doctor){
        DoctorStaffDetailResponse doctorStaffDetailResponse = new DoctorStaffDetailResponse();
        doctorStaffDetailResponse.setId(doctor.getId());
        doctorStaffDetailResponse.setStaffCode("STF-"+doctor.getId());
        doctorStaffDetailResponse.setBio(doctor.getBio());
        doctorStaffDetailResponse.setDegree(doctor.getDegree());
        doctorStaffDetailResponse.setEmail(doctor.getEmail());
        doctorStaffDetailResponse.setCreatedAt(doctor.getCreatedAt());
        doctorStaffDetailResponse.setAccountStatus(doctor.getStatus());
        doctorStaffDetailResponse.setCreatedBy(doctor.getCreatedBy().getFirstName() + " " +doctor.getCreatedBy().getMiddleName()+" " + doctor.getCreatedBy().getLastName());
        doctorStaffDetailResponse.setPhone(doctor.getPhone());
        doctorStaffDetailResponse.setDepartmentName(doctor.getDepartment().getName());
        doctorStaffDetailResponse.setFullName(doctor.getFirstName()+" "+doctor.getMiddleName()+" "+doctor.getLastName());
        doctorStaffDetailResponse.setExperienceYears(doctor.getExperienceYears());
        doctorStaffDetailResponse.setLicenseNumber(String.valueOf(doctor.getLicenseNumber()));
        doctorStaffDetailResponse.setRoleName("DOCTOR");
        doctorStaffDetailResponse.setRoleLabel("Bác sĩ");
        doctorStaffDetailResponse.setWorkingStatus(doctor.getStatus());
        doctorStaffDetailResponse.setAvatar(doctor.getAvatar());
        return doctorStaffDetailResponse;
    }

    private ReceptionistStaffDetailResponse toReceptionistStaffDetailResponse(User receptionist){
        ReceptionistStaffDetailResponse receptionistStaffDetailResponse = new ReceptionistStaffDetailResponse();
        receptionistStaffDetailResponse.setId(receptionist.getId());
        receptionistStaffDetailResponse.setStaffCode("STF-"+receptionist.getId());
        receptionistStaffDetailResponse.setEmail(receptionist.getEmail());
        receptionistStaffDetailResponse.setPhone(receptionist.getPhone());
        receptionistStaffDetailResponse.setCreatedBy(receptionist.getCreatedBy().getFirstName()+"  "+receptionist.getCreatedBy().getMiddleName()+ " "+receptionist.getCreatedBy().getLastName());
        receptionistStaffDetailResponse.setCreatedAt(receptionist.getCreatedAt());
        receptionistStaffDetailResponse.setAccountStatus(receptionist.getStatus());
        receptionistStaffDetailResponse.setFullName(receptionist.getFirstName()+" "+ receptionist.getMiddleName()+" "+receptionist.getLastName());
        receptionistStaffDetailResponse.setRoleName("RECEPTIONIST");
        receptionistStaffDetailResponse.setRoleLabel("Lễ tân");
        receptionistStaffDetailResponse.setWorkingStatus("Đang hoạt động");
        receptionistStaffDetailResponse.setAvatar(receptionist.getAvatar());
        receptionistStaffDetailResponse.setGender(receptionist.getGender());
        receptionistStaffDetailResponse.setUpdatedAt(receptionist.getUpdatedAt());
        receptionistStaffDetailResponse.setBio(receptionist.getBio());
        return receptionistStaffDetailResponse;
    }
    public ReceptionistStaffDetailResponse findReceptionistStaffDetailById(Long id){
        return toReceptionistStaffDetailResponse(findReceptionistById(id));
    }



    public Long countDoctorsAppointmentByAppointmentStatus(String appointmentStatus, Long doctorId){
       return userRepository.countDoctorsAppointmentByAppointmentStatus(appointmentStatus, doctorId);
    }


}
