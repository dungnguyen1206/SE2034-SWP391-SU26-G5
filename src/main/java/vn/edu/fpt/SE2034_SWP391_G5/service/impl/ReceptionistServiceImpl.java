package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionistServiceImpl implements ReceptionistService {

    private final UserRepository userRepository;

    @Override
    public ReceptionistResponse getReceptionistByUsername(String username) {
        List<Object[]> result = userRepository.findReceptionistInfoByUsername(username);

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("Không tìm thấy nhân viên tiếp tân với username: " + username);
        }

        Object[] row = result.get(0);

        Long id = ((Number) row[0]).longValue();
        String fullName = (String) row[1];
        String avatarText = (String) row[2];

        return new ReceptionistResponse(id, fullName, avatarText);
    }
}