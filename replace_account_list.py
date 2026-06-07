import os

file_path = r'e:\Documents\Projects\SE2034-SWP391-SU26-G5\src\main\resources\templates\admin\users\account-list.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove redundant subtitle
content = content.replace('<p class="page-subtitle">Tổng số 12 tài khoản trong hệ thống</p>', '')

# 2. Change delete buttons to lock buttons
content = content.replace('title="Xóa tài khoản" onclick="openDeleteModal', 'title="Khóa tài khoản" onclick="openLockModal')
content = content.replace('fa-regular fa-trash-can', 'fa-solid fa-lock')

# 3. Update Modal HTML
old_modal = """    <!-- MODAL 2: XÁC NHẬN XÓA TÀI KHOẢN -->
    <div id="deleteModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title" style="color: #ef4444;">Xóa tài khoản</h3>
                <button class="modal-close-btn" onclick="closeModal('deleteModal')"><i class="fa-solid fa-xmark"></i></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa tài khoản của <strong id="deleteUserName">...</strong> không?</p>
                <p style="margin-top: 10px; color: #ef4444; font-size: 0.85rem; display: flex; align-items: center; gap: 6px;">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    <span>Hành động này sẽ xóa vĩnh viễn dữ liệu tài khoản và không thể hoàn tác!</span>
                </p>
            </div>
            <div class="modal-footer">
                <button class="modal-btn cancel" onclick="closeModal('deleteModal')">Hủy</button>
                <button class="modal-btn danger" onclick="handleDeleteConfirm()">Xóa tài khoản</button>
            </div>
        </div>
    </div>"""

new_modal = """    <!-- MODAL 2: XÁC NHẬN KHÓA TÀI KHOẢN -->
    <div id="lockModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title" style="color: #f59e0b;">Khóa tài khoản</h3>
                <button class="modal-close-btn" onclick="closeModal('lockModal')"><i class="fa-solid fa-xmark"></i></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn khóa tài khoản của <strong id="lockUserName">...</strong> không?</p>
                <p style="margin-top: 10px; color: #f59e0b; font-size: 0.85rem; display: flex; align-items: center; gap: 6px;">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    <span>Người dùng sẽ không thể đăng nhập vào hệ thống cho đến khi được mở khóa.</span>
                </p>
            </div>
            <div class="modal-footer">
                <button class="modal-btn cancel" onclick="closeModal('lockModal')">Hủy</button>
                <button class="modal-btn danger" style="background-color: #f59e0b; border-color: #f59e0b;" onclick="handleLockConfirm()">Khóa tài khoản</button>
            </div>
        </div>
    </div>"""

content = content.replace(old_modal, new_modal)

# 4. Update JS for Open Modal
old_js_open = """        // Open Modal Delete
        function openDeleteModal(userName, element) {
            document.getElementById('deleteUserName').innerText = userName;
            currentTargetRow = element.closest('tr');
            document.getElementById('deleteModal').classList.add('active');
        }"""

new_js_open = """        // Open Modal Lock
        function openLockModal(userName, element) {
            document.getElementById('lockUserName').innerText = userName;
            currentTargetRow = element.closest('tr');
            document.getElementById('lockModal').classList.add('active');
        }"""

content = content.replace(old_js_open, new_js_open)

# 5. Update JS for Confirm Lock
old_js_confirm = """        // Handle Confirm Delete
        function handleDeleteConfirm() {
            if (currentTargetRow) {
                currentTargetRow.remove();
                alert('Đã xóa tài khoản thành công!');
            }
            closeModal('deleteModal');
        }"""

new_js_confirm = """        // Handle Confirm Lock
        function handleLockConfirm() {
            if (currentTargetRow) {
                const statusCell = currentTargetRow.querySelector('.status-indicator');
                if (statusCell) {
                    statusCell.innerHTML = '<span class="status-dot inactive"></span><span style="color: var(--text-muted);">Tạm khóa</span>';
                }
                alert('Đã khóa tài khoản thành công!');
            }
            closeModal('lockModal');
        }"""

content = content.replace(old_js_confirm, new_js_confirm)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Done')
