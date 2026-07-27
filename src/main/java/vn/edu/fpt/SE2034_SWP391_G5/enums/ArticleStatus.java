package vn.edu.fpt.SE2034_SWP391_G5.enums;

// Trạng thái của một bài viết
public enum ArticleStatus {
    // Bản nháp, chưa hiển thị công khai
    DRAFT,

    // Đã xuất bản, người dùng xem được
    PUBLISHED,

    // Đã xóa mềm, ẩn khỏi mọi danh sách nhưng dữ liệu vẫn giữ lại
    DELETED
}
