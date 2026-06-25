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
GO

-- [04] Rooms
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 1',   'P101', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 2',   'P102', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng khám Tim mạch','P201', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng đo điện tim',  'P202', 'MAINTENANCE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (3, N'Phòng khám Nhi',     'P301', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (4, N'Phòng khám Da liễu', 'P401', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng siêu âm',      'P501', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng X-quang',      'P502', 'ACTIVE');
GO

-- [05] Medical Services
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Khám nội tổng quát', 200000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Tư vấn dinh dưỡng',  150000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Khám tim mạch',      350000, 45, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Đo điện tim (ECG)',   100000, 15, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Khám nhi tổng quát', 180000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Tư vấn tiêm chủng',  100000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (4, N'Khám da liễu',       250000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'Siêu âm ổ bụng',     250000, 30, 'ACTIVE');
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
GO

-- [07] User Roles
-- id: 1=admin, 2=admin02, 3=manager01, 4=manager02
-- 5=dr.nguyenan, 6=dr.lequang, 7=dr.tranmai, 8=dr.phambinh
-- 9=dr.leanh, 10=dr.danghuong, 11=dr.hoanglinh, 12=dr.vutung
-- 13=recept.linh, 14=patient.thanh, 15=patient.huong, 16=patient.duc, 17=patient.lan, 18=patient.nam
INSERT INTO user_roles (user_id, role_id) VALUES (1,  1); -- admin      → ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2,  1); -- admin02    → ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (3,  2); -- manager01  → MANAGER
INSERT INTO user_roles (user_id, role_id) VALUES (4,  2); -- manager02  → MANAGER
INSERT INTO user_roles (user_id, role_id) VALUES (5,  3); -- dr.nguyenan → DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (6,  3); -- dr.lequang  → DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (7,  3); -- dr.tranmai  → DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (8,  3); -- dr.phambinh → DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (9,  3); -- dr.leanh    → DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (10, 3); -- dr.danghuong→ DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (11, 3); -- dr.hoanglinh→ DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (12, 3); -- dr.vutung   → DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (13, 4); -- recept.linh → RECEPTIONIST
INSERT INTO user_roles (user_id, role_id) VALUES (14, 5); -- patient.thanh → PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (15, 5); -- patient.huong → PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (16, 5); -- patient.duc   → PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (17, 5); -- patient.lan   → PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (18, 5); -- patient.nam   → PATIENT
GO

-- [08] User Addresses
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (14, 30, N'123 Nguyễn Huệ, Quận 1',           1);
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (15,  1, N'45 Trần Duy Hưng, Cầu Giấy',       1);
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (16, 21, N'88 Lê Duẩn, Hải Châu',             1);
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (17, 30, N'12 Cộng Hòa, Phường 4, Tân Bình',  1);
GO

-- [09] Week Schedules (bảng mới v8.3 - REQUIRED trước doctor_schedules)
-- Tạo tuần hiện tại và tuần sau
INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by)
VALUES (
    CAST(DATEADD(day, -(DATEPART(dw, GETDATE())-2), CAST(GETDATE() AS DATE)) AS DATE), -- Thứ 2 tuần này
    CAST(DATEADD(day, 8-DATEPART(dw, GETDATE()), CAST(GETDATE() AS DATE)) AS DATE),    -- Chủ nhật tuần này
    'FINALIZED', 3
);

INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by)
VALUES (
    CAST(DATEADD(day, 7-(DATEPART(dw, GETDATE())-2), CAST(GETDATE() AS DATE)) AS DATE), -- Thứ 2 tuần sau
    CAST(DATEADD(day, 15-DATEPART(dw, GETDATE()), CAST(GETDATE() AS DATE)) AS DATE),    -- Chủ nhật tuần sau
    'PUBLISHED', 3
);
GO

-- [10] Doctor Schedules (v8.3: cần week_schedule_id)
-- week_schedule_id=1 (tuần hiện tại), week_schedule_id=2 (tuần sau)
-- BS dr.nguyenan (id=5) - Nội tổng quát - Phòng P101 (room_id=1)
INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
VALUES (1, 5, 1, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'MORNING',   'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
VALUES (1, 5, 1, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'AFTERNOON', 'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
VALUES (2, 5, 1, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'MORNING',   'ACTIVE', 3);

-- BS dr.tranmai (id=7) - Tim mạch - Phòng P201 (room_id=3)
INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
VALUES (1, 7, 3, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'AFTERNOON', 'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
VALUES (1, 7, 3, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'MORNING',   'ACTIVE', 3);
GO

-- [11] Time Slots
-- schedule_id=1 (dr.nguyenan MORNING ngày mai)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '08:00', '08:30', 2, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '08:30', '09:00', 5, 5, 'FULL');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '09:00', '09:30', 0, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '09:30', '10:00', 1, 5, 'AVAILABLE');

-- schedule_id=2 (dr.nguyenan AFTERNOON ngày mai)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (2, '13:00', '13:30', 0, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (2, '13:30', '14:00', 0, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (2, '14:00', '14:30', 0, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (2, '14:30', '15:00', 0, 5, 'AVAILABLE');

-- schedule_id=4 (dr.tranmai AFTERNOON ngày mai)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (4, '13:00', '13:45', 1, 3, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (4, '13:45', '14:30', 3, 3, 'FULL');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (4, '14:30', '15:15', 0, 3, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (4, '15:15', '16:00', 0, 3, 'AVAILABLE');
GO

-- [12] Appointments (status CONFIRMED theo flow của app)
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note)
VALUES ('APT-20260001', 14, 5, 1, 1,
        CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'CONFIRMED', N'Tiền sử dạ dày mãn tính');

INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note)
VALUES ('APT-20260002', 15, 7, 3, 9,
        CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'CONFIRMED', N'Huyết áp cao định kỳ');

INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note)
VALUES ('APT-20260003', 16, 5, 1, 3,
        CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'CONFIRMED', NULL);

INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note)
VALUES ('APT-20260004', 17, 7, 3, 11,
        CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Hủy qua ứng dụng');
GO

-- [13] Medical Records (1-1 với appointment)
INSERT INTO medical_records
    (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion, prescription_text,
     heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
VALUES (1, 14, 5,
    N'Đau thượng vị, buồn nôn, ợ chua liên tục.',
    N'Viêm loét dạ dày tá tràng (K29.5).',
    N'Uống thuốc đúng giờ, kiêng cay nóng.',
    N'1. Omeprazole 20mg x 2 lần/ngày&#10;2. Amoxicillin 1g x 2 lần/ngày',
    76, '120/80', 5.10, 64.00, 'FINALIZED', 5);
GO

-- [14] Invoices
INSERT INTO invoices (invoice_code, medical_record_id, total_amount, payment_method, payment_status, paid_at)
VALUES ('INV-20260001', 1, 300000, 'CASH', 'PAID', GETDATE());
GO

-- [15] Invoice Items
INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total)
VALUES (1, 1, N'Khám nội tổng quát', 200000, 1, 200000);
INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total)
VALUES (1, 8, N'Siêu âm ổ bụng',     100000, 1, 100000);
GO

-- [16] Notifications
INSERT INTO notifications (user_id, title, message, notification_type, is_read, related_entity_id, related_entity_type)
VALUES (14, N'Lịch khám đã xác nhận', N'Ca khám APT-20260001 đã được xác nhận.', 'APPOINTMENT_CONFIRMED', 0, 1, 'APPOINTMENT');
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
