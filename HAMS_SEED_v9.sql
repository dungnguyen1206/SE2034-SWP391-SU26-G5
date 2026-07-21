-- ============================================================
-- HAMS SEED DATA - Compatible với schema v9
-- Chạy file này SAU KHI đã chạy script tạo cấu trúc bảng v9
--
-- Đặc điểm: Tự động dọn dẹp dữ liệu cũ và reset IDENTITY counters
-- giúp chạy lại nhiều lần mà không bị lỗi trùng khóa hoặc sai ID.
-- Password mặc định cho TẤT CẢ các tài khoản: 123456
-- BCrypt Hash của "123456": $2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK
-- ============================================================
USE hams_db;
GO

-- ============================================================
-- 1. DỌN DẸP DỮ LIỆU CŨ (Theo đúng thứ tự ràng buộc khóa ngoại)
-- ============================================================
DELETE FROM notifications;
DELETE FROM email_logs;
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
EXEC sp_MSForEachTable '
    IF OBJECTPROPERTY(OBJECT_ID(''?''), ''TableHasIdentity'') = 1
    BEGIN
        DECLARE @LastValue sql_variant;
        SELECT @LastValue = last_value FROM sys.identity_columns WHERE object_id = OBJECT_ID(''?'');
        
        IF @LastValue IS NULL
            DBCC CHECKIDENT (''?'', RESEED, 1) WITH NO_INFOMSGS;
        ELSE
            DBCC CHECKIDENT (''?'', RESEED, 0) WITH NO_INFOMSGS;
    END
';
GO

-- ============================================================
-- 3. CHÈN DỮ LIỆU MẪU
-- ============================================================

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

-- [03] Departments (7 khoa kèm URL ảnh mặc định)
INSERT INTO departments (name, description, image_url, status) VALUES
(N'Tim Mạch',       N'Chẩn đoán và điều trị các bệnh lý về tim mạch, huyết áp, mạch máu', '/images/departments/tim-mach.jpg', 'ACTIVE'),
(N'Thần Kinh',      N'Chẩn đoán và điều trị các bệnh lý về não bộ, cột sống và hệ thần kinh', '/images/departments/than-kinh.jpg', 'ACTIVE'),
(N'Cơ Xương Khớp',  N'Chẩn đoán và điều trị các bệnh lý về xương, khớp, cơ và mô liên kết', '/images/departments/co-xuong-khop.jpg', 'ACTIVE'),
(N'Nội Tiêu Hóa',   N'Chẩn đoán và điều trị các bệnh lý về dạ dày, ruột, gan mật và tụy', '/images/departments/noi-tieu-hoa.jpg', 'ACTIVE'),
(N'Nhi',            N'Khám và điều trị bệnh cho trẻ em từ sơ sinh đến 15 tuổi', '/images/departments/nhi.jpg', 'ACTIVE'),
(N'Mắt',            N'Khám, điều trị và phẫu thuật các bệnh lý về mắt', '/images/departments/mat.jpg', 'ACTIVE'),
(N'Tai Mũi Họng',   N'Khám và điều trị các bệnh lý tai, mũi, họng người lớn và trẻ em', '/images/departments/tai-mui-hong.jpg', 'ACTIVE');
GO

-- [04] Medical Services (7 khoa x 10 dịch vụ = 70 dịch vụ)
-- === KHOA 1: TIM MẠCH ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(1, N'Khám tim mạch tổng quát', 300000, 30, N'Thăm khám lâm sàng tim mạch toàn diện', 'ACTIVE'),
(1, N'Đo điện tâm đồ (ECG)', 150000, 15, N'Ghi lại hoạt động điện của tim', 'ACTIVE'),
(1, N'Siêu âm tim', 450000, 45, N'Đánh giá cấu trúc và chức năng tim', 'ACTIVE'),
(1, N'Đo huyết áp 24 giờ (Holter HA)', 600000, 20, N'Theo dõi huyết áp liên tục 24 giờ', 'ACTIVE'),
(1, N'Holter ECG 24 giờ', 700000, 20, N'Ghi điện tâm đồ liên tục 24 giờ', 'ACTIVE'),
(1, N'Xét nghiệm mỡ máu (Lipid profile)', 250000, 15, N'Định lượng Cholesterol toàn phần', 'ACTIVE'),
(1, N'Xét nghiệm Troponin', 350000, 15, N'Xét nghiệm chẩn đoán nhồi máu cơ tim cấp', 'ACTIVE'),
(1, N'Tư vấn điều trị tăng huyết áp', 200000, 20, N'Tư vấn phác đồ thuốc, lối sống', 'ACTIVE'),
(1, N'Tư vấn sau can thiệp tim mạch', 250000, 30, N'Tái khám sau đặt stent, mổ tim', 'ACTIVE'),
(1, N'Chụp X-quang tim phổi', 180000, 15, N'Đánh giá kích thước tim và tình trạng phổi', 'ACTIVE');

