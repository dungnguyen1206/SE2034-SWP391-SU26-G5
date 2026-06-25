package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;

@SpringBootTest
class DoctorScheduleRepositoryTest {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Test
    void testPaginationPage0AndPage1() {

        Page<User> page0 = doctorScheduleRepository.getDoctorScheduleByFilter(
                "DOCTOR",
                null,
                null,
                null,
                1L,
                PageRequest.of(0, 5)
        );

        System.out.println("===== PAGE 0 =====");
        System.out.println("Page Number: " + page0.getNumber());
        System.out.println("Total Elements: " + page0.getTotalElements());
        System.out.println("Total Pages: " + page0.getTotalPages());
        System.out.println("Content Size: " + page0.getContent().size());

        Page<User> page1 = doctorScheduleRepository.getDoctorScheduleByFilter(
                "DOCTOR",
                null,
                null,
                null,
                1L,
                PageRequest.of(1, 5)
        );

        System.out.println("===== PAGE 1 =====");
        System.out.println("Page Number: " + page1.getNumber());
        System.out.println("Total Elements: " + page1.getTotalElements());
        System.out.println("Total Pages: " + page1.getTotalPages());
        System.out.println("Content Size: " + page1.getContent().size());

        assert page0.getTotalElements() == 10;
        assert page0.getContent().size() == 5;

        assert page1.getTotalElements() == 10;
        assert page1.getContent().size() == 5;
    }
}