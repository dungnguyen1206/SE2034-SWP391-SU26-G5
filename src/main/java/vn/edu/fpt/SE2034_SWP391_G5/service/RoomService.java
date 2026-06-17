package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.RoomResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;

import java.util.List;

public interface RoomService {

    List<RoomResponse> getRoomsByDepartmentId(Long departmentId);
    List<RoomResponse> getAllRooms();
}