-- === KHOA 2: THẦN KINH ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(2, N'Khám thần kinh tổng quát', 300000, 30, N'Thăm khám lâm sàng hệ thần kinh toàn diện', 'ACTIVE'),
(2, N'Điện não đồ (EEG)', 400000, 45, N'Ghi lại hoạt động điện của não', 'ACTIVE'),
(2, N'Điện cơ đồ (EMG)', 500000, 45, N'Đánh giá chức năng dây thần kinh và cơ', 'ACTIVE'),
(2, N'Siêu âm Doppler mạch não', 450000, 30, N'Đánh giá lưu lượng máu lên não', 'ACTIVE'),
(2, N'Tư vấn điều trị đau đầu mãn tính', 200000, 25, N'Chẩn đoán nguyên nhân đau đầu mãn tính', 'ACTIVE'),
(2, N'Tư vấn điều trị mất ngủ', 200000, 25, N'Đánh giá chất lượng giấc ngủ và điều trị', 'ACTIVE'),
(2, N'Xét nghiệm dịch não tủy', 800000, 60, N'Chọc dịch não tủy chẩn đoán viêm màng não', 'ACTIVE'),
(2, N'Đánh giá chức năng nhận thức', 350000, 40, N'Sàng lọc sa sút trí tuệ, Alzheimer', 'ACTIVE'),
(2, N'Tư vấn phục hồi sau đột quỵ', 300000, 30, N'Kế hoạch phục hồi chức năng vận động', 'ACTIVE'),
(2, N'Chụp MRI não', 1500000, 60, N'Chụp cộng hưởng từ não chẩn đoán u não', 'ACTIVE');

-- === KHOA 3: CƠ XƯƠNG KHỚP ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(3, N'Khám cơ xương khớp tổng quát', 300000, 30, N'Thăm khám lâm sàng hệ cơ xương khớp', 'ACTIVE'),
(3, N'Chụp X-quang xương khớp', 200000, 15, N'Chụp X-quang đánh giá cấu trúc xương', 'ACTIVE'),
(3, N'Siêu âm khớp', 350000, 30, N'Siêu âm gân cơ dây chằng và khớp', 'ACTIVE'),
(3, N'Đo mật độ xương (DEXA)', 500000, 30, N'Chẩn đoán loãng xương', 'ACTIVE'),
(3, N'Xét nghiệm Acid Uric (Gout)', 150000, 10, N'Định lượng Acid Uric trong máu', 'ACTIVE'),
(3, N'Xét nghiệm yếu tố dạng thấp (RF)', 200000, 10, N'Hỗ trợ chẩn đoán viêm khớp dạng thấp', 'ACTIVE'),
(3, N'Tiêm nội khớp', 600000, 20, N'Tiêm thuốc giảm đau và chống viêm vào khớp', 'ACTIVE'),
(3, N'Vật lý trị liệu cơ bản', 250000, 45, N'Liệu trình vật lý trị liệu phục hồi', 'ACTIVE'),
(3, N'Tư vấn điều trị loãng xương', 200000, 20, N'Tư vấn bổ sung Canxi và thuốc điều trị', 'ACTIVE'),
(3, N'Tư vấn dinh dưỡng bệnh khớp', 180000, 20, N'Chế độ ăn phù hợp cho bệnh nhân xương khớp', 'ACTIVE');

-- === KHOA 4: NỘI TIÊU HÓA ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(4, N'Khám tiêu hóa tổng quát', 300000, 30, N'Thăm khám lâm sàng hệ tiêu hóa toàn diện', 'ACTIVE'),
(4, N'Nội soi dạ dày', 700000, 30, N'Chẩn đoán viêm loét dạ dày thực quản', 'ACTIVE'),
(4, N'Nội soi đại tràng', 900000, 45, N'Phát hiện polyp và viêm đại tràng', 'ACTIVE'),
(4, N'Siêu âm ổ bụng', 300000, 30, N'Siêu âm gan mật tụy lách thận', 'ACTIVE'),
(4, N'Xét nghiệm H.Pylori', 250000, 10, N'Phát hiện vi khuẩn Hp gây loét dạ dày', 'ACTIVE'),
(4, N'Xét nghiệm chức năng gan (AST, ALT)', 200000, 10, N'Đánh giá tổn thương tế bào gan', 'ACTIVE'),
(4, N'Xét nghiệm viêm gan B và C', 350000, 10, N'Sàng lọc HBsAg và Anti-HCV', 'ACTIVE'),
(4, N'Tư vấn điều trị trào ngược dạ dày', 200000, 20, N'Tư vấn điều trị hội chứng GERD', 'ACTIVE'),
(4, N'Tư vấn dinh dưỡng bệnh tiêu hóa', 180000, 20, N'Chế độ ăn cho bệnh tiêu hóa mãn tính', 'ACTIVE'),
(4, N'Sinh thiết niêm mạc dạ dày', 500000, 20, N'Xét nghiệm giải phẫu bệnh lý niêm mạc', 'ACTIVE');

