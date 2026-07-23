package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Province;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ProvinceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ProvinceService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }
}
