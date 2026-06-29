-- ============================================================
-- HAMS SEED DATA - Compatible với schema v8.3
-- Chạy file này SAU KHI đã chạy script tạo cấu trúc bảng
-- Password mặc định tất cả: password (BCrypt hash của "password")
-- ============================================================
USE hams_db;
GO

-- [01] Roles
INSERT INTO roles (name, description) VALUES ('ADMIN',        N'Quản trị hệ thống');
INSERT INTO roles (name, description) VALUES ('MANAGER',      N'Quản lý bệnh viện');
INSERT INTO roles (name, description) VALUES ('DOCTOR',       N'Bác sĩ');
INSERT INTO roles (name, description) VALUES ('RECEPTIONIST', N'Lễ tân');
INSERT INTO roles (name, description) VALUES ('PATIENT',      N'Bệnh nhân');
GO

-- [02] Provinces (34 tỉnh/thành - Nghị quyết 202/2025/QH15)
INSERT INTO provinces (name, type) VALUES (N'Hà Nội',           'CITY');
INSERT INTO provinces (name, type) VALUES (N'Tuyên Quang',      'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lào Cai',          'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lai Châu',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Điện Biên',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Sơn La',           'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Cao Bằng',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lạng Sơn',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Quảng Ninh',       'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Thái Nguyên',      'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Phú Thọ',          'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Bắc Ninh',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Hải Phòng',        'CITY');
INSERT INTO provinces (name, type) VALUES (N'Hưng Yên',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Ninh Bình',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Thanh Hóa',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Nghệ An',          'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Hà Tĩnh',          'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Quảng Trị',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Huế',              'CITY');
INSERT INTO provinces (name, type) VALUES (N'Đà Nẵng',          'CITY');
INSERT INTO provinces (name, type) VALUES (N'Quảng Ngãi',       'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Gia Lai',          'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Khánh Hòa',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Đắk Lắk',          'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lâm Đồng',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Bình Định',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Tây Ninh',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Đồng Nai',         'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'TP. Hồ Chí Minh',  'CITY');
INSERT INTO provinces (name, type) VALUES (N'Cần Thơ',          'CITY');
INSERT INTO provinces (name, type) VALUES (N'Đồng Tháp',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Vĩnh Long',        'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'An Giang',         'PROVINCE');
GO

-- [03] Departments
INSERT INTO departments (name, description, status) VALUES (N'Nội tổng quát',      N'Khám và điều trị các bệnh nội khoa thông thường', 'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Tim mạch',            N'Chẩn đoán và điều trị các bệnh lý tim mạch',      'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Nhi khoa',            N'Khám và điều trị bệnh cho trẻ em từ 0-15 tuổi',   'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Da liễu',             N'Chẩn đoán và điều trị các bệnh về da, tóc, móng', 'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Chẩn đoán hình ảnh', N'Siêu âm, X-quang, CT Scan, MRI',                  'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Tai Mũi Họng',        N'Khám và điều trị các bệnh về tai, mũi, họng',     'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Răng Hàm Mặt',        N'Chăm sóc sức khỏe răng miệng, nhổ răng',          'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Mắt',                 N'Khám và đo thị lực, điều trị các bệnh về mắt',    'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Xét nghiệm',          N'Thực hiện các xét nghiệm máu, sinh hóa',          'ACTIVE');
GO

-- [04] Rooms
-- Tầng 1: Nội tổng quát (Department 1)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 1', 'P101', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 2', 'P102', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 3', 'P103', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 4', 'P104', 'ACTIVE');
-- Tầng 2: Tim mạch (Department 2)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng khám Tim mạch 1', 'P201', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng khám Tim mạch 2', 'P202', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng đo điện tim', 'P203', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng khám Tim mạch VIP', 'P204', 'ACTIVE');
-- Tầng 3: Nhi khoa (Department 3)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (3, N'Phòng khám Nhi 1', 'P301', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (3, N'Phòng khám Nhi 2', 'P302', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (3, N'Phòng khám Nhi 3', 'P303', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (3, N'Phòng Tư vấn Tiêm chủng', 'P304', 'ACTIVE');
-- Tầng 4: Da liễu (Department 4)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (4, N'Phòng khám Da liễu 1', 'P401', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (4, N'Phòng khám Da liễu 2', 'P402', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (4, N'Phòng Thẩm mỹ Da 1', 'P403', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (4, N'Phòng Thẩm mỹ Da 2', 'P404', 'ACTIVE');
-- Tầng 5: Chẩn đoán hình ảnh (Department 5)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng Siêu âm 1', 'P501', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng Siêu âm 2', 'P502', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng X-Quang', 'P503', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng MRI', 'P504', 'ACTIVE');
-- Tầng 6: Tai Mũi Họng (Department 6)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (6, N'Phòng khám TMH 1', 'P601', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (6, N'Phòng khám TMH 2', 'P602', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (6, N'Phòng Nội soi TMH 1', 'P603', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (6, N'Phòng Nội soi TMH 2', 'P604', 'ACTIVE');
-- Tầng 7: Răng Hàm Mặt (Department 7)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (7, N'Phòng Răng Hàm Mặt 1', 'P701', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (7, N'Phòng Răng Hàm Mặt 2', 'P702', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (7, N'Phòng Nhổ răng Tiểu phẫu', 'P703', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (7, N'Phòng Chỉnh nha', 'P704', 'ACTIVE');
-- Tầng 8: Mắt (Department 8)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (8, N'Phòng khám Mắt 1', 'P801', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (8, N'Phòng khám Mắt 2', 'P802', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (8, N'Phòng Đo Khúc xạ 1', 'P803', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (8, N'Phòng Đo Khúc xạ 2', 'P804', 'ACTIVE');
-- Tầng 9: Xét nghiệm (Department 9)
INSERT INTO rooms (department_id, name, room_number, status) VALUES (9, N'Phòng Lấy mẫu Xét nghiệm 1', 'P901', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (9, N'Phòng Lấy mẫu Xét nghiệm 2', 'P902', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (9, N'Phòng Xét nghiệm Huyết học', 'P903', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (9, N'Phòng Xét nghiệm Sinh hóa', 'P904', 'ACTIVE');
GO

-- [05] Medical Services
-- === BẮT BUỘC GIỮ NGUYÊN ID 1 ĐẾN 8 ĐỂ KHÔNG LỖI FOREIGN KEY CỦA APPOINTMENT VÀ INVOICE ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Khám nội tổng quát', 200000, 30, 'ACTIVE'); -- ID 1
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Tư vấn dinh dưỡng',  150000, 20, 'ACTIVE'); -- ID 2
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Khám tim mạch',      350000, 45, 'ACTIVE'); -- ID 3
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Đo điện tim (ECG)',   100000, 15, 'ACTIVE'); -- ID 4
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Khám nhi tổng quát', 180000, 30, 'ACTIVE'); -- ID 5
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Tư vấn tiêm chủng',  100000, 20, 'ACTIVE'); -- ID 6
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (4, N'Khám da liễu',       250000, 30, 'ACTIVE'); -- ID 7
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'Siêu âm ổ bụng',     250000, 30, 'ACTIVE'); -- ID 8

-- === THÊM CÁC DỊCH VỤ MỚI ===
-- Khoa Nội tổng quát (Dept 1)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Khám nội tiêu hóa', 250000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Khám nội thần kinh', 300000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Khám nội tiết', 250000, 30, 'ACTIVE');

-- Khoa Tim mạch (Dept 2)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Siêu âm tim Doppler', 400000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Holter nhịp tim 24h', 800000, 30, 'ACTIVE');

-- Khoa Nhi khoa (Dept 3)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Khám dinh dưỡng trẻ em', 200000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Khám hen phế quản nhi', 250000, 30, 'ACTIVE');

-- Khoa Da liễu (Dept 4)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (4, N'Soi da', 150000, 15, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (4, N'Điều trị mụn trứng cá', 500000, 45, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (4, N'Xóa nốt ruồi bằng Laser', 300000, 30, 'ACTIVE');

-- Khoa Chẩn đoán hình ảnh (Dept 5)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'Siêu âm tuyến giáp', 200000, 15, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'X-quang phổi thẳng', 150000, 15, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'Chụp CT Scan sọ não', 1500000, 45, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'Chụp MRI cột sống', 2500000, 60, 'ACTIVE');

-- Khoa Tai Mũi Họng (Dept 6)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (6, N'Khám Tai Mũi Họng', 200000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (6, N'Nội soi Tai Mũi Họng', 300000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (6, N'Lấy dị vật Tai Mũi Họng', 500000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (6, N'Đo thính lực', 200000, 20, 'ACTIVE');

-- Khoa Răng Hàm Mặt (Dept 7)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (7, N'Khám Răng Hàm Mặt', 150000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (7, N'Nhổ răng khôn', 1500000, 60, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (7, N'Cạo vôi răng, đánh bóng', 300000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (7, N'Trám răng thẩm mỹ', 400000, 30, 'ACTIVE');

-- Khoa Mắt (Dept 8)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (8, N'Khám mắt tổng quát', 200000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (8, N'Đo thị lực máy tự động', 100000, 15, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (8, N'Soi đáy mắt', 250000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (8, N'Lấy dị vật giác mạc', 400000, 30, 'ACTIVE');

-- Khoa Xét nghiệm (Dept 9)
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (9, N'Xét nghiệm máu cơ bản (22 chỉ số)', 350000, 10, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (9, N'Xét nghiệm sinh hóa máu', 400000, 10, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (9, N'Xét nghiệm nước tiểu toàn phần', 100000, 10, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (9, N'Xét nghiệm viêm gan B (HBsAg)', 150000, 10, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (9, N'Xét nghiệm đường huyết (HbA1c)', 200000, 10, 'ACTIVE');
GO

-- [06] Users - ADMIN & MANAGER
-- password = "password" (BCrypt)
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin', 'admin@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Quản', N'Trị', N'Admin', '0900000001', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin02', 'admin02@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Minh', N'Hoàng', N'Vũ', '0900000009', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager01', 'manager01@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hùng', N'Văn', N'Trần', '0900000002', 'MALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager02', 'manager02@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Thảo', N'Phương', N'Lê', '0900000003', 'FEMALE', 1);
GO

-- [06b] Users - DOCTOR (department_id, experience_years NOT NULL)
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.nguyenan', 'dr.nguyenan@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'An', N'Văn', N'Nguyễn', '0901111111', 'MALE', 1, 12, N'Tiến sĩ Y khoa', 'LIC-BS-001', N'Bác sĩ chuyên khoa II Nội tổng quát.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.lequang', 'dr.lequang@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Quang', N'Minh', N'Lê', '0901111112', 'MALE', 1, 6, N'Thạc sĩ Y khoa', 'LIC-BS-004', N'Chuyên khám tầm soát bệnh mạn tính.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.tranmai', 'dr.tranmai@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Mai', N'Thị', N'Trần', '0902222222', 'FEMALE', 2, 8, N'Thạc sĩ Y khoa', 'LIC-BS-002', N'Chuyên gia tim mạch can thiệp.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.phambinh', 'dr.phambinh@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Bình', N'Quốc', N'Phạm', '0902222223', 'MALE', 2, 15, N'Phó Giáo sư, Tiến sĩ', 'LIC-BS-005', N'Chuyên gia đầu ngành về suy tim.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.leanh', 'dr.leanh@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Anh', N'Hoàng', N'Lê', '0903333333', 'MALE', 3, 5, N'Bác sĩ chuyên khoa I', 'LIC-BS-003', N'Bác sĩ Nhi khoa giàu kinh nghiệm.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.danghuong', 'dr.danghuong@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hương', N'Thu', N'Đặng', '0903333334', 'FEMALE', 3, 9, N'Thạc sĩ Nhi khoa', 'LIC-BS-006', N'Chuyên khoa hô hấp nhi.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.hoanglinh', 'dr.hoanglinh@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Linh', N'Khánh', N'Hoàng', '0904444411', 'FEMALE', 4, 7, N'Bác sĩ chuyên khoa I', 'LIC-BS-007', N'Chuyên thẩm mỹ da liễu.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.vutung', 'dr.vutung@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Tùng', N'Thanh', N'Vũ', '0904444412', 'MALE', 4, 11, N'Tiến sĩ Y khoa', 'LIC-BS-008', N'Chuyên da liễu tự miễn.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.minhtai', 'dr.minhtai@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Tài', N'Minh', N'Phan', '0905555513', 'MALE', 6, 8, N'Thạc sĩ Y khoa', 'LIC-BS-009', N'Chuyên khoa Tai Mũi Họng.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.thuyrang', 'dr.thuyrang@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Thúy', N'Thị', N'Lý', '0905555514', 'FEMALE', 7, 10, N'Bác sĩ chuyên khoa I', 'LIC-BS-010', N'Chuyên Răng Hàm Mặt.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.phuong', 'dr.phuong@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Phương', N'Thu', N'Nguyễn', '0905555515', 'FEMALE', 5, 12, N'Tiến sĩ Y khoa', 'LIC-BS-011', N'Chuyên gia chẩn đoán hình ảnh.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.hung', 'dr.hung@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hùng', N'Quang', N'Đỗ', '0905555516', 'MALE', 8, 9, N'Thạc sĩ Y khoa', 'LIC-BS-012', N'Bác sĩ chuyên khoa Mắt.', 'ACTIVE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, experience_years, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.loan', 'dr.loan@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Loan', N'Thanh', N'Trần', '0905555517', 'FEMALE', 9, 15, N'Tiến sĩ Y khoa', 'LIC-BS-013', N'Trưởng khoa Xét nghiệm.', 'ACTIVE', 3);
GO

-- [06c] Users - RECEPTIONIST
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept.linh', 'recept.linh@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Linh', N'Thị', N'Phạm', '0904444444', 'FEMALE', 3);

-- [06d] Users - PATIENT
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.thanh', 'nguyenminhthanh@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Thành', N'Minh', N'Nguyễn', '0905555551', 'MALE',   '1990-05-15', 'O+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.huong', 'phamthihuong@gmail.com',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hương', N'Thị',   N'Phạm',   '0905555552', 'FEMALE', '1995-08-22', 'A+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.duc',   'levandduc@gmail.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Đức',   N'Văn',    N'Lê',      '0905555553', 'MALE',   '1985-03-10', 'B+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.lan',   'dothiblan@gmail.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 0, N'Lan',   N'Thị B',  N'Đỗ',      '0905555554', 'FEMALE', '2000-11-30', 'AB+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.nam',   'hoangnham@gmail.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Nam',   N'Hoài',   N'Hoàng',   '0905555555', 'MALE',   '1993-01-25', 'O-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.dung', 'dungnguyen@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Dũng', N'Tiến', N'Nguyễn', '0905555556', 'MALE', '1998-04-12', 'A-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.hoa', 'hoatran@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hoa', N'Thị', N'Trần', '0905555557', 'FEMALE', '1982-10-05', 'O+');

-- [06e] Users - Lễ tân bổ sung
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept.mai', 'recept.mai@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Mai', N'Phương', N'Nguyễn', '0904444445', 'FEMALE', 3);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept.tuan', 'recept.tuan@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Tuấn', N'Anh', N'Lê', '0904444446', 'MALE', 3);

-- [06f] Users - PATIENT (Tạo thêm 50 bệnh nhân mẫu)
DECLARE @i INT = 1;
DECLARE @username VARCHAR(50);
DECLARE @email VARCHAR(100);
DECLARE @phone VARCHAR(20);

WHILE @i <= 50
BEGIN
    SET @username = 'patient.test' + CAST(@i AS VARCHAR(2));
    SET @email = 'patient.test' + CAST(@i AS VARCHAR(2)) + '@gmail.com';
    SET @phone = '0990000' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2);
    
    INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
    VALUES (@username, @email, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Test', N'Bệnh Nhân', CAST(@i AS NVARCHAR(10)), @phone, 'MALE', '1990-01-01', 'O+');
    
    SET @i = @i + 1;
END
GO

-- [07] User Roles (Gán tự động bằng Script để bất biến ID)
INSERT INTO user_roles (user_id, role_id)
SELECT id, 1 FROM users WHERE username IN ('admin', 'admin02');

INSERT INTO user_roles (user_id, role_id)
SELECT id, 2 FROM users WHERE username LIKE 'manager%';

INSERT INTO user_roles (user_id, role_id)
SELECT id, 3 FROM users WHERE username LIKE 'dr.%';

INSERT INTO user_roles (user_id, role_id)
SELECT id, 4 FROM users WHERE username LIKE 'recept.%';

INSERT INTO user_roles (user_id, role_id)
SELECT id, 5 FROM users WHERE username LIKE 'patient.%';
GO

-- [08] User Addresses
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 30, N'123 Nguyễn Huệ, Quận 1', 1 FROM users WHERE username = 'patient.thanh';

INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'45 Trần Duy Hưng, Cầu Giấy', 1 FROM users WHERE username = 'patient.huong';

INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 21, N'88 Lê Duẩn, Hải Châu', 1 FROM users WHERE username = 'patient.duc';

INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 30, N'12 Cộng Hòa, Phường 4, Tân Bình', 1 FROM users WHERE username = 'patient.lan';

-- Thêm địa chỉ mặc định cho các bệnh nhân có sẵn còn lại
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'Số 1, Phố Cổ, Hà Nội', 1 FROM users WHERE username IN ('patient.nam', 'patient.dung', 'patient.hoa');

-- Thêm địa chỉ tự động cho 50 bệnh nhân test (Chọn province ngẫu nhiên hoặc 1)
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'Nhà số ' + CAST(id AS NVARCHAR(10)) + N', Đường Test', 1 
FROM users 
WHERE username LIKE 'patient.test%';
GO

-- [09] Week Schedules (bảng mới v8.3 - REQUIRED trước doctor_schedules)
-- Tự động sinh lịch tuần từ tuần trước đến tuần sau nữa (để cover -7 đến +7 ngày)
DECLARE @Today DATE = CAST(GETDATE() AS DATE);
-- Cách tính ngày Thứ 2 an toàn tuyệt đối trong SQL Server (không phụ thuộc DATEFIRST)
DECLARE @Monday_ThisWeek DATE = DATEADD(wk, DATEDIFF(wk, 0, @Today), 0);
DECLARE @Monday_LastWeek DATE = DATEADD(day, -7, @Monday_ThisWeek);
DECLARE @Monday_NextWeek DATE = DATEADD(day, 7, @Monday_ThisWeek);
DECLARE @Monday_Next2Week DATE = DATEADD(day, 14, @Monday_ThisWeek);

INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by) VALUES (@Monday_LastWeek, DATEADD(day, 6, @Monday_LastWeek), 'FINALIZED', 3);
INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by) VALUES (@Monday_ThisWeek, DATEADD(day, 6, @Monday_ThisWeek), 'FINALIZED', 3);
INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by) VALUES (@Monday_NextWeek, DATEADD(day, 6, @Monday_NextWeek), 'PUBLISHED', 3);
INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by) VALUES (@Monday_Next2Week, DATEADD(day, 6, @Monday_Next2Week), 'DRAFT', 3);
GO

-- [10] Doctor Schedules (v8.3: cần week_schedule_id)
-- Tự động sinh lịch khám cho TẤT CẢ các bác sĩ từ 7 ngày trước đến 7 ngày sau (tổng 15 ngày)
DECLARE @StartDate DATE = CAST(DATEADD(day, -7, GETDATE()) AS DATE);
DECLARE @EndDate DATE = CAST(DATEADD(day, 7, GETDATE()) AS DATE);
DECLARE @CurrDate DATE = @StartDate;

WHILE @CurrDate <= @EndDate
BEGIN
    DECLARE @WeekId BIGINT = (SELECT id FROM week_schedules WHERE @CurrDate BETWEEN week_start_date AND week_end_date);
    
    INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
    SELECT @WeekId, u.id, (SELECT TOP 1 id FROM rooms r WHERE r.department_id = u.department_id), @CurrDate, 'MORNING', 'ACTIVE', 3
    FROM users u WHERE u.username LIKE 'dr.%';

    INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
    SELECT @WeekId, u.id, (SELECT TOP 1 id FROM rooms r WHERE r.department_id = u.department_id), @CurrDate, 'AFTERNOON', 'ACTIVE', 3
    FROM users u WHERE u.username LIKE 'dr.%';

    SET @CurrDate = DATEADD(day, 1, @CurrDate);
END
GO

-- [11] Time Slots (Tự động sinh slot cho TẤT CẢ các lịch khám vừa tạo)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status)
SELECT 
    ds.id, 
    t.start_time, 
    t.end_time, 
    0, 
    5, 
    'AVAILABLE'
FROM doctor_schedules ds
CROSS JOIN (
    -- MORNING SHIFT (08:00 - 12:00)
    SELECT '08:00' AS start_time, '08:30' AS end_time, 'MORNING' AS shift
    UNION ALL SELECT '08:30', '09:00', 'MORNING'
    UNION ALL SELECT '09:00', '09:30', 'MORNING'
    UNION ALL SELECT '09:30', '10:00', 'MORNING'
    UNION ALL SELECT '10:00', '10:30', 'MORNING'
    UNION ALL SELECT '10:30', '11:00', 'MORNING'
    UNION ALL SELECT '11:00', '11:30', 'MORNING'
    UNION ALL SELECT '11:30', '12:00', 'MORNING'
    -- AFTERNOON SHIFT (13:00 - 16:30)
    UNION ALL SELECT '13:00', '13:30', 'AFTERNOON'
    UNION ALL SELECT '13:30', '14:00', 'AFTERNOON'
    UNION ALL SELECT '14:00', '14:30', 'AFTERNOON'
    UNION ALL SELECT '14:30', '15:00', 'AFTERNOON'
    UNION ALL SELECT '15:00', '15:30', 'AFTERNOON'
    UNION ALL SELECT '15:30', '16:00', 'AFTERNOON'
    UNION ALL SELECT '16:00', '16:30', 'AFTERNOON'
) t
WHERE ds.shift = t.shift;
GO

-- [12] Appointments (Sinh data cho 15 ngày từ -7 đến +7)
DECLARE @DayOffset INT = -7;
DECLARE @ApptIndex INT = 1;

WHILE @DayOffset <= 7
BEGIN
    DECLARE @TargetDate DATE = CAST(DATEADD(day, @DayOffset, GETDATE()) AS DATE);
    DECLARE @DailyAppts INT = 1;
    
    -- Sinh 5 lịch hẹn mỗi ngày (Tổng cộng 75 lịch hẹn)
    WHILE @DailyAppts <= 5 
    BEGIN
        DECLARE @SelectedSlotId BIGINT;
        DECLARE @SelectedDoctorId BIGINT;
        DECLARE @SelectedServiceId BIGINT;
        
        -- Chọn 1 slot trống ngẫu nhiên
        SELECT TOP 1 
            @SelectedSlotId = ts.id,
            @SelectedDoctorId = ds.doctor_id
        FROM time_slots ts
        JOIN doctor_schedules ds ON ts.schedule_id = ds.id
        WHERE ds.work_date = @TargetDate AND ts.booked_capacity < ts.max_capacity
        ORDER BY NEWID();
        
        -- Lấy dịch vụ tương ứng của khoa bác sĩ
        SELECT TOP 1 @SelectedServiceId = ms.id 
        FROM medical_services ms 
        JOIN users u ON ms.department_id = u.department_id 
        WHERE u.id = @SelectedDoctorId 
        ORDER BY NEWID();
        
        -- Chọn 1 bệnh nhân ngẫu nhiên
        DECLARE @SelectedPatientId BIGINT;
        SELECT TOP 1 @SelectedPatientId = id FROM users WHERE username LIKE 'patient.%' ORDER BY NEWID();
        
        -- Quyết định Status (Đúng nghiệp vụ)
        DECLARE @ApptStatus VARCHAR(20);
        DECLARE @Rand INT = ABS(CHECKSUM(NEWID())) % 100;
        
        IF @DayOffset < 0
        BEGIN
            -- Quá khứ: COMPLETED (60%), CANCELLED (20%), NO_SHOW (20%)
            IF @Rand < 60 SET @ApptStatus = 'COMPLETED';
            ELSE IF @Rand < 80 SET @ApptStatus = 'CANCELLED';
            ELSE SET @ApptStatus = 'NO_SHOW';
        END
        ELSE IF @DayOffset = 0
        BEGIN
            -- Hôm nay: CONFIRMED (33%), WAITING (33%), EXAMINING (34%)
            IF @Rand < 33 SET @ApptStatus = 'CONFIRMED';
            ELSE IF @Rand < 66 SET @ApptStatus = 'WAITING';
            ELSE SET @ApptStatus = 'EXAMINING';
        END
        ELSE
        BEGIN
            -- Tương lai: CONFIRMED (80%), CANCELLED (20%)
            IF @Rand < 80 SET @ApptStatus = 'CONFIRMED';
            ELSE SET @ApptStatus = 'CANCELLED';
        END
        
        -- Phát sinh mã Appointment (VD: APT-2026060001)
        DECLARE @ApptCode VARCHAR(20) = 'APT-' + FORMAT(GETDATE(), 'yyyyMM') + RIGHT('0000' + CAST(@ApptIndex AS VARCHAR), 4);
        
        -- Tính toán check_in_time ngẫu nhiên (-15 phút đến +4 phút so với giờ bắt đầu)
        DECLARE @SlotStartTime TIME;
        SELECT @SlotStartTime = start_time FROM time_slots WHERE id = @SelectedSlotId;

        DECLARE @CheckInTime DATETIME2 = NULL;
        IF @ApptStatus IN ('WAITING', 'EXAMINING', 'COMPLETED')
        BEGIN
            DECLARE @RandomMinutes INT = (ABS(CHECKSUM(NEWID())) % 20) - 15;
            SET @CheckInTime = DATEADD(minute, @RandomMinutes, CAST(CAST(@TargetDate AS DATETIME) + CAST(@SlotStartTime AS DATETIME) AS DATETIME2));
        END
        
        -- Bắt đầu Insert Appointment
        INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
        VALUES (@ApptCode, @SelectedPatientId, @SelectedDoctorId, @SelectedServiceId, @SelectedSlotId, @TargetDate, @CheckInTime, @ApptStatus, N'Khám định kỳ tổng quát tự động sinh');
        
        DECLARE @NewApptId BIGINT = SCOPE_IDENTITY();
        
        -- Cập nhật Slot (Chỉ CANCELLED mới nhả capacity, NO_SHOW vẫn giữ)
        IF @ApptStatus <> 'CANCELLED'
        BEGIN
            UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SelectedSlotId;
        END
        
        -- [13 & 14 & 15] Tạo Bệnh án và Hóa đơn nếu trạng thái là COMPLETED
        IF @ApptStatus = 'COMPLETED'
        BEGIN
            INSERT INTO medical_records (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion, prescription_text, heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
            VALUES (@NewApptId, @SelectedPatientId, @SelectedDoctorId, N'Đau mỏi cơ thể', N'Cảm cúm thông thường (J00)', N'Uống thuốc đầy đủ và nghỉ ngơi', N'1. Paracetamol 500mg x 2 viên/ngày', 80, '120/80', 5.0, 60.0, 'FINALIZED', @SelectedDoctorId);
            
            DECLARE @NewMedRecId BIGINT = SCOPE_IDENTITY();
            DECLARE @SvcPrice DECIMAL(12,2) = (SELECT reference_price FROM medical_services WHERE id = @SelectedServiceId);
            
            -- Invoice: PAID (80%), UNPAID (20%) để test
            DECLARE @InvStatus VARCHAR(20) = CASE WHEN ABS(CHECKSUM(NEWID())) % 100 < 80 THEN 'PAID' ELSE 'UNPAID' END;
            DECLARE @PaidAt DATETIME2 = CASE WHEN @InvStatus = 'PAID' THEN @TargetDate ELSE NULL END;
            DECLARE @InvCode VARCHAR(20) = 'INV-' + FORMAT(GETDATE(), 'yyyyMM') + RIGHT('0000' + CAST(@ApptIndex AS VARCHAR), 4);
            
            INSERT INTO invoices (invoice_code, medical_record_id, total_amount, payment_method, payment_status, paid_at)
            VALUES (@InvCode, @NewMedRecId, @SvcPrice, CASE WHEN @InvStatus = 'PAID' THEN 'CASH' ELSE 'PENDING' END, @InvStatus, @PaidAt);
            
            DECLARE @NewInvId BIGINT = SCOPE_IDENTITY();
            
            INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total)
            VALUES (@NewInvId, @SelectedServiceId, (SELECT name FROM medical_services WHERE id = @SelectedServiceId), @SvcPrice, 1, @SvcPrice);
        END
        
        -- [16] Notification cho bệnh nhân
        IF @ApptStatus IN ('CONFIRMED', 'WAITING', 'EXAMINING', 'COMPLETED')
        BEGIN
            INSERT INTO notifications (user_id, title, message, notification_type, is_read, related_entity_id, related_entity_type)
            VALUES (@SelectedPatientId, N'Cập nhật lịch khám', N'Ca khám ' + @ApptCode + N' đã chuyển trạng thái ' + @ApptStatus, 'APPOINTMENT_CONFIRMED', 0, @NewApptId, 'APPOINTMENT');
        END
        
        SET @DailyAppts = @DailyAppts + 1;
        SET @ApptIndex = @ApptIndex + 1;
    END
    
    SET @DayOffset = @DayOffset + 1;
END
GO

-- ============================================================
-- VERIFY
-- ============================================================
SELECT [table], [rows] FROM (
    SELECT 'roles'            AS [table], COUNT(*) AS [rows] FROM roles
    UNION ALL SELECT 'provinces',       COUNT(*) FROM provinces
    UNION ALL SELECT 'departments',     COUNT(*) FROM departments
    UNION ALL SELECT 'rooms',           COUNT(*) FROM rooms
    UNION ALL SELECT 'medical_services',COUNT(*) FROM medical_services
    UNION ALL SELECT 'users',           COUNT(*) FROM users
    UNION ALL SELECT 'user_roles',      COUNT(*) FROM user_roles
    UNION ALL SELECT 'week_schedules',  COUNT(*) FROM week_schedules
    UNION ALL SELECT 'doctor_schedules',COUNT(*) FROM doctor_schedules
    UNION ALL SELECT 'time_slots',      COUNT(*) FROM time_slots
    UNION ALL SELECT 'appointments',    COUNT(*) FROM appointments
    UNION ALL SELECT 'medical_records', COUNT(*) FROM medical_records
    UNION ALL SELECT 'invoices',        COUNT(*) FROM invoices
) t ORDER BY [table];
GO