-- === KHOA 5: NHI ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(5, N'Khám nhi tổng quát', 250000, 30, N'Đánh giá tăng trưởng và phát triển của trẻ', 'ACTIVE'),
(5, N'Tiêm phòng vaccine', 200000, 15, N'Tiêm vaccine dịch vụ cho trẻ em', 'ACTIVE'),
(5, N'Xét nghiệm máu tổng quát (CBC)', 180000, 10, N'Phân tích công thức máu trẻ em', 'ACTIVE'),
(5, N'Siêu âm bụng trẻ em', 280000, 25, N'Siêu âm ổ bụng cho trẻ', 'ACTIVE'),
(5, N'Đo thị lực và sàng lọc mắt', 150000, 20, N'Phát hiện cận, loạn thị học đường', 'ACTIVE'),
(5, N'Đánh giá phát triển tâm thần vận động', 300000, 40, N'Đánh giá mốc phát triển ngôn ngữ, nhận thức', 'ACTIVE'),
(5, N'Tư vấn dinh dưỡng cho trẻ', 200000, 25, N'Chế độ ăn phù hợp từng độ tuổi', 'ACTIVE'),
(5, N'Khám sơ sinh và tư vấn cho mẹ', 250000, 30, N'Khám sơ sinh toàn diện và tư vấn chăm sóc', 'ACTIVE'),
(5, N'Xét nghiệm tầm soát dị ứng trẻ em', 400000, 15, N'Phát hiện các dị nguyên gây dị ứng', 'ACTIVE'),
(5, N'Tư vấn điều trị hen và viêm phế quản', 220000, 25, N'Chẩn đoán hen suyễn trẻ em', 'ACTIVE');

-- === KHOA 6: MẮT ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(6, N'Khám mắt tổng quát', 150000, 20, N'Khám tầm soát các bệnh lý về mắt', 'ACTIVE'),
(6, N'Đo khúc xạ máy tự động', 80000, 10, N'Đo độ cận, loạn máy tự động', 'ACTIVE'),
(6, N'Thử thị lực và thử kính', 100000, 15, N'Cắt kính cận, viễn, loạn thị', 'ACTIVE'),
(6, N'Khám sinh hiển vi điện tử', 120000, 15, N'Kiểm tra bán phần trước nhãn cầu', 'ACTIVE'),
(6, N'Đo nhãn áp', 90000, 10, N'Tầm soát bệnh thiên đầu thống (Glocom)', 'ACTIVE'),
(6, N'Soi đáy mắt trực tiếp', 150000, 20, N'Đánh giá võng mạc và dịch kính', 'ACTIVE'),
(6, N'Chụp ảnh màu võng mạc', 250000, 25, N'Ghi hình đáy mắt tầm soát bệnh lý', 'ACTIVE'),
(6, N'Siêu âm mắt (A/B)', 200000, 20, N'Siêu âm nhãn cầu và hốc mắt', 'ACTIVE'),
(6, N'Bơm rửa lệ đạo', 180000, 20, N'Thông tắc lệ đạo bị nghẽn', 'ACTIVE'),
(6, N'Lấy dị vật kết mạc nông', 150000, 15, N'Loại bỏ bụi bẩn, dị vật trong mắt', 'ACTIVE');

