package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Notification;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    //1. Find all user notification and order by createdAt
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    //2.Find top 5 new notification (using for droplist)
    List<Notification> findTop5ByUserOrderByCreatedAtDesc(User user);

    //3. Count the number of unread notification
    Long  countByUserAndIsRead(User user, boolean isRead);


}
