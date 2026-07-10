package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalServiceResponseForManager {
    private Long id;
    private String serviceName;
    private Department department;
    private BigDecimal servicePrice;
    private Integer timeDuration;
    private String status;
    private String imageUrl;
}