-- === KHOA 7: TAI MŨI HỌNG ===
INSERT INTO medical_services (department_id, name, reference_price, estimated_duration, description, status) VALUES
(7, N'Khám tai mũi họng thông thường', 150000, 15, N'Thăm khám lâm sàng tai mũi họng', 'ACTIVE'),
(7, N'Nội sơ tai mũi họng ống cứng', 250000, 20, N'Nội soi chẩn đoán viêm xoang, viêm tai', 'ACTIVE'),
(7, N'Nội sơ tai mũi họng ống mềm', 400000, 30, N'Nội soi không đau tầm soát u vùng họng', 'ACTIVE'),
(7, N'Đo thính lực đơn âm', 180000, 25, N'Đánh giá mức độ nghe kém', 'ACTIVE'),
(7, N'Đo nhĩ lượng', 120000, 15, N'Đánh giá độ thông tai giữa', 'ACTIVE'),
(7, N'Hút mũi bằng máy áp lực âm', 80000, 10, N'Hút sạch dịch nhầy mũi họng', 'ACTIVE'),
(7, N'Lấy dị vật tai/mũi đơn giản', 150000, 15, N'Gắp hạt cườm, côn trùng trong tai/mũi', 'ACTIVE'),
(7, N'Làm thuốc tai/mũi/họng', 90000, 15, N'Rửa, bôi thuốc tại chỗ vùng tai mũi họng', 'ACTIVE'),
(7, N'Xông họng bằng máy siêu âm', 70000, 15, N'Xông khí dung giảm viêm họng', 'ACTIVE'),
(7, N'Trích rạch áp xe amiđan', 500000, 30, N'Thủ thuật dẫn lưu ổ mủ amiđan', 'ACTIVE');
GO

-- [05] Rooms (7 khoa x 10 phòng = 70 phòng, tên phòng trùng với tên dịch vụ)
INSERT INTO rooms (department_id, name, room_number, status)
SELECT 
    department_id, 
    name, 
    'P' + CAST(department_id AS VARCHAR) + RIGHT('00' + CAST(ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY id) AS VARCHAR), 2), 
    'ACTIVE'
FROM medical_services;
GO

-- [06] Users - ADMIN & MANAGER
-- password cho tất cả: 123456
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin', 'admin@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Quản', N'Trị', N'Admin', '0900000001', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender)
VALUES ('admin02', 'admin02@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Minh', N'Hoàng', N'Vũ', '0900000009', 'MALE');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager01', 'manager01@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hùng', N'Văn', N'Trần', '0900000002', 'MALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('manager02', 'manager02@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Thảo', N'Phương', N'Lê', '0900000003', 'FEMALE', 1);
GO

-- [06b] Users - DOCTOR (7 bác sĩ cho 7 khoa, bao gồm dr.timmach1 bắt buộc)
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.timmach1', 'dr.timmach1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'An', N'Minh', N'Nguyễn', '0901234561', 'MALE', 1, '2015-06-13', N'Bác sĩ chuyên khoa I', 'LIC-BS-011', N'Bác sĩ tim mạch giàu kinh nghiệm', 'ACTIVE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.thankinh1', 'dr.thankinh1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Bình', N'Thị', N'Lê', '0901234562', 'FEMALE', 2, '2016-08-20', N'Thạc sĩ Y khoa', 'LIC-BS-021', N'Chuyên gia thần kinh học', 'ACTIVE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.coxuongkhop1', 'dr.coxuongkhop1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Cường', N'Văn', N'Phạm', '0901234563', 'MALE', 3, '2014-03-15', N'Tiến sĩ Y khoa', 'LIC-BS-031', N'Chuyên khoa xương khớp', 'ACTIVE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.noitieuhoa1', 'dr.noitieuhoa1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Dũng', N'Thanh', N'Hoàng', '0901234564', 'MALE', 4, '2017-10-10', N'Bác sĩ chuyên khoa I', 'LIC-BS-041', N'Chuyên điều trị dạ dày, tiêu hóa', 'ACTIVE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.nhi1', 'dr.nhi1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hương', N'Thu', N'Vũ', '0901234565', 'FEMALE', 5, '2018-05-12', N'Thạc sĩ Y khoa', 'LIC-BS-051', N'Bác sĩ nhi khoa tận tâm', 'ACTIVE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.mat1', 'dr.mat1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Linh', N'Quang', N'Đỗ', '0901234566', 'MALE', 6, '2019-12-05', N'Bác sĩ chuyên khoa I', 'LIC-BS-061', N'Chuyên khoa mắt nhãn khoa', 'ACTIVE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
VALUES ('dr.tmh1', 'dr.tmh1@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Tùng', N'Khánh', N'Phan', '0901234567', 'MALE', 7, '2013-02-18', N'Tiến sĩ Y khoa', 'LIC-BS-071', N'Bác sĩ tai mũi họng đầu ngành', 'ACTIVE', 1);
GO

