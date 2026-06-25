package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DoctorScheduleRepositoryTest {

    @Autowired
    private DoctorScheduleRepository repository;

    @Test
    void shouldReturnSecondPage() {

        Page<User> page0 =
                repository.getDoctorScheduleByFilter(
                        "DOCTOR",
                        null,
                        null,
                        null,
                        1L,
                        PageRequest.of(0, 5));

        Page<User> page1 =
                repository.getDoctorScheduleByFilter(
                        "DOCTOR",
                        null,
                        null,
                        null,
                        1L,
                        PageRequest.of(1, 5));

        System.out.println(page0.getTotalElements());
        System.out.println(page0.getContent().size());

        System.out.println(page1.getTotalElements());
        System.out.println(page1.getContent().size());
    }
}