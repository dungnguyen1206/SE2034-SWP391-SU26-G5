package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DepartmentRepository;
import org.springframework.cache.annotation.Cacheable;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Cacheable("departments")
    public List<Department> getAllActiveDepartments() {
        return departmentRepository.findAll().stream()
                .filter(d -> "ACTIVE".equals(d.getStatus()))
                .toList();
    }

    @Override
    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoa với id: " + id));
    }

    @Override
    public Department getDepartmentByName(String name) {
        return departmentRepository.findDepartmentByDepartmentName(name);
    }


}