-- [06c] Users - RECEPTIONIST (5 lễ tân)
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept01', 'recept01@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Linh', N'Thị', N'Nguyễn', '0912000001', 'FEMALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept02', 'recept02@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Mai', N'Văn', N'Lê', '0912000002', 'MALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept03', 'recept03@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Tuấn', N'Thành', N'Trần', '0912000003', 'MALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept04', 'recept04@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hương', N'Thị B', N'Phạm', '0912000004', 'FEMALE', 1);

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
VALUES ('recept05', 'recept05@hams.vn', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Lan', N'Thị', N'Lý', '0912000005', 'FEMALE', 1);
GO

-- [06d] Users - PATIENT (7 bệnh nhân chính + 10 bệnh nhân test)
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.thanh', 'nguyenminhthanh@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Thành', N'Minh', N'Nguyễn', '0905555551', 'MALE',   '1990-05-15', 'O+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.huong', 'phamthihuong@gmail.com',   '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hương', N'Thị',   N'Phạm',   '0905555552', 'FEMALE', '1995-08-22', 'A+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.duc',   'levandduc@gmail.com',      '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Đức',   N'Văn',    N'Lê',      '0905555553', 'MALE',   '1985-03-10', 'B+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.lan',   'dothiblan@gmail.com',      '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 0, N'Lan',   N'Thị B',  N'Đỗ',      '0905555554', 'FEMALE', '2000-11-30', 'AB+');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.nam',   'hoangnham@gmail.com',      '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Nam',   N'Hoài',   N'Hoàng',   '0905555555', 'MALE',   '1993-01-25', 'O-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.dung', 'dungnguyen@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Dũng', N'Tiến', N'Nguyễn', '0905555556', 'MALE', '1998-04-12', 'A-');

INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.hoa', 'hoatran@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hoa', N'Thị', N'Trần', '0905555557', 'FEMALE', '1982-10-05', 'O+');

-- Bệnh nhân test
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test1', 'patient.test1@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Khánh', N'Văn', N'Nguyễn', '0990000001', 'MALE', '1990-01-01', 'O+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test2', 'patient.test2@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hà', N'Thu', N'Lê', '0990000002', 'FEMALE', '1991-02-02', 'A+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test3', 'patient.test3@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Phong', N'Minh', N'Trần', '0990000003', 'MALE', '1992-03-03', 'B+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test4', 'patient.test4@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Thúy', N'Thị', N'Phạm', '0990000004', 'FEMALE', '1993-04-04', 'AB+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test5', 'patient.test5@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Huy', N'Quang', N'Hoàng', '0990000005', 'MALE', '1994-05-05', 'O-');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test6', 'patient.test6@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hải', N'Nam', N'Vũ', '0990000006', 'MALE', '1995-06-06', 'A-');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test7', 'patient.test7@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Phương', N'Thu', N'Lê', '0990000007', 'FEMALE', '1996-07-07', 'B-');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test8', 'patient.test8@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Đạt', N'Quốc', N'Nguyễn', '0990000008', 'MALE', '1997-08-08', 'O+');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test9', 'patient.test9@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Vy', N'Thị', N'Phan', '0990000009', 'FEMALE', '1998-09-09', 'AB-');
INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
VALUES ('patient.test10', 'patient.test10@gmail.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Bảo', N'Khánh', N'Đặng', '0990000010', 'MALE', '1999-10-10', 'O+');
GO

-- [07] User Roles (Gán động theo tên Role bảo vệ tính nhất quán)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username IN ('admin', 'admin02') AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username LIKE 'manager%' AND r.name = 'MANAGER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username LIKE 'dr.%' AND r.name = 'DOCTOR';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username LIKE 'recept%' AND r.name = 'RECEPTIONIST';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username LIKE 'patient%' AND r.name = 'PATIENT';
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

-- Thêm địa chỉ mặc định cho các bệnh nhân còn lại
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'Số 1, Phố Cổ, Hà Nội', 1 FROM users WHERE username IN ('patient.nam', 'patient.dung', 'patient.hoa');

INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'Địa chỉ Test Bệnh Nhân', 1 FROM users WHERE username LIKE 'patient.test%';
GO

-- [09] Week Schedules
-- Tự động sinh lịch 53 tuần của năm 2026. Các tuần cũ mặc định là EXPIRED.
-- Tuần hiện tại là FINALIZED, tuần sau là PUBLISHED, các tuần còn lại là DRAFT.
DECLARE @Today DATE = CAST(GETDATE() AS DATE);
DECLARE @Monday_ThisWeek DATE = DATEADD(wk, DATEDIFF(wk, 0, @Today), 0);
DECLARE @LoopDate DATE = '2025-12-29'; -- Thứ 2 đầu tiên chứa ngày của năm 2026
DECLARE @AdminId BIGINT = (SELECT id FROM users WHERE username = 'admin');

