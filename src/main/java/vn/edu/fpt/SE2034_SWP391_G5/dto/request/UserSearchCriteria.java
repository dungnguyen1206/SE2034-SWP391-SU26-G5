package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Điều kiện tìm kiếm trong màn hình quản lý tài khoản
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {

    private String keyword;

    private String roleName;

    private boolean searchFirstName;

    private boolean searchMiddleName;

    private boolean searchLastName;
}
