package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;
import vn.edu.fpt.SE2034_SWP391_G5.enums.RoomStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private Integer id;
    private String name;
    private String roomNumber;
    private Integer departmentId;
    private String roomStatus;

}