WHILE @LoopDate <= '2026-12-28'
BEGIN
    DECLARE @Status VARCHAR(20);
    
    IF @LoopDate < @Monday_ThisWeek
    BEGIN
        SET @Status = 'EXPIRED';
    END
    ELSE IF @LoopDate = @Monday_ThisWeek
    BEGIN
        SET @Status = 'FINALIZED';
    END
    ELSE IF @LoopDate = DATEADD(day, 7, @Monday_ThisWeek)
    BEGIN
        SET @Status = 'PUBLISHED';
    END
    ELSE
    BEGIN
        SET @Status = 'DRAFT';
    END

    INSERT INTO week_schedules (week_start_date, week_end_date, status, created_by)
    VALUES (@LoopDate, DATEADD(day, 6, @LoopDate), @Status, @AdminId);

    SET @LoopDate = DATEADD(day, 7, @LoopDate);
END
GO

-- [10] Doctor Schedules
-- Tự động sinh lịch khám cho 7 bác sĩ trong 3 tuần: Tuần trước, Tuần này và Tuần sau (tổng cộng 21 ngày)
-- Chạy trên cả 7 ngày để đảm bảo dù chạy test ngày nào cũng có ca làm việc và đặt lịch
DECLARE @Today DATE = CAST(GETDATE() AS DATE);
DECLARE @Monday_ThisWeek DATE = DATEADD(wk, DATEDIFF(wk, 0, @Today), 0);
DECLARE @StartDate DATE = DATEADD(day, -7, @Monday_ThisWeek); -- Thứ hai tuần trước
DECLARE @EndDate DATE = DATEADD(day, 20, @Monday_ThisWeek);   -- Chủ nhật tuần sau
DECLARE @CurrDate DATE = @StartDate;
DECLARE @AdminId BIGINT = (SELECT id FROM users WHERE username = 'admin');

WHILE @CurrDate <= @EndDate
BEGIN
    DECLARE @WeekId BIGINT = (SELECT id FROM week_schedules WHERE @CurrDate BETWEEN week_start_date AND week_end_date);
    
    IF @WeekId IS NOT NULL
    BEGIN
        -- Sinh ca trực duy nhất cho mỗi bác sĩ mỗi ngày (Sáng, Chiều, hoặc Cả Ngày xen kẽ)
        INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
        SELECT 
            @WeekId, 
            u.id, 
            (SELECT TOP 1 id FROM rooms r WHERE r.department_id = u.department_id ORDER BY r.id), 
            @CurrDate, 
            CASE 
                -- Thứ 3 và Thứ 5 xếp ca Cả ngày (FULL_DAY)
                WHEN DATEDIFF(day, 0, @CurrDate) % 7 IN (1, 3) THEN 'FULL_DAY'
                -- Các ngày khác xếp xen kẽ MORNING / AFTERNOON theo ID bác sĩ
                WHEN (u.id + DATEDIFF(day, 0, @CurrDate)) % 2 = 0 THEN 'MORNING'
                ELSE 'AFTERNOON'
            END, 
            'ACTIVE', 
            @AdminId
        FROM users u WHERE u.username LIKE 'dr.%';
    END
    SET @CurrDate = DATEADD(day, 1, @CurrDate);
END
GO

-- [11] Time Slots
-- Tự động sinh slot cho TẤT CẢ các ca trực vừa tạo (~4410 slots)
BEGIN TRAN;
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
WHERE ds.shift = t.shift OR (ds.shift = 'FULL_DAY' AND t.shift IN ('MORNING', 'AFTERNOON'));
COMMIT TRAN;
GO

-- ============================================================
-- 12. APPOINTMENTS, MEDICAL RECORDS, INVOICES (Deterministic Seeding)
-- ============================================================
DECLARE @Today DATE = CAST(GETDATE() AS DATE);
DECLARE @Monday_ThisWeek DATE = DATEADD(wk, DATEDIFF(wk, 0, @Today), 0);
DECLARE @Yesterday DATE = DATEADD(day, -1, @Today);
DECLARE @Tomorrow DATE = DATEADD(day, 1, @Today);

