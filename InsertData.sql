-- ============================================================
--                    DỮ LIỆU MẪU (SEED DATA)
-- ============================================================

-- [01] SEED: Roles
INSERT INTO roles (name, description) VALUES ('ADMIN', N'Quản trị hệ thống cao cấp, phân quyền và giám sát an toàn');
INSERT INTO roles (name, description) VALUES ('MANAGER', N'Quản lý bệnh viện, tổ chức nhân sự, điều phối phân lịch trực');
INSERT INTO roles (name, description) VALUES ('DOCTOR', N'Bác sĩ chuyên khoa, khám chữa bệnh và hoàn thiện bệnh án');
INSERT INTO roles (name, description) VALUES ('RECEPTIONIST', N'Nhân viên lễ tân, tiếp đón check-in và xử lý hóa đơn viện phí');
INSERT INTO roles (name, description) VALUES ('PATIENT', N'Bệnh nhân, khách hàng đăng ký trải nghiệm dịch vụ y tế');
GO

-- [02] SEED: 34 tỉnh/thành phố (Nghị quyết 202/2025/QH15)
INSERT INTO provinces (name, type) VALUES (N'Hà Nội', 'CITY');
INSERT INTO provinces (name, type) VALUES (N'Tuyên Quang', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lào Cai', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lai Châu', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Điện Biên', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Sơn La', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Cao Bằng', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lạng Sơn', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Quảng Ninh', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Thái Nguyên', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Phú Thọ', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Bắc Ninh', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Hải Phòng', 'CITY');
INSERT INTO provinces (name, type) VALUES (N'Hưng Yên', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Ninh Bình', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Thanh Hóa', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Nghệ An', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Hà Tĩnh', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Quảng Trị', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Huế', 'CITY');
INSERT INTO provinces (name, type) VALUES (N'Đà Nẵng', 'CITY');
INSERT INTO provinces (name, type) VALUES (N'Quảng Ngãi', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Gia Lai', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Khánh Hòa', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Đắk Lắk', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Lâm Đồng', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Bình Định', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Tây Ninh', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Đồng Nai', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'TP. Hồ Chí Minh', 'CITY');
INSERT INTO provinces (name, type) VALUES (N'Cần Thơ', 'CITY');
INSERT INTO provinces (name, type) VALUES (N'Đồng Tháp', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'Vĩnh Long', 'PROVINCE');
INSERT INTO provinces (name, type) VALUES (N'An Giang', 'PROVINCE');
GO

-- [03] SEED: Departments
INSERT INTO departments (name, description, status) VALUES (N'Nội tổng quát', N'Khám và điều trị các bệnh nội khoa thông thường', 'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Tim mạch', N'Chẩn đoán và điều trị các bệnh lý tim mạch', 'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Nhi khoa', N'Khám và điều trị bệnh cho trẻ em từ 0-15 tuổi', 'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Da liễu', N'Chẩn đoán và điều trị các bệnh về da, tóc, móng', 'ACTIVE');
INSERT INTO departments (name, description, status) VALUES (N'Chẩn đoán hình ảnh', N'Siêu âm, X-quang, CT Scan, MRI', 'ACTIVE');
GO

-- [04] SEED: Rooms
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 1', 'P101', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (1, N'Phòng khám Nội 2', 'P102', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng khám Tim mạch', 'P201', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (2, N'Phòng đo điện tim', 'P202', 'MAINTENANCE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (3, N'Phòng khám Nhi', 'P301', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (4, N'Phòng khám Da liễu', 'P401', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng siêu âm', 'P501', 'ACTIVE');
INSERT INTO rooms (department_id, name, room_number, status) VALUES (5, N'Phòng X-quang', 'P502', 'ACTIVE');
GO

-- [05] SEED: Medical Services
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Khám nội tổng quát', 200000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (1, N'Tư vấn dinh dưỡng', 150000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Khám tim mạch', 350000, 45, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (2, N'Đo điện tim (ECG)', 100000, 15, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Khám nhi tổng quát', 180000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (3, N'Tư vấn tiêm chủng', 100000, 20, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (4, N'Khám da liễu', 250000, 30, 'ACTIVE');
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, status) VALUES (5, N'Siêu âm ổ bụng', 250000, 30, 'ACTIVE');
GO

-- [06] DETAILED MASSIVE SEED: Users
-- --- Nhóm ADMIN & MANAGER ---
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin', 'admin@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Quản', N'Trị', N'Admin', '0900000001', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin02', 'admin02@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Minh', N'Hoàng', N'Vũ', '0900000009', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager01', 'manager01@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hùng', N'Văn', N'Trần', '0900000002', 'MALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager02', 'manager02@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Thảo', N'Phương', N'Lê', '0900000003', 'FEMALE', 1);

-- --- Nhóm DOCTOR ---
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

-- --- Nhóm RECEPTIONIST ---
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept.linh', 'recept.linh@hams.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Linh', N'Thị', N'Phạm', '0904444444', 'FEMALE', 3);

-- --- Nhóm PATIENT ---
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.thanh', 'nguyenminhthanh@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Thành', N'Minh', N'Nguyễn', '0905555551', 'MALE', '1990-05-15', 'O+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.huong', 'phamthihuong@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Hương', N'Thị', N'Phạm', '0905555552', 'FEMALE', '1995-08-22', 'A+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.duc', 'levandduc@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Đức', N'Văn', N'Lê', '0905555553', 'MALE', '1985-03-10', 'B+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.lan', 'dothiblan@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 0, N'Lan', N'Thị B', N'Đỗ', '0905555554', 'FEMALE', '2000-11-30', 'AB+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.nam', 'hoangnham@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Nam', N'Hoài', N'Hoàng', '0905555555', 'MALE', '1993-01-25', 'O-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.vy', 'nguyenthanhvy@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Vy', N'Thanh', N'Nguyễn', '0905555556', 'FEMALE', '1997-07-14', 'A-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.long', 'tranbaolong@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Long', N'Bảo', N'Trần', '0905555557', 'MALE', '1978-12-05', 'B-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.mai', 'phuongmai@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Mai', N'Phương', N'Vũ', '0905555558', 'FEMALE', '2004-04-19', 'O+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.binh', 'tranquocbinh@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'Bình', N'Quốc', N'Trần', '0905555559', 'MALE', '1965-09-30', 'AB-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.an', 'haquynhan@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ACTIVE', 1, N'An', N'Quỳnh', N'Hà', '0905555560', 'FEMALE', '2015-05-12', 'A+');
GO

-- [07] SEED: Ánh xạ bảng trung gian quyền user_roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);  -- admin -> ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);  -- admin02 -> ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (3, 2);  -- manager01 -> MANAGER
INSERT INTO user_roles (user_id, role_id) VALUES (4, 2);  -- manager02 -> MANAGER
INSERT INTO user_roles (user_id, role_id) VALUES (5, 3);  -- dr.nguyenan -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (6, 3);  -- dr.lequang -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (6, 2);  -- dr.lequang kiêm nhiệm MANAGER
INSERT INTO user_roles (user_id, role_id) VALUES (7, 3);  -- dr.tranmai -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (8, 3);  -- dr.phambinh -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (9, 3);  -- dr.leanh -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (10, 3); -- dr.danghuong -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (11, 3); -- dr.hoanglinh -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (12, 3); -- dr.vutung -> DOCTOR
INSERT INTO user_roles (user_id, role_id) VALUES (13, 4); -- recept.linh -> RECEPTIONIST
INSERT INTO user_roles (user_id, role_id) VALUES (14, 5); -- patient.thanh -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (15, 5); -- patient.huong -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (16, 5); -- patient.duc -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (17, 5); -- patient.lan -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (18, 5); -- patient.nam -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (19, 5); -- patient.vy -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (20, 5); -- patient.long -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (21, 5); -- patient.mai -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (22, 5); -- patient.binh -> PATIENT
INSERT INTO user_roles (user_id, role_id) VALUES (23, 5); -- patient.an -> PATIENT
GO

