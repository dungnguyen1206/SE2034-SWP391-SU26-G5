package vn.edu.fpt.SE2034_SWP391_G5.controller.notification;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.NotificationService;

@ControllerAdvice
public class NotificationAdvice {

    private final NotificationService notificationService;

    public NotificationAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Biến này sẽ có mặt ở MỌI file HTML bằng cách gọi ${unreadNotificationCount}
    @ModelAttribute("unreadNotificationCount")
    public long addUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null && userDetails.getUser() != null) {
            return notificationService.countUnread(userDetails.getUser());
        }
        return 0; // Nếu chưa đăng nhập thì = 0
    }
}