-- Patients
DECLARE @Patient1 BIGINT = (SELECT id FROM users WHERE username = 'patient.thanh');
DECLARE @Patient2 BIGINT = (SELECT id FROM users WHERE username = 'patient.huong');
DECLARE @Patient3 BIGINT = (SELECT id FROM users WHERE username = 'patient.duc');
DECLARE @Patient4 BIGINT = (SELECT id FROM users WHERE username = 'patient.lan');
DECLARE @Patient5 BIGINT = (SELECT id FROM users WHERE username = 'patient.nam');
DECLARE @Patient6 BIGINT = (SELECT id FROM users WHERE username = 'patient.dung');
DECLARE @Patient7 BIGINT = (SELECT id FROM users WHERE username = 'patient.hoa');

-- Doctors
DECLARE @DrTimMach BIGINT = (SELECT id FROM users WHERE username = 'dr.timmach1');
DECLARE @DrThanKinh BIGINT = (SELECT id FROM users WHERE username = 'dr.thankinh1');
DECLARE @DrNhi BIGINT = (SELECT id FROM users WHERE username = 'dr.nhi1');

-- Services
DECLARE @ServiceTimMach BIGINT = (SELECT id FROM medical_services WHERE department_id = 1 AND name = N'Khám tim mạch tổng quát');
DECLARE @ServiceThanKinh BIGINT = (SELECT id FROM medical_services WHERE department_id = 2 AND name = N'Khám thần kinh tổng quát');
DECLARE @ServiceNhi BIGINT = (SELECT id FROM medical_services WHERE department_id = 5 AND name = N'Khám nhi tổng quát');

-- ------------------------------------------------------------
-- DOCTOR 1: TIM MẠCH (dr.timmach1)
-- ------------------------------------------------------------

-- Hẹn 1: Hôm qua - Đã khám xong (COMPLETED), có Bệnh án & Hóa đơn đã thanh toán
DECLARE @SlotYesterday BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrTimMach AND ds.work_date = @Yesterday AND ts.start_time IN ('08:00', '13:00')
);
IF @SlotYesterday IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0001', @Patient1, @DrTimMach, @ServiceTimMach, @SlotYesterday, @Yesterday, 
            CAST(CAST(@Yesterday AS DATETIME) + CAST('07:50' AS DATETIME) AS DATETIME2), 'COMPLETED', N'Khám định kỳ huyết áp');
    DECLARE @ApptId1 BIGINT = SCOPE_IDENTITY();
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotYesterday;
    
    INSERT INTO medical_records (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion, prescription_text, heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
    VALUES (@ApptId1, @Patient1, @DrTimMach, N'Đau ngực nhẹ khi vận động mạnh', N'Thiếu máu cơ tim cục bộ (I24)', N'Tránh vận động quá sức, uống thuốc đúng giờ', N'1. Nitroglycerin 2.5mg x 2 viên/ngày\n2. Aspirin 81mg x 1 viên/ngày', 78, '130/80', 5.2, 65.5, 'FINALIZED', @DrTimMach);
    
    INSERT INTO invoices (invoice_code, appointment_id, total_amount, payment_method, payment_status, paid_at)
    VALUES ('INV-2607-0001', @ApptId1, 300000.00, 'CASH', 'PAID', CAST(CAST(@Yesterday AS DATETIME) + CAST('07:55' AS DATETIME) AS DATETIME2));
    DECLARE @InvId1 BIGINT = SCOPE_IDENTITY();
    
    INSERT INTO invoice_items (invoice_id, service_id, medical_service_order_id, item_name, price_applied, quantity, line_total)
    VALUES (@InvId1, @ServiceTimMach, NULL, N'Khám tim mạch tổng quát', 300000.00, 1, 300000.00);
END

-- Hẹn 2: Hôm nay - Đã xác nhận (CONFIRMED)
DECLARE @SlotToday1 BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrTimMach AND ds.work_date = @Today AND ts.start_time IN ('09:00', '14:00')
);
IF @SlotToday1 IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0002', @Patient2, @DrTimMach, @ServiceTimMach, @SlotToday1, @Today, NULL, 'CONFIRMED', N'Tái khám tim mạch');
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotToday1;
END

-- Hẹn 3: Hôm nay - Đang khám (IN_PROGRESS)
DECLARE @SlotToday2 BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrTimMach AND ds.work_date = @Today AND ts.id <> @SlotToday1 AND ts.start_time IN ('10:00', '15:00')
);
IF @SlotToday2 IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0003', @Patient3, @DrTimMach, @ServiceTimMach, @SlotToday2, @Today, 
            CAST(CAST(@Today AS DATETIME) + CAST('09:45' AS DATETIME) AS DATETIME2), 'IN_PROGRESS', N'Theo dõi hẹp van tim');
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotToday2;
END