-- [08] SEED: User Addresses
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (14, 30, N'123 Nguyễn Huệ, Phường Bến Nghé, Quận 1', 1);
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (15, 1, N'45 Trần Duy Hưng, Phường Trung Hòa, Cầu Giấy', 1);
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (16, 21, N'88 Lê Duẩn, Phường Thạch Thang, Hải Châu', 1);
INSERT INTO user_addresses (user_id, province_id, address_line, is_default) VALUES (17, 30, N'12 Cộng Hòa, Phường 4, Tân Bình', 1);
GO

-- [09] SEED: Week Schedules [ĐÃ ĐƯỢC THÊM VÀO ĐỂ FIX LỖI]
INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by) 
VALUES ('2026-06-22', '2026-06-28', 'PUBLISHED', 3);
GO

-- [10] SEED: Doctor Schedules [ĐÃ THÊM week_schedule_id = 1]
INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by) 
VALUES (1, 5, 1, '2026-06-22', 'MORNING', 'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by) 
VALUES (1, 5, 1, '2026-06-23', 'AFTERNOON', 'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by) 
VALUES (1, 5, 3, '2026-06-24', 'FULL_DAY', 'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by) 
VALUES (1, 5, 3, '2026-06-25', 'MORNING', 'ACTIVE', 3);

INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by) 
VALUES (1, 5, 3, '2026-06-26', 'FULL_DAY', 'ACTIVE', 3);

GO

-- [11] SEED: Time Slots
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '08:00', '08:30', 2, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '08:30', '09:00', 5, 5, 'FULL');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '09:00', '09:30', 0, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '09:30', '10:00', 1, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (1, '10:00', '10:30', 0, 5, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (3, '13:00', '13:45', 1, 3, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (3, '13:45', '14:30', 3, 3, 'FULL');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (3, '14:30', '15:15', 0, 3, 'AVAILABLE');
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status) VALUES (3, '15:15', '16:00', 0, 3, 'AVAILABLE');
GO

-- [12] SEED: Appointments
-- ============================================================
-- BỔ SUNG 50 APPOINTMENTS (TỪ -7 NGÀY ĐẾN +7 NGÀY)
-- Mapped chính xác theo Patient ID (14-23) và Doctor ID (5-12)
-- ============================================================

-- --- 7 NGÀY TRƯỚC (-7) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-7,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');


-- --- 6 NGÀY TRƯỚC (-6) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-6,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 5 NGÀY TRƯỚC (-5) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-5,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 4 NGÀY TRƯỚC (-4) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-4,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 3 NGÀY TRƯỚC (-3) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-3,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 2 NGÀY TRƯỚC (-2) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-2,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 1 NGÀY TRƯỚC (-1) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,-1,CAST(GETDATE() AS DATE)) AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- NGÀY HIỆN TẠI (0) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(GETDATE() AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(GETDATE() AS DATE), 'WAITING', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(GETDATE() AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(GETDATE() AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(GETDATE() AS DATE), 'EXAMINING', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(GETDATE() AS DATE), 'COMPLETED', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(GETDATE() AS DATE), 'COMPLETED', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(GETDATE() AS DATE), 'COMPLETED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(GETDATE() AS DATE), 'COMPLETED', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(GETDATE() AS DATE), 'COMPLETED', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 1 NGÀY SAU (+1) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,1,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 2 NGÀY SAU (+2) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,2,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 3 NGÀY SAU (+3) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,3,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 4 NGÀY SAU (+4) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,4,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 5 NGÀY SAU (+5) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,5,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 6 NGÀY SAU (+6) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,6,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');

-- --- 7 NGÀY SAU (+7) ---
INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, status, note) VALUES 
('APT-20260005', 14, 5, 1, 1, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám sức khỏe tổng quát định kỳ'),
('APT-20260006', 15, 5, 3, 6, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Bệnh nhân vắng mặt không đến'),
('APT-20260007', 16, 5, 5, 2, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Khám nhi ho sốt kèm phát ban'),
('APT-20260008', 17, 5, 7, 3, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra tình trạng kích ứng da'),
('APT-20260009', 18, 5, 2, 4, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'CANCELLED', N'Khách hàng gọi điện xin hủy lịch'),
('APT-20260010', 19, 5, 4, 7, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Đo điện tim kiểm tra nhịp đều'),
('APT-20260011', 20, 5, 6, 5, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Tư vấn tiêm chủng mũi nhắc lại'),
('APT-20260012', 21, 5, 7, 8, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Bệnh nhân vắng mặt không đến'),
('APT-20260013', 22, 5, 1, 1, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Triệu chứng đau đầu chóng mặt kéo dài'),
('APT-20260014', 23, 5, 3, 6, CAST(DATEADD(day,7,CAST(GETDATE() AS DATE)) AS DATE), 'WAITING', N'Kiểm tra dấu hiệu suy tim giai đoạn đầu');
GO

-- [13] SEED: Medical Records
INSERT INTO medical_records (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion, prescription_text, heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
VALUES (1, 14, 5, N'Đau thượng vị, buồn nôn, ợ chua liên tục.', N'Viêm loét dạ dày tá tràng (K29.5).', N'Uống thuốc đúng giờ, kiêng cay nóng.', N'1. Omeprazole 20mg x 2 lần/ngày\n2. Amoxicillin 1g x 2 lần/ngày', 76, '120/80', 5.10, 64.00, 'FINALIZED', 5);
GO

-- [14] SEED: Invoices
INSERT INTO invoices (invoice_code, medical_record_id, total_amount, payment_method, payment_status, paid_at) VALUES ('INV-20260001', 1, 300000, 'CASH', 'PAID', GETDATE());
INSERT INTO invoices (invoice_code, medical_record_id, total_amount, payment_method, payment_status, paid_at) VALUES ('INV-20260002', 2, 450000, 'BANK_TRANSFER', 'UNPAID', NULL);
GO

-- [15] SEED: Invoice Items
INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total) VALUES (1, 1, N'Khám nội tổng quát', 200000, 1, 200000);
INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total) VALUES (1, 8, N'Siêu âm ổ bụng', 100000, 1, 100000);
INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total) VALUES (2, 3, N'Khám tim mạch', 350000, 1, 350000);
INSERT INTO invoice_items (invoice_id, service_id, item_name, price_applied, quantity, line_total) VALUES (2, 4, N'Đo điện tim (ECG)', 100000, 1, 100000);
GO

-- [16] SEED: Articles & News
INSERT INTO articles (title, slug, summary, content, category, doctor_author_id, created_by, status, published_at) VALUES (N'Làm thế nào để bảo vệ hệ tim mạch khỏe mạnh?', 'bao-ve-tim-mach-khoe-manh', N'Lời khuyên bổ ích từ các chuyên gia tim mạch hàng đầu.', N'Tập thể dục 30 phút mỗi ngày, hạn chế ăn thức ăn nhanh và các chất kích thích.', N'Tim mạch', 7, 7, 'PUBLISHED', GETDATE());
INSERT INTO news (title, slug, content, type, status, created_by, published_at) VALUES (N'HAMS cập nhật hệ thống đặt lịch tự động phiên bản mới', 'cap-nhat-he-thong-dat-lich-moi', N'Phiên bản mới nâng cao tốc độ tải và chống trùng lịch khám.', 'NEWS', 'PUBLISHED', 3, GETDATE());
GO

-- [17] SEED: Notifications
INSERT INTO notifications (user_id, title, message, notification_type, is_read, related_entity_id, related_entity_type) 
VALUES (14, N'Khám bệnh hoàn tất', N'Hồ sơ bệnh án ca khám APT-20260001 đã được tạo thành công.', 'MEDICAL_RECORD_CREATED', 1, 1, 'MEDICAL_RECORD');

INSERT INTO notifications (user_id, title, message, notification_type, is_read, related_entity_id, related_entity_type) 
VALUES (15, N'Lịch khám đã được xác nhận', N'Ca khám của bạn với BS. Trần Thị Mai đã được phân phòng.', 'APPOINTMENT_CONFIRMED', 0, 2, 'APPOINTMENT');
GO

-- ============================================================
-- QUICK VERIFY — Kiểm tra tổng quan dữ liệu toàn hệ thống (19 bảng)
-- ============================================================
SELECT [table], [rows] FROM (
    SELECT 'roles'                AS [table], COUNT(*) AS [rows] FROM roles
    UNION ALL SELECT 'users'                  , COUNT(*) FROM users
    UNION ALL SELECT 'user_roles'             , COUNT(*) FROM user_roles
    UNION ALL SELECT 'user_addresses'         , COUNT(*) FROM user_addresses
    UNION ALL SELECT 'departments'            , COUNT(*) FROM departments
    UNION ALL SELECT 'rooms'                  , COUNT(*) FROM rooms
    UNION ALL SELECT 'provinces'              , COUNT(*) FROM provinces
    UNION ALL SELECT 'medical_services'       , COUNT(*) FROM medical_services
    UNION ALL SELECT 'week_schedules'         , COUNT(*) FROM week_schedules
    UNION ALL SELECT 'doctor_schedules'       , COUNT(*) FROM doctor_schedules
    UNION ALL SELECT 'time_slots'             , COUNT(*) FROM time_slots
    UNION ALL SELECT 'appointments'           , COUNT(*) FROM appointments
    UNION ALL SELECT 'medical_records'        , COUNT(*) FROM medical_records
    UNION ALL SELECT 'invoices'               , COUNT(*) FROM invoices
    UNION ALL SELECT 'invoice_items'          , COUNT(*) FROM invoice_items
    UNION ALL SELECT 'articles'               , COUNT(*) FROM articles
    UNION ALL SELECT 'news'                   , COUNT(*) FROM news
    UNION ALL SELECT 'email_logs'             , COUNT(*) FROM email_logs
    UNION ALL SELECT 'notifications'          , COUNT(*) FROM notifications
) t ORDER BY [table];
GO