-- ============================================================

GO
-- [11] Time Slots (tu dong sinh slot theo dung logic trong ScheduleServiceImpl.java)
-- Ca sang (MORNING, max_capacity = 4 trong code): 07:00-08:00 (cap 1), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2)
-- Ca chieu (AFTERNOON, max_capacity = 4 trong code): 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
-- Ca ca ngay (FULL_DAY, max_capacity = 10 trong code): 07:00-08:00 (cap 2), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2), 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status)
SELECT ds.id, t.start_time, t.end_time, 0, t.max_capacity, 'AVAILABLE'
FROM doctor_schedules ds
CROSS JOIN (
    -- MORNING SHIFT (max_capacity = 4 trong Java: floor(4/3)=1, ceil(4/3)=2, ceil(4/3)=2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 1 AS max_capacity, 'MORNING' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'MORNING' UNION ALL
    SELECT '10:00', '11:00', 2, 'MORNING' UNION ALL
    -- AFTERNOON SHIFT (max_capacity = 4 trong Java: ceil(4/2)=2, ceil(4/2)=2)
    SELECT '13:00' AS start_time, '14:00' AS end_time, 2 AS max_capacity, 'AFTERNOON' AS shift UNION ALL
    SELECT '14:00', '15:00', 2, 'AFTERNOON' UNION ALL
    -- FULL_DAY SHIFT (max_capacity = 10 trong Java: 5 slots moi slot 2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 2 AS max_capacity, 'FULL_DAY' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'FULL_DAY' UNION ALL
    SELECT '10:00', '11:00', 2, 'FULL_DAY' UNION ALL
    SELECT '13:00', '14:00', 2, 'FULL_DAY' UNION ALL
    SELECT '14:00', '15:00', 2, 'FULL_DAY'
) t
WHERE ds.shift = t.shift;
GO
-- ============================================================

GO
-- [11] Time Slots (tu dong sinh slot theo dung logic trong ScheduleServiceImpl.java)
GO

-- ============================================================

GO
-- [11] Time Slots (tu dong sinh slot theo dung logic trong ScheduleServiceImpl.java)
-- Ca sang (MORNING, max_capacity = 4 trong code): 07:00-08:00 (cap 1), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2)
-- [11] Time Slots (tu dong sinh slot theo dung logic trong ScheduleServiceImpl.java)
-- Ca sang (MORNING, max_capacity = 4 trong code): 07:00-08:00 (cap 1), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2)
-- Ca chieu (AFTERNOON, max_capacity = 4 trong code): 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
-- Ca ca ngay (FULL_DAY, max_capacity = 10 trong code): 07:00-08:00 (cap 2), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2), 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status)
SELECT ds.id, t.start_time, t.end_time, 0, t.max_capacity, 'AVAILABLE'
FROM doctor_schedules ds
CROSS JOIN (
    -- MORNING SHIFT (max_capacity = 4 trong Java: floor(4/3)=1, ceil(4/3)=2, ceil(4/3)=2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 1 AS max_capacity, 'MORNING' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'MORNING' UNION ALL
    SELECT '10:00', '11:00', 2, 'MORNING' UNION ALL
    -- AFTERNOON SHIFT (max_capacity = 4 trong Java: ceil(4/2)=2, ceil(4/2)=2)
    SELECT '13:00' AS start_time, '14:00' AS end_time, 2 AS max_capacity, 'AFTERNOON' AS shift UNION ALL
    SELECT '14:00', '15:00', 2, 'AFTERNOON' UNION ALL
    -- FULL_DAY SHIFT (max_capacity = 10 trong Java: 5 slots moi slot 2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 2 AS max_capacity, 'FULL_DAY' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'FULL_DAY' UNION ALL
    SELECT '10:00', '11:00', 2, 'FULL_DAY' UNION ALL
    SELECT '13:00', '14:00', 2, 'FULL_DAY' UNION ALL
    SELECT '14:00', '15:00', 2, 'FULL_DAY'
) t
WHERE ds.shift = t.shift;
GO
-- ============================================================

GO
-- [11] Time Slots (tu dong sinh slot theo dung logic trong ScheduleServiceImpl.java)
-- Ca sang (MORNING, max_capacity = 4 trong code): 07:00-08:00 (cap 1), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2)
-- Ca chieu (AFTERNOON, max_capacity = 4 trong code): 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
-- Ca ca ngay (FULL_DAY, max_capacity = 10 trong code): 07:00-08:00 (cap 2), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2), 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status)
SELECT ds.id, t.start_time, t.end_time, 0, t.max_capacity, 'AVAILABLE'
FROM doctor_schedules ds
CROSS JOIN (
    -- MORNING SHIFT (max_capacity = 4 trong Java: floor(4/3)=1, ceil(4/3)=2, ceil(4/3)=2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 1 AS max_capacity, 'MORNING' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'MORNING' UNION ALL
    SELECT '10:00', '11:00', 2, 'MORNING' UNION ALL
    -- AFTERNOON SHIFT (max_capacity = 4 trong Java: ceil(4/2)=2, ceil(4/2)=2)
    SELECT '13:00' AS start_time, '14:00' AS end_time, 2 AS max_capacity, 'AFTERNOON' AS shift UNION ALL
    SELECT '14:00', '15:00', 2, 'AFTERNOON' UNION ALL
    -- FULL_DAY SHIFT (max_capacity = 10 trong Java: 5 slots moi slot 2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 2 AS max_capacity, 'FULL_DAY' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'FULL_DAY' UNION ALL
    SELECT '10:00', '11:00', 2, 'FULL_DAY' UNION ALL
    SELECT '13:00', '14:00', 2, 'FULL_DAY' UNION ALL
    SELECT '14:00', '15:00', 2, 'FULL_DAY'
) t
WHERE ds.shift = t.shift;
GO
-- ============================================================
-- HAMS SEED DATA v10
-- Compatible with schema v9
-- ============================================================
-- Thay doi so voi v9:
--   [+] 3 phong kham lam sang / khoa (Toa A): P{K}01-A, P{K}02-A, P{K}03-A
--   [+] 7 phong dich vu / khoa   (Toa B): P{K}04-B -> P{K}10-B
--   [+] Medical services: 8 dich vu / khoa (bo 2 dich vu tu van trung lap)
--   [+] Doctor schedules: 6 bac si / khoa / ngay, phan bo conflict-free
--       (Rank 1-3 -> MORNING, Rank 4-6 -> AFTERNOON, vao 3 phong A)
--   [+] Appointments: chi seed den 25/07/2026 (du lieu lich su sach)
--   [+] Giu nguyen image_url departments tu v9 (Cloudinary)
-- Password mac dinh: 123456
-- BCrypt: $2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK
-- ============================================================

USE hams_db;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- ============================================================
-- 1. DON DEP DU LIEU CU (dung thu tu rang buoc khoa ngoai)
-- ============================================================
DELETE FROM notifications;
DELETE FROM email_logs;
DELETE FROM article_comments;
DELETE FROM articles;
DELETE FROM invoice_items;
DELETE FROM invoices;
DELETE FROM medical_service_orders;
DELETE FROM medical_records;
DELETE FROM appointments;
DELETE FROM time_slots;
DELETE FROM doctor_schedules;
DELETE FROM week_schedules;
DELETE FROM medical_services;
DELETE FROM user_addresses;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM rooms;
DELETE FROM departments;
DELETE FROM provinces;
DELETE FROM roles;
GO

-- ============================================================
-- 2. RESET IDENTITY COUNTERS
-- ============================================================
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('roles') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('roles', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('provinces') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('provinces', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('departments') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('departments', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('rooms') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('rooms', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('users') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('users', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('user_addresses') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('user_addresses', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('medical_services') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('medical_services', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('week_schedules') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('week_schedules', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('doctor_schedules') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('doctor_schedules', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('time_slots') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('time_slots', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('appointments') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('appointments', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('medical_records') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('medical_records', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('medical_service_orders') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('medical_service_orders', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('invoices') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('invoices', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('invoice_items') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('invoice_items', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('articles') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('articles', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('article_comments') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('article_comments', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('email_logs') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('email_logs', RESEED, 0);
IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('notifications') AND last_value IS NOT NULL)
    DBCC CHECKIDENT ('notifications', RESEED, 0);
GO

-- ============================================================
-- 3. CHEN DU LIEU MAU
-- ============================================================

-- [01] Roles
INSERT INTO roles (name, description) VALUES ('ADMIN',        N'Quản trị hệ thống');
INSERT INTO roles (name, description) VALUES ('MANAGER',      N'Quản lý bệnh viện');
INSERT INTO roles (name, description) VALUES ('DOCTOR',       N'Bác sĩ');
INSERT INTO roles (name, description) VALUES ('RECEPTIONIST', N'Lễ tân');
INSERT INTO roles (name, description) VALUES ('PATIENT',      N'Bệnh nhân');
GO

-- [02] Provinces (34 tinh/thanh - Nghi quyet 202/2025/QH15)
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

-- [03] Departments (7 khoa - giu nguyen image_url tu v9)
INSERT INTO departments (name, description, image_url, status) VALUES
(N'Tim Mạch',      N'Chẩn đoán và điều trị các bệnh lý về tim mạch, huyết áp, mạch máu',       'https://res.cloudinary.com/doj5derxh/image/upload/v1784568150/vi%E1%BB%87n_tim_m%E1%BA%A1ch_pkjjze.jpg', 'ACTIVE'),
(N'Thần Kinh',     N'Chẩn đoán và điều trị các bệnh lý về giữ thăng bằng và hệ thần kinh',     'https://res.cloudinary.com/doj5derxh/image/upload/v1784568288/a1_hxhh4w.jpg', 'ACTIVE'),
(N'Cơ Xương Khớp', N'Chẩn đoán và điều trị các bệnh lý về xương, khớp, cơ và mô liên kết',    'https://res.cloudinary.com/doj5derxh/image/upload/v1784568341/2_pfbxqg.png', 'ACTIVE'),
(N'Nội Tiêu Hóa',  N'Chẩn đoán và điều trị các bệnh lý về dạ dày, ruột, gan mật và tụy',      'https://res.cloudinary.com/doj5derxh/image/upload/v1784568423/kham-tieu-hoa-la-kham-nhung-gi_fqzygw.jpg', 'ACTIVE'),
(N'Nhi',           N'Khám và điều trị bệnh cho trẻ em từ sơ sinh đến 15 tuổi',                 'https://res.cloudinary.com/doj5derxh/image/upload/v1784568458/nhung-dieu-can-biet-ve-khoa-nhi-benh-vien-quan-dan-y-mien-dong-1_wjsumr.webp', 'ACTIVE'),
(N'Mắt',           N'Khám, điều trị và phẫu thuật các bệnh lý về mắt',                         'https://res.cloudinary.com/doj5derxh/image/upload/v1784568491/Kham_mat_qu4rke.jpg', 'ACTIVE'),
(N'Tai Mũi Họng',  N'Khám và điều trị các bệnh lý tai, mũi, họng người lớn và trẻ em',         'https://res.cloudinary.com/doj5derxh/image/upload/v1784568540/VHC._Khoa_tai_mui_hong_hluhum.jpg', 'ACTIVE');
GO

-- [04] Medical Services (8 dich vu x 7 khoa = 56 dich vu)
-- Dich vu dau tien cua moi khoa = khám lam sang tong quat (mac dinh cho appointment)
-- Cac dich vu 2-8 = dich vu ky thuat co phong rieng (Toa B)

-- === KHOA 1: TIM MACH (IDs 1-8) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(1, N'Khám tim mạch tổng quát',          300000, 30, N'Thăm khám lâm sàng tim mạch toàn diện', 'ACTIVE'),
(1, N'Đo điện tâm đồ (ECG)',             150000, 15, N'Ghi lại hoạt động điện của tim',         'ACTIVE'),
(1, N'Siêu âm tim',                      450000, 45, N'Đánh giá cấu trúc và chức năng tim',     'ACTIVE'),
(1, N'Đo huyết áp 24 giờ (Holter HA)',   600000, 20, N'Theo dõi huyết áp liên tục 24 giờ',     'ACTIVE'),
(1, N'Holter ECG 24 giờ',               700000, 20, N'Ghi điện tâm đồ liên tục 24 giờ',       'ACTIVE'),
(1, N'Xét nghiệm mỡ máu (Lipid profile)',250000, 15, N'Định lượng Cholesterol, Triglyceride',   'ACTIVE'),
(1, N'Xét nghiệm Troponin',              350000, 15, N'Chẩn đoán nhồi máu cơ tim cấp',         'ACTIVE'),
(1, N'Chụp X-quang tim phổi',            180000, 15, N'Đánh giá kích thước tim và tình trạng phổi', 'ACTIVE');

-- === KHOA 2: THAN KINH (IDs 9-16) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(2, N'Khám thần kinh tổng quát',         300000, 30, N'Thăm khám lâm sàng hệ thần kinh toàn diện',   'ACTIVE'),
(2, N'Điện não đồ (EEG)',               400000, 45, N'Ghi lại hoạt động điện não, chẩn đoán động kinh','ACTIVE'),
(2, N'Điện cơ đồ (EMG)',               500000, 45, N'Đánh giá chức năng dây thần kinh và cơ',        'ACTIVE'),
(2, N'Siêu âm Doppler mạch não',        450000, 30, N'Đánh giá lưu lượng máu lên não',               'ACTIVE'),
(2, N'Xét nghiệm dịch não tủy',         800000, 60, N'Chọc dịch não tủy chẩn đoán viêm màng não',    'ACTIVE'),
(2, N'Đánh giá chức năng nhận thức',    350000, 40, N'Sàng lọc sa sút trí tuệ, Alzheimer',           'ACTIVE'),
(2, N'Tư vấn phục hồi sau đột quỵ',    300000, 30, N'Kế hoạch phục hồi chức năng vận động',         'ACTIVE'),
(2, N'Chụp MRI não',                   1500000, 60, N'Cộng hưởng từ não chẩn đoán u não, xuất huyết','ACTIVE');

-- === KHOA 3: CO XUONG KHOP (IDs 17-24) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(3, N'Khám cơ xương khớp tổng quát',    300000, 30, N'Thăm khám lâm sàng hệ cơ xương khớp',         'ACTIVE'),
(3, N'Chụp X-quang xương khớp',         200000, 15, N'Đánh giá cấu trúc xương và khoang khớp',       'ACTIVE'),
(3, N'Siêu âm khớp',                    350000, 30, N'Siêu âm gân cơ dây chằng và màng khớp',        'ACTIVE'),
(3, N'Đo mật độ xương (DEXA)',           500000, 30, N'Chẩn đoán loãng xương, nguy cơ gãy xương',     'ACTIVE'),
(3, N'Xét nghiệm Acid Uric (Gout)',      150000, 10, N'Định lượng Acid Uric trong máu',               'ACTIVE'),
(3, N'Xét nghiệm yếu tố dạng thấp (RF)',200000, 10, N'Hỗ trợ chẩn đoán viêm khớp dạng thấp',        'ACTIVE'),
(3, N'Tiêm nội khớp',                   600000, 20, N'Tiêm thuốc chống viêm Corticoid vào khớp',     'ACTIVE'),
(3, N'Vật lý trị liệu cơ bản',          250000, 45, N'Siêu âm trị liệu, điện phân, kéo giãn',        'ACTIVE');

-- === KHOA 4: NOI TIEU HOA (IDs 25-32) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(4, N'Khám tiêu hóa tổng quát',         300000, 30, N'Thăm khám lâm sàng hệ tiêu hóa toàn diện',    'ACTIVE'),
(4, N'Nội soi dạ dày',                  700000, 30, N'Chẩn đoán viêm loét dạ dày - thực quản',       'ACTIVE'),
(4, N'Nội soi đại tràng',               900000, 45, N'Phát hiện polyp và viêm đại tràng',             'ACTIVE'),
(4, N'Siêu âm ổ bụng',                  300000, 30, N'Siêu âm gan mật tụy lách thận',                'ACTIVE'),
(4, N'Xét nghiệm H.Pylori',             250000, 10, N'Phát hiện vi khuẩn Hp gây loét dạ dày',         'ACTIVE'),
(4, N'Xét nghiệm chức năng gan (AST, ALT)',200000,10, N'Đánh giá tổn thương tế bào gan',             'ACTIVE'),
(4, N'Xét nghiệm viêm gan B và C',      350000, 10, N'Sàng lọc HBsAg và Anti-HCV',                   'ACTIVE'),
(4, N'Sinh thiết niêm mạc dạ dày',      500000, 20, N'Xét nghiệm giải phẫu bệnh lý niêm mạc',        'ACTIVE');

-- === KHOA 5: NHI (IDs 33-40) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(5, N'Khám nhi tổng quát',              250000, 30, N'Đánh giá tăng trưởng và phát triển của trẻ',   'ACTIVE'),
(5, N'Tiêm phòng vaccine',              200000, 15, N'Tiêm vaccine dịch vụ cho trẻ em',              'ACTIVE'),
(5, N'Xét nghiệm máu tổng quát (CBC)', 180000, 10, N'Phân tích công thức máu toàn phần trẻ em',     'ACTIVE'),
(5, N'Siêu âm bụng trẻ em',            280000, 25, N'Siêu âm ổ bụng chẩn đoán bệnh tiêu hóa trẻ',  'ACTIVE'),
(5, N'Đánh giá phát triển tâm thần vận động',300000,40, N'Đánh giá mốc phát triển ngôn ngữ, nhận thức','ACTIVE'),
(5, N'Khám sơ sinh và tư vấn cho mẹ',  250000, 30, N'Khám sơ sinh toàn diện và tư vấn chăm sóc',   'ACTIVE'),
(5, N'Xét nghiệm tầm soát dị ứng trẻ em',400000,15, N'Phát hiện các dị nguyên gây dị ứng',         'ACTIVE'),
(5, N'Tư vấn điều trị hen và viêm phế quản',220000,25, N'Chẩn đoán và điều trị hen suyễn trẻ em',  'ACTIVE');

-- === KHOA 6: MAT (IDs 41-48) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(6, N'Khám mắt tổng quát',             150000, 20, N'Khám tầm soát các bệnh lý về mắt',             'ACTIVE'),
(6, N'Đo khúc xạ máy tự động',          80000, 10, N'Đo độ cận, viễn, loạn thị tự động',            'ACTIVE'),
(6, N'Thử thị lực và thử kính',        100000, 15, N'Cắt kính cận, viễn, loạn thị phù hợp',         'ACTIVE'),
(6, N'Khám sinh hiển vi điện tử',      120000, 15, N'Kiểm tra bán phần trước nhãn cầu',              'ACTIVE'),
(6, N'Đo nhãn áp',                      90000, 10, N'Tầm soát bệnh thiên đầu thống (Glocom)',        'ACTIVE'),
(6, N'Soi đáy mắt trực tiếp',          150000, 20, N'Đánh giá võng mạc, dịch kính và gai thị',      'ACTIVE'),
(6, N'Chụp ảnh màu võng mạc',          250000, 25, N'Ghi hình đáy mắt tầm soát bệnh lý võng mạc',   'ACTIVE'),
(6, N'Siêu âm mắt (A/B)',              200000, 20, N'Siêu âm nhãn cầu và hốc mắt',                  'ACTIVE');

-- === KHOA 7: TAI MUI HONG (IDs 49-56) ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(7, N'Khám tai mũi họng thông thường',  150000, 15, N'Thăm khám lâm sàng tai mũi họng',             'ACTIVE'),
(7, N'Nội soi tai mũi họng ống cứng',   250000, 20, N'Nội soi chẩn đoán viêm xoang, viêm tai',      'ACTIVE'),
(7, N'Nội soi tai mũi họng ống mềm',    400000, 30, N'Nội soi tầm soát u vùng họng thanh quản',     'ACTIVE'),
(7, N'Đo thính lực đơn âm',             180000, 25, N'Đánh giá mức độ nghe kém, phân loại điếc',    'ACTIVE'),
(7, N'Đo nhĩ lượng',                    120000, 15, N'Đánh giá độ di động màng nhĩ và tai giữa',    'ACTIVE'),
(7, N'Làm thuốc tai/mũi/họng',           90000, 15, N'Rửa, bôi thuốc tại chỗ vùng tai mũi họng',   'ACTIVE'),
(7, N'Xông họng bằng máy siêu âm',       70000, 15, N'Xông khí dung giảm viêm họng, viêm amidan',  'ACTIVE'),
(7, N'Trích rạch áp xe amiđan',          500000, 30, N'Thủ thuật dẫn lưu ổ mủ amiđan',              'ACTIVE');
GO

-- [05] Rooms (10 phong x 7 khoa = 70 phong)
-- Toa A (phong kham lam sang): P{K}01-A, P{K}02-A, P{K}03-A -> bac si ngoi kham
-- Toa B (phong dich vu ky thuat): P{K}04-B den P{K}10-B -> phong thu thuat/xet nghiem

-- === KHOA 1: TIM MACH === (Room IDs: 1-10)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(1, N'Phòng khám lâm sàng Tim Mạch 1',    'P101-A', 'ACTIVE'),
(1, N'Phòng khám lâm sàng Tim Mạch 2',    'P102-A', 'ACTIVE'),
(1, N'Phòng khám lâm sàng Tim Mạch 3',    'P103-A', 'ACTIVE'),
(1, N'Đo điện tâm đồ (ECG)',              'P104-B', 'ACTIVE'),
(1, N'Siêu âm tim',                       'P105-B', 'ACTIVE'),
(1, N'Đo huyết áp 24 giờ (Holter HA)',    'P106-B', 'ACTIVE'),
(1, N'Holter ECG 24 giờ',                'P107-B', 'ACTIVE'),
(1, N'Xét nghiệm mỡ máu (Lipid profile)', 'P108-B', 'ACTIVE'),
(1, N'Xét nghiệm Troponin',              'P109-B', 'ACTIVE'),
(1, N'Chụp X-quang tim phổi',            'P110-B', 'ACTIVE');

-- === KHOA 2: THAN KINH === (Room IDs: 11-20)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(2, N'Phòng khám lâm sàng Thần Kinh 1',   'P201-A', 'ACTIVE'),
(2, N'Phòng khám lâm sàng Thần Kinh 2',   'P202-A', 'ACTIVE'),
(2, N'Phòng khám lâm sàng Thần Kinh 3',   'P203-A', 'ACTIVE'),
(2, N'Điện não đồ (EEG)',                 'P204-B', 'ACTIVE'),
(2, N'Điện cơ đồ (EMG)',                 'P205-B', 'ACTIVE'),
(2, N'Siêu âm Doppler mạch não',          'P206-B', 'ACTIVE'),
(2, N'Xét nghiệm dịch não tủy',           'P207-B', 'ACTIVE'),
(2, N'Đánh giá chức năng nhận thức',      'P208-B', 'ACTIVE'),
(2, N'Tư vấn phục hồi sau đột quỵ',      'P209-B', 'ACTIVE'),
(2, N'Chụp MRI não',                     'P210-B', 'ACTIVE');

-- === KHOA 3: CO XUONG KHOP === (Room IDs: 21-30)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(3, N'Phòng khám lâm sàng Cơ Xương Khớp 1', 'P301-A', 'ACTIVE'),
(3, N'Phòng khám lâm sàng Cơ Xương Khớp 2', 'P302-A', 'ACTIVE'),
(3, N'Phòng khám lâm sàng Cơ Xương Khớp 3', 'P303-A', 'ACTIVE'),
(3, N'Chụp X-quang xương khớp',              'P304-B', 'ACTIVE'),
(3, N'Siêu âm khớp',                         'P305-B', 'ACTIVE'),
(3, N'Đo mật độ xương (DEXA)',               'P306-B', 'ACTIVE'),
(3, N'Xét nghiệm Acid Uric (Gout)',          'P307-B', 'ACTIVE'),
(3, N'Xét nghiệm yếu tố dạng thấp (RF)',    'P308-B', 'ACTIVE'),
(3, N'Tiêm nội khớp',                        'P309-B', 'ACTIVE'),
(3, N'Vật lý trị liệu cơ bản',               'P310-B', 'ACTIVE');

-- === KHOA 4: NOI TIEU HOA === (Room IDs: 31-40)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(4, N'Phòng khám lâm sàng Nội Tiêu Hóa 1',  'P401-A', 'ACTIVE'),
(4, N'Phòng khám lâm sàng Nội Tiêu Hóa 2',  'P402-A', 'ACTIVE'),
(4, N'Phòng khám lâm sàng Nội Tiêu Hóa 3',  'P403-A', 'ACTIVE'),
(4, N'Nội soi dạ dày',                       'P404-B', 'ACTIVE'),
(4, N'Nội soi đại tràng',                    'P405-B', 'ACTIVE'),
(4, N'Siêu âm ổ bụng',                       'P406-B', 'ACTIVE'),
(4, N'Xét nghiệm H.Pylori',                  'P407-B', 'ACTIVE'),
(4, N'Xét nghiệm chức năng gan (AST, ALT)',  'P408-B', 'ACTIVE'),
(4, N'Xét nghiệm viêm gan B và C',           'P409-B', 'ACTIVE'),
(4, N'Sinh thiết niêm mạc dạ dày',           'P410-B', 'ACTIVE');

-- === KHOA 5: NHI === (Room IDs: 41-50)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(5, N'Phòng khám lâm sàng Nhi 1',            'P501-A', 'ACTIVE'),
(5, N'Phòng khám lâm sàng Nhi 2',            'P502-A', 'ACTIVE'),
(5, N'Phòng khám lâm sàng Nhi 3',            'P503-A', 'ACTIVE'),
(5, N'Tiêm phòng vaccine',                   'P504-B', 'ACTIVE'),
(5, N'Xét nghiệm máu tổng quát (CBC)',       'P505-B', 'ACTIVE'),
(5, N'Siêu âm bụng trẻ em',                 'P506-B', 'ACTIVE'),
(5, N'Đánh giá phát triển tâm thần vận động','P507-B', 'ACTIVE'),
(5, N'Khám sơ sinh và tư vấn cho mẹ',       'P508-B', 'ACTIVE'),
(5, N'Xét nghiệm tầm soát dị ứng trẻ em',   'P509-B', 'ACTIVE'),
(5, N'Tư vấn điều trị hen và viêm phế quản','P510-B', 'ACTIVE');

-- === KHOA 6: MAT === (Room IDs: 51-60)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(6, N'Phòng khám lâm sàng Mắt 1',           'P601-A', 'ACTIVE'),
(6, N'Phòng khám lâm sàng Mắt 2',           'P602-A', 'ACTIVE'),
(6, N'Phòng khám lâm sàng Mắt 3',           'P603-A', 'ACTIVE'),
(6, N'Đo khúc xạ máy tự động',              'P604-B', 'ACTIVE'),
(6, N'Thử thị lực và thử kính',             'P605-B', 'ACTIVE'),
(6, N'Khám sinh hiển vi điện tử',           'P606-B', 'ACTIVE'),
(6, N'Đo nhãn áp',                          'P607-B', 'ACTIVE'),
(6, N'Soi đáy mắt trực tiếp',              'P608-B', 'ACTIVE'),
(6, N'Chụp ảnh màu võng mạc',              'P609-B', 'ACTIVE'),
(6, N'Siêu âm mắt (A/B)',                  'P610-B', 'ACTIVE');

-- === KHOA 7: TAI MUI HONG === (Room IDs: 61-70)
INSERT INTO rooms (department_id, name, room_number, status) VALUES
(7, N'Phòng khám lâm sàng Tai Mũi Họng 1',  'P701-A', 'ACTIVE'),
(7, N'Phòng khám lâm sàng Tai Mũi Họng 2',  'P702-A', 'ACTIVE'),
(7, N'Phòng khám lâm sàng Tai Mũi Họng 3',  'P703-A', 'ACTIVE'),
(7, N'Nội soi tai mũi họng ống cứng',        'P704-B', 'ACTIVE'),
(7, N'Nội soi tai mũi họng ống mềm',         'P705-B', 'ACTIVE'),
(7, N'Đo thính lực đơn âm',                  'P706-B', 'ACTIVE'),
(7, N'Đo nhĩ lượng',                         'P707-B', 'ACTIVE'),
(7, N'Làm thuốc tai/mũi/họng',               'P708-B', 'ACTIVE'),
(7, N'Xông họng bằng máy siêu âm',           'P709-B', 'ACTIVE'),
(7, N'Trích rạch áp xe amiđan',              'P710-B', 'ACTIVE');
GO

-- [06] Users - ADMIN & MANAGER
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin', 'admin@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Quản', N'Trị', N'Admin', '0900000001', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin02', 'admin02@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Minh', N'Hoàng', N'Vũ', '0900000009', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager01', 'manager01@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hùng', N'Văn', N'Trần', '0900000002', 'MALE', (SELECT id FROM users WHERE username = 'admin'));

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager02', 'manager02@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Thảo', N'Phương', N'Lê', '0900000003', 'FEMALE', (SELECT id FROM users WHERE username = 'admin'));
GO

-- [06b] Users - DOCTOR (7 khoa x 10 bac si = 70 bac si)
DECLARE @DeptId INT = 1;
DECLARE @DocIdx INT = 1;
DECLARE @FirstNames TABLE (idx INT, name NVARCHAR(50));
DECLARE @LastNames  TABLE (idx INT, name NVARCHAR(50));
DECLARE @MiddleNames TABLE (idx INT, name NVARCHAR(50));

INSERT INTO @FirstNames  VALUES (0,N'An'),(1,N'Bình'),(2,N'Cường'),(3,N'Dũng'),(4,N'Anh'),(5,N'Hương'),(6,N'Linh'),(7,N'Tùng'),(8,N'Tài'),(9,N'Thúy');
INSERT INTO @LastNames   VALUES (0,N'Nguyễn'),(1,N'Trần'),(2,N'Lê'),(3,N'Phạm'),(4,N'Hoàng'),(5,N'Vũ'),(6,N'Đỗ'),(7,N'Phan'),(8,N'Lý'),(9,N'Đặng');
INSERT INTO @MiddleNames VALUES (0,N'Văn'),(1,N'Thị'),(2,N'Minh'),(3,N'Quốc'),(4,N'Hoàng'),(5,N'Thu'),(6,N'Khánh'),(7,N'Thanh'),(8,N'Quang'),(9,N'Hồng');

WHILE @DeptId <= 7
BEGIN
    SET @DocIdx = 1;
    WHILE @DocIdx <= 10
    BEGIN
        DECLARE @Username VARCHAR(50) = 'dr.' +
            CASE @DeptId
                WHEN 1 THEN 'timmach'
                WHEN 2 THEN 'thankinh'
                WHEN 3 THEN 'coxuongkhop'
                WHEN 4 THEN 'noitieuhoa'
                WHEN 5 THEN 'nhi'
                WHEN 6 THEN 'mat'
                WHEN 7 THEN 'tmh'
            END + CAST(@DocIdx AS VARCHAR(2));
        DECLARE @Email      VARCHAR(100) = @Username + '@hams.vn';
        DECLARE @FName      NVARCHAR(50) = (SELECT name FROM @FirstNames  WHERE idx = (@DocIdx - 1));
        DECLARE @MName      NVARCHAR(50) = (SELECT name FROM @MiddleNames WHERE idx = (@DeptId + @DocIdx) % 10);
        DECLARE @LName      NVARCHAR(50) = (SELECT name FROM @LastNames   WHERE idx = (@DeptId - 1));
        DECLARE @LicenseNo  VARCHAR(50)  = 'LIC-BS-' + RIGHT('000' + CAST((@DeptId * 10 + @DocIdx) AS VARCHAR), 3);
        DECLARE @LicDate    DATE         = DATEADD(year, -(5 + ((@DeptId + @DocIdx) % 15)), '2026-06-13');

        INSERT INTO users (username, email, password_hash, status, email_verified,
                           first_name, middle_name, last_name, phone, gender,
                           department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
        VALUES (@Username, @Email,
                '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK',
                'ACTIVE', 1, @FName, @MName, @LName,
                '090' + CAST((1000000 + @DeptId * 100000 + @DocIdx * 1000) AS VARCHAR(15)),
                CASE WHEN @DocIdx % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
                @DeptId, @LicDate,
                CASE WHEN @DocIdx % 3 = 0 THEN N'Tiến sĩ Y khoa'
                     WHEN @DocIdx % 3 = 1 THEN N'Thạc sĩ Y khoa'
                     ELSE N'Bác sĩ chuyên khoa I' END,
                @LicenseNo, N'Bác sĩ chuyên khoa khám chữa bệnh tại khoa.', 'ACTIVE',
                (SELECT id FROM users WHERE username = 'admin'));

        SET @DocIdx = @DocIdx + 1;
    END
    SET @DeptId = @DeptId + 1;
END
GO

-- [06c] Users - RECEPTIONIST (10 le tan)
DECLARE @RecIdx INT = 1;
WHILE @RecIdx <= 10
BEGIN
    DECLARE @RecUsername VARCHAR(50)  = 'recept' + RIGHT('0' + CAST(@RecIdx AS VARCHAR), 2);
    DECLARE @RecEmail    VARCHAR(100) = @RecUsername + '@hams.vn';
    DECLARE @RecFName    NVARCHAR(50) = CASE @RecIdx
        WHEN 1 THEN N'Linh' WHEN 2 THEN N'Mai'  WHEN 3 THEN N'Tuấn' WHEN 4 THEN N'Hương' WHEN 5 THEN N'Lan'
        WHEN 6 THEN N'Nam'  WHEN 7 THEN N'Dũng' WHEN 8 THEN N'Hoa'  WHEN 9 THEN N'Hùng'  ELSE N'Thảo' END;
    DECLARE @RecMName    NVARCHAR(50) = CASE WHEN @RecIdx % 2 = 0 THEN N'Thị' ELSE N'Văn' END;
    DECLARE @RecLName    NVARCHAR(50) = CASE WHEN @RecIdx % 2 = 0 THEN N'Nguyễn' ELSE N'Lê' END;

    INSERT INTO users (username, email, password_hash, status, email_verified,
                       first_name, middle_name, last_name, phone, gender, created_by)
    VALUES (@RecUsername, @RecEmail,
            '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK',
            'ACTIVE', 1, @RecFName, @RecMName, @RecLName,
            '091' + CAST((2000000 + @RecIdx * 1000) AS VARCHAR(15)),
            CASE WHEN @RecIdx % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
            (SELECT id FROM users WHERE username = 'admin'));
    SET @RecIdx = @RecIdx + 1;
END
GO

-- [06d] Users - PATIENT (7 benh nhan chinh)
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.thanh', 'nguyenminhthanh@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Thành', N'Minh',    N'Nguyễn', '0905555551', 'MALE',   '1990-05-15', 'O+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.huong', 'phamthihuong@gmail.com',   '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hương', N'Thị',     N'Phạm',   '0905555552', 'FEMALE', '1995-08-22', 'A+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.duc',   'levandduc@gmail.com',      '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Đức',   N'Văn',     N'Lê',     '0905555553', 'MALE',   '1985-03-10', 'B+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.lan',   'dothiblan@gmail.com',      '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 0, N'Lan',   N'Thị B',   N'Đỗ',     '0905555554', 'FEMALE', '2000-11-30', 'AB+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.nam',   'hoangnham@gmail.com',      '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Nam',   N'Hoài',    N'Hoàng',  '0905555555', 'MALE',   '1993-01-25', 'O-');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.dung',  'dungnguyen@gmail.com',     '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Dũng',  N'Tiến',    N'Nguyễn', '0905555556', 'MALE',   '1998-04-12', 'A-');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.hoa',   'hoatran@gmail.com',        '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hoa',   N'Thị',     N'Trần',   '0905555557', 'FEMALE', '1982-10-05', 'O+');
GO

-- [06e] Users - PATIENT (50 benh nhan test tu dong)
DECLARE @i INT = 1;
WHILE @i <= 50
BEGIN
    DECLARE @uname VARCHAR(50)  = 'patient.test' + CAST(@i AS VARCHAR(2));
    DECLARE @uemail VARCHAR(100) = 'patient.test' + CAST(@i AS VARCHAR(2)) + '@gmail.com';
    DECLARE @uphone VARCHAR(20)  = '0990000' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2);
    INSERT INTO users (username, email, password_hash, status, email_verified,
                       first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
    VALUES (@uname, @uemail, '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK',
            'ACTIVE', 1, N'Test', N'Bệnh Nhân', CAST(@i AS NVARCHAR(10)),
            @uphone, 'MALE', '1990-01-01', 'O+');
    SET @i = @i + 1;
END
GO

-- [07] User Roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
WHERE u.username IN ('admin', 'admin02') AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
WHERE u.username LIKE 'manager%' AND r.name = 'MANAGER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
WHERE u.username LIKE 'dr.%' AND r.name = 'DOCTOR';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
WHERE u.username LIKE 'recept%' AND r.name = 'RECEPTIONIST';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
WHERE u.username LIKE 'patient%' AND r.name = 'PATIENT';
GO

-- [08] User Addresses
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 30, N'123 Nguyễn Huệ, Quận 1', 1 FROM users WHERE username = 'patient.thanh';
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1,  N'45 Trần Duy Hưng, Cầu Giấy', 1 FROM users WHERE username = 'patient.huong';
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 21, N'88 Lê Duẩn, Hải Châu', 1 FROM users WHERE username = 'patient.duc';
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 30, N'12 Cộng Hòa, Phường 4, Tân Bình', 1 FROM users WHERE username = 'patient.lan';
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1,  N'Số 1, Phố Cổ, Hà Nội', 1 FROM users WHERE username IN ('patient.nam', 'patient.dung', 'patient.hoa');
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1,  N'Nhà số ' + CAST(id AS NVARCHAR(10)) + N', Đường Test', 1
FROM users WHERE username LIKE 'patient.test%';
GO

-- [09] Week Schedules (tu dong sinh 53 tuan nam 2026)
-- Tuan hien tai = FINALIZED, tuan sau = PUBLISHED, cac tuan con lai = DRAFT/EXPIRED
DECLARE @Today_WS         DATE = CAST(GETDATE() AS DATE);
DECLARE @Monday_This_WS   DATE = DATEADD(wk, DATEDIFF(wk, 0, @Today_WS), 0);
DECLARE @LoopDate_WS       DATE = '2025-12-29';
DECLARE @AdminId_WS        BIGINT = (SELECT id FROM users WHERE username = 'admin');

WHILE @LoopDate_WS <= '2026-12-28'
BEGIN
    DECLARE @WS_Status VARCHAR(20);
    IF      @LoopDate_WS < @Monday_This_WS                          SET @WS_Status = 'EXPIRED';
    ELSE IF @LoopDate_WS = @Monday_This_WS                          SET @WS_Status = 'FINALIZED';
    ELSE IF @LoopDate_WS = DATEADD(day, 7, @Monday_This_WS)         SET @WS_Status = 'PUBLISHED';
    ELSE                                                             SET @WS_Status = 'DRAFT';

    INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by)
    VALUES (@LoopDate_WS, DATEADD(day, 6, @LoopDate_WS), @WS_Status, @AdminId_WS);

    SET @LoopDate_WS = DATEADD(day, 7, @LoopDate_WS);
END
GO

-- [10] Doctor Schedules
-- Phan bo 6 bac si / khoa / ngay vao 3 phong lam sang (Toa A) x 2 ca:
--   Rank 1,2,3 -> MORNING  -> Phong A rank 1,2,3
--   Rank 4,5,6 -> AFTERNOON -> Phong A rank 1,2,3
-- Ket qua: moi phong co 1 bac si MORNING + 1 bac si AFTERNOON (khong xung dot)
-- Range: 14 ngay truoc -> 14 ngay sau ngay chay seed

IF OBJECT_ID('tempdb..#DR') IS NOT NULL DROP TABLE #DR;
IF OBJECT_ID('tempdb..#CR') IS NOT NULL DROP TABLE #CR;

SELECT id AS doctor_id, department_id,
       ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY id) AS dr_rank
INTO #DR
FROM users
WHERE department_id IS NOT NULL AND doctor_status IS NOT NULL AND status = 'ACTIVE';

SELECT id AS room_id, department_id,
       ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY id) AS room_rank
INTO #CR
FROM rooms
WHERE room_number LIKE '%-A';

DECLARE @SchedStart DATE = CAST(DATEADD(day, -14, GETDATE()) AS DATE);
DECLARE @SchedEnd   DATE = CAST(DATEADD(day,  14, GETDATE()) AS DATE);
DECLARE @SchedCurr  DATE = @SchedStart;
DECLARE @AdminId_S  BIGINT = (SELECT id FROM users WHERE username = 'admin');

WHILE @SchedCurr <= @SchedEnd
BEGIN
    DECLARE @WkId BIGINT = (
        SELECT id FROM week_schedules
        WHERE @SchedCurr BETWEEN week_start_date AND week_end_date
    );

    IF @WkId IS NOT NULL
    BEGIN
        INSERT INTO doctor_schedules
            (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
        SELECT
            @WkId,
            dr.doctor_id,
            cr.room_id,
            @SchedCurr,
            CASE WHEN dr.dr_rank <= 3 THEN 'MORNING' ELSE 'AFTERNOON' END,
            'ACTIVE',
            @AdminId_S
        FROM #DR dr
        JOIN #CR cr ON cr.department_id = dr.department_id
                    AND cr.room_rank = ((dr.dr_rank - 1) % 3) + 1
        WHERE dr.dr_rank <= 6;
    END

    SET @SchedCurr = DATEADD(day, 1, @SchedCurr);
END

DROP TABLE #DR;
DROP TABLE #CR;
GO

-- [11] Time Slots (tu dong sinh slot theo dung logic trong ScheduleServiceImpl.java)
-- Ca sang (MORNING, max_capacity = 4 trong code): 07:00-08:00 (cap 1), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2)
-- Ca chieu (AFTERNOON, max_capacity = 4 trong code): 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
-- Ca ca ngay (FULL_DAY, max_capacity = 10 trong code): 07:00-08:00 (cap 2), 09:00-10:00 (cap 2), 10:00-11:00 (cap 2), 13:00-14:00 (cap 2), 14:00-15:00 (cap 2)
INSERT INTO time_slots (schedule_id, start_time, end_time, booked_capacity, max_capacity, status)
SELECT ds.id, t.start_time, t.end_time, 0, t.max_capacity, 'AVAILABLE'
FROM doctor_schedules ds
CROSS JOIN (
    -- MORNING SHIFT (max_capacity = 4 trong Java: floor(4/3)=1, ceil(4/3)=2, ceil(4/3)=2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 1 AS max_capacity, 'MORNING' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'MORNING' UNION ALL
    SELECT '10:00', '11:00', 2, 'MORNING' UNION ALL
    -- AFTERNOON SHIFT (max_capacity = 4 trong Java: ceil(4/2)=2, ceil(4/2)=2)
    SELECT '13:00' AS start_time, '14:00' AS end_time, 2 AS max_capacity, 'AFTERNOON' AS shift UNION ALL
    SELECT '14:00', '15:00', 2, 'AFTERNOON' UNION ALL
    -- FULL_DAY SHIFT (max_capacity = 10 trong Java: 5 slots moi slot 2)
    SELECT '07:00' AS start_time, '08:00' AS end_time, 2 AS max_capacity, 'FULL_DAY' AS shift UNION ALL
    SELECT '09:00', '10:00', 2, 'FULL_DAY' UNION ALL
    SELECT '10:00', '11:00', 2, 'FULL_DAY' UNION ALL
    SELECT '13:00', '14:00', 2, 'FULL_DAY' UNION ALL
    SELECT '14:00', '15:00', 2, 'FULL_DAY'
) t
WHERE ds.shift = t.shift;
GO

-- [12] Articles & Comments (2 bai mau)
INSERT INTO articles (title, slug, summary, content, category, thumbnail_url, doctor_author_id, created_by, view_count, status, published_at, created_at, updated_at)
VALUES
(N'Chế độ ăn tốt cho tim mạch', 'che-do-an-tot-cho-tim-mach',
 N'Các thực phẩm có lợi giúp giảm cholesterol và bảo vệ mạch máu.',
 N'Hệ tim mạch là bộ phận quan trọng... Nên ăn nhiều rau xanh, cá béo và giảm muối...',
 N'Tim mạch',
 'https://res.cloudinary.com/demo/image/upload/v1570975200/heart.jpg',
 (SELECT TOP 1 id FROM users WHERE username = 'dr.timmach1'),
 (SELECT TOP 1 id FROM users WHERE username = 'admin'),
 120, 'PUBLISHED', GETDATE(), GETDATE(), GETDATE()),
(N'Phòng ngừa đau lưng ở dân văn phòng', 'phong-ngua-dau-lung-dan-van-phong',
 N'Hướng dẫn tư thế ngồi làm việc chuẩn và các bài tập giãn cơ tại chỗ.',
 N'Đau lưng là căn bệnh phổ biến... Cần duy trì tư thế ngồi thẳng, tập yoga...',
 N'Cơ xương khớp',
 'https://res.cloudinary.com/demo/image/upload/v1570975200/back.jpg',
 (SELECT TOP 1 id FROM users WHERE username = 'dr.coxuongkhop1'),
 (SELECT TOP 1 id FROM users WHERE username = 'admin'),
 85, 'PUBLISHED', GETDATE(), GETDATE(), GETDATE());

INSERT INTO article_comments (article_id, user_id, content, created_at) VALUES
((SELECT TOP 1 id FROM articles WHERE slug = 'che-do-an-tot-cho-tim-mach'),
 (SELECT TOP 1 id FROM users WHERE username = 'patient.thanh'), N'Bài viết rất hữu ích bác sĩ ơi!', GETDATE()),
((SELECT TOP 1 id FROM articles WHERE slug = 'che-do-an-tot-cho-tim-mach'),
 (SELECT TOP 1 id FROM users WHERE username = 'patient.huong'), N'Bác sĩ cho em hỏi huyết áp cao uống trà xanh có tốt không?', GETDATE()),
((SELECT TOP 1 id FROM articles WHERE slug = 'phong-ngua-dau-lung-dan-van-phong'),
 (SELECT TOP 1 id FROM users WHERE username = 'patient.duc'), N'Tôi đã thử và thấy đỡ đau hẳn.', GETDATE());
GO

-- [13] Appointments (chi den 25/07/2026 - du lieu lich su sach de test)
-- Tat ca ngay tu 14/07 den 25/07 deu la qua khu -> trang thai COMPLETED/CANCELLED/NO_SHOW
DECLARE @ApptStart   DATE   = '2026-07-14';
DECLARE @ApptEnd     DATE   = '2026-07-25';
DECLARE @ApptCurr    DATE   = @ApptStart;
DECLARE @ApptIndex   INT    = 1;

WHILE @ApptCurr <= @ApptEnd
BEGIN
    DECLARE @DailyCount INT = 1;

    WHILE @DailyCount <= 4
    BEGIN
        DECLARE @SlotId_A     BIGINT;
        DECLARE @DoctorId_A   BIGINT;
        DECLARE @ServiceId_A  BIGINT;
        DECLARE @PatientId_A  BIGINT;

        -- Chon 1 slot trong ngay con trong
        SELECT TOP 1
            @SlotId_A   = ts.id,
            @DoctorId_A = ds.doctor_id
        FROM time_slots ts
        JOIN doctor_schedules ds ON ts.schedule_id = ds.id
        WHERE ds.work_date = @ApptCurr
          AND ts.booked_capacity < ts.max_capacity
        ORDER BY NEWID();

        IF @SlotId_A IS NULL
        BEGIN
            SET @DailyCount = @DailyCount + 1;
            CONTINUE;
        END

        -- Lay dich vu mac dinh cua khoa bac si (dich vu thu nhat = kham lam sang)
        SELECT TOP 1 @ServiceId_A = ms.id
        FROM medical_services ms
        WHERE ms.department_id = (SELECT department_id FROM users WHERE id = @DoctorId_A)
        ORDER BY ms.id;

        -- Chon benh nhan ngau nhien
        SELECT TOP 1 @PatientId_A = id FROM users WHERE username LIKE 'patient.%' ORDER BY NEWID();

        -- Tinh trang thai: qua khu -> COMPLETED/CANCELLED/NO_SHOW
        DECLARE @RandA INT = ABS(CHECKSUM(NEWID())) % 100;
        DECLARE @ApptStatus_A VARCHAR(20);
        IF @RandA < 60      SET @ApptStatus_A = 'COMPLETED';
        ELSE IF @RandA < 80 SET @ApptStatus_A = 'CANCELLED';
        ELSE                SET @ApptStatus_A = 'NO_SHOW';

        DECLARE @ApptCode_A VARCHAR(20) = 'APT-' + FORMAT(@ApptCurr, 'yyyyMM') + RIGHT('0000' + CAST(@ApptIndex AS VARCHAR), 4);

        DECLARE @SlotStart_A TIME;
        SELECT @SlotStart_A = start_time FROM time_slots WHERE id = @SlotId_A;

        DECLARE @CheckIn_A DATETIME2 = NULL;
        IF @ApptStatus_A IN ('COMPLETED')
        BEGIN
            DECLARE @RandMin_A INT = (ABS(CHECKSUM(NEWID())) % 20) - 15;
            SET @CheckIn_A = DATEADD(minute, @RandMin_A,
                CAST(CAST(@ApptCurr AS DATETIME) + CAST(@SlotStart_A AS DATETIME) AS DATETIME2));
        END

        INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
        VALUES (@ApptCode_A, @PatientId_A, @DoctorId_A, @ServiceId_A, @SlotId_A,
                @ApptCurr, @CheckIn_A, @ApptStatus_A, N'Khám định kỳ - dữ liệu mẫu v10');

        DECLARE @NewApptId_A BIGINT = SCOPE_IDENTITY();

        IF @ApptStatus_A <> 'CANCELLED'
            UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotId_A;

        -- Tao benh an + hoa don cho cuoc hen COMPLETED
        IF @ApptStatus_A = 'COMPLETED'
        BEGIN
            INSERT INTO medical_records
                (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion,
                 prescription_text, heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
            VALUES (@NewApptId_A, @PatientId_A, @DoctorId_A,
                    N'Đau đầu nhẹ và mỏi cơ', N'Theo dõi sức khỏe tổng quát (Z00)',
                    N'Chế độ ăn hợp lý, tái khám khi có bất thường',
                    N'1. Vitamin C 500mg x 1 viên/ngày',
                    80, '120/80', 5.0, 60.0, 'FINALIZED', @DoctorId_A);

            DECLARE @SvcPrice_A  DECIMAL(12,2) = (SELECT reference_price FROM medical_services WHERE id = @ServiceId_A);
            DECLARE @InvStatus_A VARCHAR(20)   = CASE WHEN ABS(CHECKSUM(NEWID())) % 100 < 80 THEN 'PAID' ELSE 'UNPAID' END;
            DECLARE @PaidAt_A    DATETIME2     = CASE WHEN @InvStatus_A = 'PAID' THEN @ApptCurr ELSE NULL END;
            DECLARE @InvCode_A   VARCHAR(20)   = 'INV-' + FORMAT(@ApptCurr, 'yyyyMM') + RIGHT('0000' + CAST(@ApptIndex AS VARCHAR), 4);

            INSERT INTO invoices (invoice_code, appointment_id, total_amount, payment_method, payment_status, paid_at)
            VALUES (@InvCode_A, @NewApptId_A, @SvcPrice_A,
                    CASE WHEN @InvStatus_A = 'PAID' THEN 'CASH' ELSE 'PENDING' END,
                    @InvStatus_A, @PaidAt_A);

            DECLARE @NewInvId_A BIGINT = SCOPE_IDENTITY();

            INSERT INTO invoice_items (invoice_id, service_id, medical_service_order_id, item_name, price_applied, quantity, line_total)
            VALUES (@NewInvId_A, @ServiceId_A, NULL,
                    (SELECT name FROM medical_services WHERE id = @ServiceId_A),
                    @SvcPrice_A, 1, @SvcPrice_A);
        END

        -- Notification
        IF @ApptStatus_A IN ('COMPLETED')
        BEGIN
            INSERT INTO notifications (user_id, title, message, notification_type, is_read, related_entity_id, related_entity_type)
            VALUES (@PatientId_A, N'Cập nhật lịch khám',
                    N'Ca khám ' + @ApptCode_A + N' đã hoàn tất.',
                    'APPOINTMENT_CONFIRMED', 0, @NewApptId_A, 'APPOINTMENT');
        END

        SET @DailyCount  = @DailyCount + 1;
        SET @ApptIndex   = @ApptIndex + 1;
    END

    SET @ApptCurr = DATEADD(day, 1, @ApptCurr);
END
GO

-- ============================================================
-- VERIFY - Kiem tra so luong ban ghi
-- ============================================================
SELECT [table], [rows] FROM (
    SELECT 'roles'                      AS [table], COUNT(*) AS [rows] FROM roles
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
    UNION ALL SELECT 'invoice_items',   COUNT(*) FROM invoice_items
    UNION ALL SELECT 'articles',        COUNT(*) FROM articles
    UNION ALL SELECT 'article_comments',COUNT(*) FROM article_comments
) t ORDER BY [table];
GO