-- Hẹn 4: Hôm nay - Chờ xác nhận (PENDING)
DECLARE @SlotToday3 BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrTimMach AND ds.work_date = @Today AND ts.id NOT IN (@SlotToday1, @SlotToday2) AND ts.start_time IN ('11:00', '16:00')
);
IF @SlotToday3 IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0004', @Patient4, @DrTimMach, @ServiceTimMach, @SlotToday3, @Today, NULL, 'PENDING', N'Đăng ký khám tim mạch');
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotToday3;
END

-- Hẹn 5: Ngày mai - Đã xác nhận (CONFIRMED)
DECLARE @SlotTomorrow BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrTimMach AND ds.work_date = @Tomorrow AND ts.start_time IN ('08:30', '13:30')
);
IF @SlotTomorrow IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0005', @Patient5, @DrTimMach, @ServiceTimMach, @SlotTomorrow, @Tomorrow, NULL, 'CONFIRMED', N'Đo điện tâm đồ định kỳ');
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotTomorrow;
END

-- ------------------------------------------------------------
-- DOCTOR 2: THẦN KINH (dr.thankinh1)
-- ------------------------------------------------------------

-- Hẹn 6: Hôm qua - Đã khám xong (COMPLETED)
DECLARE @SlotYesterday2 BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrThanKinh AND ds.work_date = @Yesterday AND ts.start_time IN ('09:00', '14:00')
);
IF @SlotYesterday2 IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0006', @Patient6, @DrThanKinh, @ServiceThanKinh, @SlotYesterday2, @Yesterday, 
            CAST(CAST(@Yesterday AS DATETIME) + CAST('08:50' AS DATETIME) AS DATETIME2), 'COMPLETED', N'Đau nửa đầu Migraine');
    DECLARE @ApptId6 BIGINT = SCOPE_IDENTITY();
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotYesterday2;
    
    INSERT INTO medical_records (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion, prescription_text, heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
    VALUES (@ApptId6, @Patient6, @DrThanKinh, N'Đau đầu giật nửa bên kèm buồn nôn', N'Hội chứng Migraine (G43)', N'Nghỉ ngơi phòng tối, tránh tiếng ồn', N'1. Paracetamol 500mg x 3 viên/ngày\n2. Sibelium 5mg x 1 viên/ngày', 72, '115/75', 4.8, 58.0, 'FINALIZED', @DrThanKinh);
    
    INSERT INTO invoices (invoice_code, appointment_id, total_amount, payment_method, payment_status, paid_at)
    VALUES ('INV-2607-0006', @ApptId6, 300000.00, 'CASH', 'PAID', CAST(CAST(@Yesterday AS DATETIME) + CAST('08:55' AS DATETIME) AS DATETIME2));
    DECLARE @InvId6 BIGINT = SCOPE_IDENTITY();
    
    INSERT INTO invoice_items (invoice_id, service_id, medical_service_order_id, item_name, price_applied, quantity, line_total)
    VALUES (@InvId6, @ServiceThanKinh, NULL, N'Khám thần kinh tổng quát', 300000.00, 1, 300000.00);
END

-- Hẹn 7: Hôm nay - Đã xác nhận (CONFIRMED)
DECLARE @SlotToday4 BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrThanKinh AND ds.work_date = @Today AND ts.start_time IN ('08:00', '13:00')
);
IF @SlotToday4 IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0007', @Patient7, @DrThanKinh, @ServiceThanKinh, @SlotToday4, @Today, NULL, 'CONFIRMED', N'Rối loạn giấc ngủ');
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotToday4;
END

-- ------------------------------------------------------------
-- DOCTOR 3: NHI (dr.nhi1)
-- ------------------------------------------------------------

-- Hẹn 8: Hôm nay - Đã xác nhận (CONFIRMED)
DECLARE @SlotToday5 BIGINT = (
    SELECT TOP 1 ts.id FROM time_slots ts 
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.doctor_id = @DrNhi AND ds.work_date = @Today AND ts.start_time IN ('09:30', '14:30')
);
IF @SlotToday5 IS NOT NULL
BEGIN
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
    VALUES ('APT-2607-0008', @Patient1, @DrNhi, @ServiceNhi, @SlotToday5, @Today, NULL, 'CONFIRMED', N'Khám dinh dưỡng trẻ em');
    UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SlotToday5;
END
GO

-- ============================================================
-- VERIFY
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
    UNION ALL SELECT 'medical_service_orders', COUNT(*) FROM medical_service_orders
    UNION ALL SELECT 'invoices',        COUNT(*) FROM invoices
    UNION ALL SELECT 'invoice_items',   COUNT(*) FROM invoice_items
) t ORDER BY [table];
GO
