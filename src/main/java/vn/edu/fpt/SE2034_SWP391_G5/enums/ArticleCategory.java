package vn.edu.fpt.SE2034_SWP391_G5.enums;

import java.util.ArrayList;
import java.util.List;

// Danh sách chuyên mục bài viết, dùng chung cho cả trang quản lý và trang công khai
public enum ArticleCategory {
    TIM_MACH("Tim mạch"),
    NHI_KHOA("Nhi khoa"),
    DA_LIEU("Da liễu"),
    SUC_KHOE("Sức khỏe"),
    DINH_DUONG("Dinh dưỡng");

    private final String displayName;

    ArticleCategory(String displayName) {
        this.displayName = displayName;
    }

    // Tên chuyên mục hiển thị cho người dùng
    public String getDisplayName() {
        return displayName;
    }

    // Lấy toàn bộ tên chuyên mục để đổ vào ô chọn trên giao diện
    public static List<String> getAllDisplayNames() {
        List<String> names = new ArrayList<>();
        for (ArticleCategory category : values()) {
            names.add(category.getDisplayName());
        }
        return names;
    }
}
