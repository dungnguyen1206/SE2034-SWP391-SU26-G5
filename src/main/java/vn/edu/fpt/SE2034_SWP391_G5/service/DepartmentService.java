package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllActiveDepartments();
    Department getDepartmentById(Integer id);
}
