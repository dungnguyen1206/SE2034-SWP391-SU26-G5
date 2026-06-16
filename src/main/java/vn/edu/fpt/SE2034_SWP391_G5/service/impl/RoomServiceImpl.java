package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.RoomResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoomRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.RoomService;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

   private final RoomRepository roomRepository;

    @Override
    public List<RoomResponse> getRoomsByDepartmentId(Long departmentId) {
        List<RoomResponse> roomResponses = new ArrayList<>();
        List<Room> rooms = roomRepository.getRoomsByDepartmentId(departmentId);
        rooms.forEach(room -> {roomResponses.add(toRoomResponse(room));});
        return roomResponses;
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        List<RoomResponse> roomResponses = new ArrayList<>();
        List<Room> rooms = roomRepository.findAll();
        rooms.forEach(room -> {roomResponses.add(toRoomResponse(room));});
        return roomResponses;
    }

    private RoomResponse toRoomResponse(Room room) {
        RoomResponse roomResponse = RoomResponse.builder().
                id(room.getId())
                .name(room.getName())
                .roomNumber(room.getRoomNumber())
                .departmentId(room.getDepartment().getId())
                .roomStatus(room.getStatus())
                .build();
        return roomResponse;
    }
}
