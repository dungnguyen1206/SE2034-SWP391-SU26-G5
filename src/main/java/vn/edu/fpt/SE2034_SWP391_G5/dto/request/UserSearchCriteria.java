package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import java.util.List;

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

    // Dựng điều kiện tìm kiếm từ tham số trên URL
    public static UserSearchCriteria from(String keyword, String roleName, List<String> searchFields) {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setKeyword(normalize(keyword));
        criteria.setRoleName(normalize(roleName));

        // Không tick trường nào thì mặc định tìm trong cả họ, tên đệm và tên
        boolean noneSelected = searchFields == null || searchFields.isEmpty();
        criteria.setSearchFirstName(noneSelected || searchFields.contains("firstName"));
        criteria.setSearchMiddleName(noneSelected || searchFields.contains("middleName"));
        criteria.setSearchLastName(noneSelected || searchFields.contains("lastName"));

        return criteria;
    }

    // Chuỗi rỗng coi như người dùng không nhập gì
    private static String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }
}
