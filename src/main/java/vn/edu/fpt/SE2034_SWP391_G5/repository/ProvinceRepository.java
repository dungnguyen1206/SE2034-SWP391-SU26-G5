package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Province;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {
}
