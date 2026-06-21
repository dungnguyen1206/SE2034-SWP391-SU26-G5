import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class PatchPagination {
    public static void main(String[] args) throws Exception {
        String path = "src/main/resources/templates/admin/users/account-list.html";
        String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        // 1. Add pageFilter to filterForm and reset page on search
        String oldFilterFormStart = "<form id=\"filterForm\" th:action=\"@{/admin/account-list}\" method=\"get\" class=\"filter-bar\">\n" +
"                    <div class=\"filter-left\">\n" +
"                        <div class=\"search-wrapper\">";
        String newFilterFormStart = "<form id=\"filterForm\" th:action=\"@{/admin/account-list}\" method=\"get\" class=\"filter-bar\">\n" +
"                    <input type=\"hidden\" name=\"page\" id=\"pageFilter\" th:value=\"${currentPage}\">\n" +
"                    <div class=\"filter-left\">\n" +
"                        <div class=\"search-wrapper\">";
        content = content.replace(oldFilterFormStart, newFilterFormStart);

        String oldSearchBtn = "<button type=\"submit\" class=\"filter-btn-trigger\" title=\"Bộ lọc nâng cao\">";
        String newSearchBtn = "<button type=\"submit\" class=\"filter-btn-trigger\" title=\"Bộ lọc nâng cao\" onclick=\"document.getElementById('pageFilter').value='1';\">";
        content = content.replace(oldSearchBtn, newSearchBtn);

        // 2. Reset page on role filter pills
        content = content.replace("document.getElementById('filterForm').submit();\">Tất cả", "document.getElementById('pageFilter').value='1'; document.getElementById('filterForm').submit();\">Tất cả");
        content = content.replace("document.getElementById('filterForm').submit();\">Bệnh nhân", "document.getElementById('pageFilter').value='1'; document.getElementById('filterForm').submit();\">Bệnh nhân");
        content = content.replace("document.getElementById('filterForm').submit();\">Bác sĩ", "document.getElementById('pageFilter').value='1'; document.getElementById('filterForm').submit();\">Bác sĩ");
        content = content.replace("document.getElementById('filterForm').submit();\">Lễ tân", "document.getElementById('pageFilter').value='1'; document.getElementById('filterForm').submit();\">Lễ tân");
        content = content.replace("document.getElementById('filterForm').submit();\">Quản lý", "document.getElementById('pageFilter').value='1'; document.getElementById('filterForm').submit();\">Quản lý");
        content = content.replace("document.getElementById('filterForm').submit();\">Quản trị viên", "document.getElementById('pageFilter').value='1'; document.getElementById('filterForm').submit();\">Quản trị viên");

        // 3. Replace Pagination HTML
        String oldPagination = "<!-- Pagination -->\n" +
"                <div class=\"pagination-container\">\n" +
"                    <span>Trang 1 / 2 &bull; Tổng số 12 tài khoản</span>\n" +
"                    <div class=\"pagination-controls\">\n" +
"                        <button class=\"pagination-btn\" disabled><i class=\"fa-solid fa-chevron-left\"></i></button>\n" +
"                        <button class=\"pagination-btn active\">1</button>\n" +
"                        <button class=\"pagination-btn\">2</button>\n" +
"                        <button class=\"pagination-btn\"><i class=\"fa-solid fa-chevron-right\"></i></button>\n" +
"                    </div>\n" +
"                </div>";
        String newPagination = "<!-- Pagination -->\n" +
"                <div class=\"pagination-container\" th:if=\"${totalPages > 0}\">\n" +
"                    <span>Trang <span th:text=\"${currentPage}\"></span> / <span th:text=\"${totalPages}\"></span> &bull; Tổng số <span th:text=\"${totalItems}\"></span> tài khoản</span>\n" +
"                    <div class=\"pagination-controls\">\n" +
"                        <button type=\"button\" class=\"pagination-btn\" th:disabled=\"${currentPage == 1}\" th:onclick=\"'goToPage(' + (${currentPage - 1}) + ')'\"><i class=\"fa-solid fa-chevron-left\"></i></button>\n" +
"                        \n" +
"                        <th:block th:each=\"i : ${#numbers.sequence(1, totalPages)}\">\n" +
"                            <button type=\"button\" class=\"pagination-btn\" th:classappend=\"${currentPage == i} ? 'active' : ''\" th:text=\"${i}\" th:onclick=\"'goToPage(' + ${i} + ')'\"></button>\n" +
"                        </th:block>\n" +
"                        \n" +
"                        <button type=\"button\" class=\"pagination-btn\" th:disabled=\"${currentPage == totalPages}\" th:onclick=\"'goToPage(' + (${currentPage + 1}) + ')'\"><i class=\"fa-solid fa-chevron-right\"></i></button>\n" +
"                    </div>\n" +
"                </div>";
        
        if (content.contains(oldPagination)) {
            content = content.replace(oldPagination, newPagination);
        } else {
            System.out.println("Could not find old pagination block");
        }

        // 4. Add goToPage function to JS
        String goToPageJs = "\n        function goToPage(page) {\n" +
"            document.getElementById('pageFilter').value = page;\n" +
"            document.getElementById('filterForm').submit();\n" +
"        }\n";
        content = content.replace("<script>", "<script>" + goToPageJs);

        Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
        System.out.println("Pagination Patch Complete");
    }
}
