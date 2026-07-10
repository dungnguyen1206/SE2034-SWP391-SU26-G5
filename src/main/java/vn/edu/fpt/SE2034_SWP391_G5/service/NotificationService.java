package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.NotificationResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Notification;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface NotificationService {

    // Tạo một thông báo mới và lưu vào DB
    void createNotification(User user, String title, String message, String type, Long relatedEntityId, String relatedEntityType);

    // Lấy 5 thông báo mới nhất cho menu dropdown
    List<NotificationResponse> getTop5ForUser(User user);

    // Lấy toàn bộ thông báo của user cho trang xem chi tiết
    List<NotificationResponse> getAllForUser(User user);

    // Đếm số lượng thông báo chưa đọc
    long countUnread(User user);

    // Đánh dấu 1 thông báo cụ thể là đã đọc
    void markAsRead(Long notificationId);

    // Đánh dấu tất cả thông báo của 1 user là đã đọc
    void markAllAsRead(User user);


}
