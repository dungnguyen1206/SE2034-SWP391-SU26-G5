USE master;
GO

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'hams_db')
BEGIN
    ALTER DATABASE hams_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE hams_db;
END;
GO

CREATE DATABASE hams_db;
GO
USE hams_db;
GO

-- ============================================================
-- 1. provinces
-- ============================================================
CREATE TABLE provinces (
    id      INT             IDENTITY(1,1)   PRIMARY KEY,
    name    NVARCHAR(100)   NOT NULL,
    type    VARCHAR(10)     NOT NULL        DEFAULT 'PROVINCE'
);
GO

-- ============================================================
-- 2. roles
-- ============================================================
CREATE TABLE roles (
    id          INT             IDENTITY(1,1)   PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL        UNIQUE,
    description NVARCHAR(255)   NULL
);
GO

-- ============================================================
-- 3. departments
-- ============================================================
CREATE TABLE departments (
    id          INT             IDENTITY(1,1)   PRIMARY KEY,
    name        NVARCHAR(100)   NOT NULL,
    description NVARCHAR(MAX)   NULL,
    image_url   VARCHAR(500)    NULL,
    status      VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE'
);
GO

-- ============================================================
-- 4. rooms
-- ============================================================
CREATE TABLE rooms (
    id              INT             IDENTITY(1,1)   PRIMARY KEY,
    department_id   INT             NOT NULL,
    name            NVARCHAR(100)   NOT NULL,
    room_number     VARCHAR(20)     NOT NULL,
    status          VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    created_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (department_id) REFERENCES departments(id)
);
GO

-- ============================================================
-- 5. users
-- ============================================================
CREATE TABLE users (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    username            VARCHAR(50)     NOT NULL        UNIQUE,
    email               VARCHAR(100)    NOT NULL        UNIQUE,
    password_hash       VARCHAR(255)    NOT NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    email_verified      BIT             NOT NULL        DEFAULT 0,
    email_verified_at   DATETIME2       NULL,

    -- Thông tin cá nhân chung
    first_name          NVARCHAR(50)    NOT NULL,
    middle_name         NVARCHAR(50)    NULL,
    last_name           NVARCHAR(50)    NOT NULL,
    phone               VARCHAR(15)     NULL,
    gender              VARCHAR(20)     NULL,
    avatar              VARCHAR(255)    NULL,

    -- Trường đặc thù dành cho PATIENT
    date_of_birth       DATE            NULL,
    blood_type          VARCHAR(10)     NULL,

    -- Trường đặc thù dành cho DOCTOR
    department_id       INT             NULL,
    license_issue_date  DATE            NULL,               -- [v9] Ngày cấp chứng chỉ hành nghề để tính số năm KN động
    degree              NVARCHAR(100)   NULL,
    license_number      VARCHAR(50)     NULL,
    bio                 NVARCHAR(MAX)   NULL,
    doctor_status       VARCHAR(20)     NULL,

    -- Audit
    created_by          BIGINT          NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (created_by)    REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
GO

-- ============================================================
-- 6. user_roles
-- ============================================================
CREATE TABLE user_roles (
    user_id     BIGINT          NOT NULL,
    role_id     INT             NOT NULL,
    assigned_at DATETIME2       NOT NULL        DEFAULT GETDATE(),

    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
GO

-- ============================================================
-- 7. user_addresses
-- ============================================================
CREATE TABLE user_addresses (
    id              BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    province_id     INT             NOT NULL,
    address_line    NVARCHAR(255)   NOT NULL,
    is_default      BIT             NOT NULL        DEFAULT 1,
    created_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (user_id)     REFERENCES users(id),
    FOREIGN KEY (province_id) REFERENCES provinces(id)
);
GO

-- ============================================================
-- 8. medical_services
-- ============================================================
CREATE TABLE medical_services (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    department_id       INT             NOT NULL,
    name                NVARCHAR(150)   NOT NULL,
    reference_price     DECIMAL(12,2)   NOT NULL,
    estimated_duration  INT             NOT NULL        DEFAULT 30,
    description         NVARCHAR(MAX)   NULL,
    image_url           VARCHAR(500)    NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (department_id) REFERENCES departments(id)
);
GO

-- ============================================================
-- 9. week_schedules  [Giữ nguyên từ v8.3]
-- Quản lý lifecycle lịch theo tuần cho toàn bệnh viện
-- status: DRAFT / PUBLISHED / FINALIZED / EXPIRED
--
-- DRAFT     : Manager đang soạn lịch, bệnh nhân chưa book được
-- PUBLISHED : Lịch hiển thị cho bác sĩ xem, bệnh nhân chưa book được
-- FINALIZED : Lịch chính thức, bệnh nhân book được
-- EXPIRED   : Quá deadline chủ nhật mà chưa Finalized
-- ============================================================
CREATE TABLE week_schedules (
    id              BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    week_start_date DATE            NOT NULL,           -- Luôn là thứ 2
    week_end_date   DATE            NOT NULL,           -- Luôn là chủ nhật
    status          VARCHAR(20)     NOT NULL        DEFAULT 'DRAFT',
    created_by      BIGINT          NULL,
    created_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),

    CONSTRAINT UQ_week_start UNIQUE (week_start_date),
    FOREIGN KEY (created_by) REFERENCES users(id)
);
GO

-- ============================================================
-- 10. doctor_schedules  [Giữ nguyên từ v8.3]
-- FK week_schedule_id gắn shift vào tuần
-- note (max 256 ký tự) cho Manager ghi chú khi tạo shift
-- status: ACTIVE / CANCELLED
--   ACTIVE    : Ca làm việc bình thường
--   CANCELLED : Ca bị hủy (có hoặc không có appointment)
-- shift: MORNING / AFTERNOON / FULL_DAY
--   MORNING  : 07:00 - 12:00 (2 khung: 07:00-09:00, 09:00-12:00)
--   AFTERNOON: 13:00 - 17:00 (2 khung: 13:00-15:00, 15:00-17:00)
--   FULL_DAY : 07:00 - 17:00 (4 khung, gộp MORNING + AFTERNOON)
-- ============================================================
CREATE TABLE doctor_schedules (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    week_schedule_id    BIGINT          NOT NULL,           -- FK → week_schedules
    doctor_id           BIGINT          NOT NULL,
    room_id             INT             NOT NULL,
    work_date           DATE            NOT NULL,
    shift               VARCHAR(20)     NOT NULL,
    note                NVARCHAR(256)   NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    created_by          BIGINT          NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (week_schedule_id)  REFERENCES week_schedules(id),
    FOREIGN KEY (doctor_id)         REFERENCES users(id),
    FOREIGN KEY (room_id)           REFERENCES rooms(id),
    FOREIGN KEY (created_by)        REFERENCES users(id)
);
GO

-- ============================================================
-- 11. time_slots  [Giữ nguyên từ v8.3]
-- Slot khám bệnh nhân đặt lịch vào
-- status: AVAILABLE / FULL
-- version: optimistic locking tránh race condition khi book đồng thời
-- ============================================================
CREATE TABLE time_slots (
    id              BIGINT      IDENTITY(1,1)   PRIMARY KEY,
    schedule_id     BIGINT      NOT NULL,
    start_time      TIME        NOT NULL,
    end_time        TIME        NOT NULL,
    booked_capacity INT         NOT NULL        DEFAULT 0,
    max_capacity    INT         NOT NULL        DEFAULT 5,
    status          VARCHAR(20) NOT NULL        DEFAULT 'AVAILABLE',
    version         BIGINT      NOT NULL        DEFAULT 0,

    FOREIGN KEY (schedule_id) REFERENCES doctor_schedules(id)
);
GO

-- ============================================================
-- 12. appointments  [Giữ nguyên từ v8.3]
-- status: PENDING / CONFIRMED / IN_PROGRESS / COMPLETED / CANCELLED
--   PENDING     : Bệnh nhân vừa đặt, chờ xác nhận
--   CONFIRMED   : Đã xác nhận lịch hẹn
--   IN_PROGRESS : Đang trong quá trình khám (sau khi check-in & thanh toán lần 1)
--   COMPLETED   : Đã khám xong
--   CANCELLED   : Đã hủy (bởi patient hoặc do shift bị cancel)
-- ============================================================
CREATE TABLE appointments (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    appointment_code    VARCHAR(20)     NOT NULL,
    patient_id          BIGINT          NOT NULL,
    doctor_id           BIGINT          NOT NULL,
    service_id          BIGINT          NOT NULL,
    slot_id             BIGINT          NOT NULL,
    booking_date        DATE            NOT NULL,
    check_in_time       DATETIME2       NULL,
    note                NVARCHAR(MAX)   NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'PENDING',
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (patient_id)  REFERENCES users(id),
    FOREIGN KEY (doctor_id)   REFERENCES users(id),
    FOREIGN KEY (service_id)  REFERENCES medical_services(id),
    FOREIGN KEY (slot_id)     REFERENCES time_slots(id)
);
GO

-- ============================================================
-- 13. medical_records  [Giữ nguyên từ v8.3]
-- Quan hệ 1-1 STRICT với Appointment
-- status: DRAFT / FINALIZED
--   DRAFT     : Bác sĩ đang khám, đang chờ kết quả cận lâm sàng
--   FINALIZED : Đã kết luận bệnh án, đóng hồ sơ
-- ============================================================
CREATE TABLE medical_records (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    appointment_id      BIGINT          NOT NULL        UNIQUE,
    patient_id          BIGINT          NOT NULL,
    doctor_id           BIGINT          NOT NULL,
    examination_date    DATETIME2       NOT NULL        DEFAULT GETDATE(),
    symptoms            NVARCHAR(MAX)   NOT NULL,
    diagnosis           NVARCHAR(MAX)   NOT NULL,
    conclusion          NVARCHAR(MAX)   NOT NULL,
    prescription_text   NVARCHAR(MAX)   NULL,
    notes               NVARCHAR(MAX)   NULL,
    heart_rate          INT             NULL,
    blood_pressure      VARCHAR(20)     NULL,
    blood_glucose       DECIMAL(5,2)    NULL,
    weight              DECIMAL(5,2)    NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'DRAFT',
    created_by          BIGINT          NULL,
    updated_by          BIGINT          NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (patient_id)     REFERENCES users(id),
    FOREIGN KEY (doctor_id)      REFERENCES users(id),
    FOREIGN KEY (created_by)     REFERENCES users(id),
    FOREIGN KEY (updated_by)     REFERENCES users(id)
);
GO

-- ============================================================
-- 14. medical_service_orders  [MỚI - v9]
-- Bảng trung gian: Bác sĩ chỉ định dịch vụ cận lâm sàng
-- Quan hệ: medical_records (1-N) → medical_service_orders
--           medical_services (1-N) → medical_service_orders
--
-- status lifecycle:
--   PENDING_PAYMENT : Bác sĩ vừa kê chỉ định, bệnh nhân chưa đóng tiền
--   PAID            : Lễ tân đã thu tiền (bác sĩ được phép nhập kết quả)
--   COMPLETED       : Bác sĩ đã nhập kết quả cận lâm sàng
--   CANCELLED       : Bệnh nhân từ chối / hoàn tiền / hủy chỉ định
--
-- Luồng trạng thái:
--   PENDING_PAYMENT → PAID      (tự động khi invoice thanh toán thành công)
--   PAID            → COMPLETED (bác sĩ nhập kết quả)
--   PENDING_PAYMENT → CANCELLED (bệnh nhân không đóng tiền, hoặc bác sĩ hủy)
--   PAID            → CANCELLED (hoàn tiền qua lễ tân)
-- ============================================================
CREATE TABLE medical_service_orders (
    id                      BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    medical_record_id       BIGINT          NOT NULL,
    medical_service_id      BIGINT          NOT NULL,
    price_applied           DECIMAL(12,2)   NOT NULL,           -- Giá dịch vụ tại thời điểm chỉ định
    status                  VARCHAR(30)     NOT NULL        DEFAULT 'PENDING_PAYMENT',
    result                  NVARCHAR(MAX)   NULL,               -- Kết quả cận lâm sàng (bác sĩ nhập)
    notes                   NVARCHAR(500)   NULL,               -- Lời dặn của bác sĩ khi chỉ định
    created_at              DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at              DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (medical_record_id)  REFERENCES medical_records(id),
    FOREIGN KEY (medical_service_id) REFERENCES medical_services(id)
);
GO

-- ============================================================
-- 15. invoices  [CẬP NHẬT - v9]
-- Quan hệ N-1 với Appointment (1 appointment có thể có nhiều invoice)
--   Invoice #1 : Tiền khám lâm sàng ban đầu (tạo lúc check-in)
--   Invoice #2+: Tiền dịch vụ cận lâm sàng phát sinh (tạo sau khi bác sĩ chỉ định)
--
-- payment_status: UNPAID / PAID / REFUNDED
--   UNPAID   : Chờ bệnh nhân đóng tiền
--   PAID     : Đã thu tiền thành công
--   REFUNDED : Đã hoàn tiền cho bệnh nhân
-- ============================================================
CREATE TABLE invoices (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    invoice_code        VARCHAR(20)     NOT NULL,
    appointment_id      BIGINT          NOT NULL,               -- [v9] Thay thế medical_record_id
    total_amount        DECIMAL(12,2)   NOT NULL        DEFAULT 0.00,
    payment_method      VARCHAR(20)     NOT NULL        DEFAULT 'PENDING',
    payment_status      VARCHAR(20)     NOT NULL        DEFAULT 'UNPAID',
    paid_at             DATETIME2       NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (appointment_id) REFERENCES appointments(id)    -- [v9] KHÔNG có UNIQUE → cho phép 1-N
);
GO

-- ============================================================
-- 16. invoice_items  [CẬP NHẬT - v9]
-- Thêm FK nullable tới medical_service_orders (quan hệ 1-0..1)
--   NULL     : Dòng hóa đơn cho dịch vụ khám ban đầu (check-in)
--   NOT NULL : Dòng hóa đơn cho dịch vụ cận lâm sàng do bác sĩ chỉ định
-- ============================================================
CREATE TABLE invoice_items (
    id                          BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    invoice_id                  BIGINT          NOT NULL,
    service_id                  BIGINT          NOT NULL,
    medical_service_order_id    BIGINT          NULL,               -- [v9] FK nullable → medical_service_orders
    item_name                   NVARCHAR(150)   NOT NULL,
    price_applied               DECIMAL(12,2)   NOT NULL,
    quantity                    INT             NOT NULL        DEFAULT 1,
    line_total                  DECIMAL(12,2)   NOT NULL,

    FOREIGN KEY (invoice_id)                REFERENCES invoices(id),
    FOREIGN KEY (service_id)                REFERENCES medical_services(id),
    FOREIGN KEY (medical_service_order_id)  REFERENCES medical_service_orders(id)
);
GO

-- [v9] Unique Filtered Index: đảm bảo 1 chỉ định chỉ xuất hiện trên tối đa 1 dòng hóa đơn
-- Bỏ qua các dòng NULL (dịch vụ khám ban đầu) → cho phép nhiều dòng NULL tồn tại
CREATE UNIQUE NONCLUSTERED INDEX UQ_invoice_items_service_order
ON invoice_items (medical_service_order_id)
WHERE medical_service_order_id IS NOT NULL;
GO

-- ============================================================
-- 17. articles  [Giữ nguyên từ v8.3]
-- ============================================================
CREATE TABLE articles (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    title               NVARCHAR(200)   NOT NULL,
    slug                VARCHAR(250)    NOT NULL,
    summary             NVARCHAR(500)   NULL,
    content             NVARCHAR(MAX)   NOT NULL,
    category            NVARCHAR(100)   NULL,
    thumbnail_url       VARCHAR(500)    NULL,
    doctor_author_id    BIGINT          NULL,
    created_by          BIGINT          NOT NULL,
    view_count          INT             NOT NULL        DEFAULT 0,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'DRAFT',
    published_at        DATETIME2       NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (doctor_author_id) REFERENCES users(id),
    FOREIGN KEY (created_by)       REFERENCES users(id)
);
GO

-- [v9] Bảng news đã bị xóa — chức năng tin tức gộp vào bảng articles (dùng cột category)

-- ============================================================
-- 18. email_logs  [Giữ nguyên từ v8.3]
-- ============================================================
CREATE TABLE email_logs (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    email_type          VARCHAR(50)     NOT NULL,
    recipient           VARCHAR(100)    NOT NULL,
    subject             NVARCHAR(200)   NULL,
    sent_at             DATETIME2       NOT NULL        DEFAULT GETDATE(),
    status              VARCHAR(20)     NOT NULL,
    error_message       NVARCHAR(MAX)   NULL,
    related_entity_id   BIGINT          NULL,
    related_entity_type VARCHAR(50)     NULL
);
GO

-- ============================================================
-- 19. notifications
-- ============================================================
CREATE TABLE notifications (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    user_id             BIGINT          NOT NULL,
    title               NVARCHAR(200)   NOT NULL,
    message             NVARCHAR(MAX)   NOT NULL,
    notification_type   VARCHAR(50)     NOT NULL,
    is_read             BIT             NOT NULL        DEFAULT 0,
    related_entity_id   BIGINT          NULL,
    related_entity_type VARCHAR(50)     NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- ============================================================
-- QUICK VERIFY  [CẬP NHẬT - v9]
-- ============================================================
SELECT [table], [rows] FROM (
    SELECT 'roles'                      AS [table], COUNT(*) AS [rows] FROM roles
    UNION ALL SELECT 'users'                        , COUNT(*) FROM users
    UNION ALL SELECT 'user_roles'                   , COUNT(*) FROM user_roles
    UNION ALL SELECT 'user_addresses'               , COUNT(*) FROM user_addresses
    UNION ALL SELECT 'departments'                  , COUNT(*) FROM departments
    UNION ALL SELECT 'rooms'                        , COUNT(*) FROM rooms
    UNION ALL SELECT 'provinces'                    , COUNT(*) FROM provinces
    UNION ALL SELECT 'medical_services'             , COUNT(*) FROM medical_services
    UNION ALL SELECT 'week_schedules'               , COUNT(*) FROM week_schedules
    UNION ALL SELECT 'doctor_schedules'             , COUNT(*) FROM doctor_schedules
    UNION ALL SELECT 'time_slots'                   , COUNT(*) FROM time_slots
    UNION ALL SELECT 'appointments'                 , COUNT(*) FROM appointments
    UNION ALL SELECT 'medical_records'              , COUNT(*) FROM medical_records
    UNION ALL SELECT 'medical_service_orders'       , COUNT(*) FROM medical_service_orders
    UNION ALL SELECT 'invoices'                     , COUNT(*) FROM invoices
    UNION ALL SELECT 'invoice_items'                , COUNT(*) FROM invoice_items
    UNION ALL SELECT 'articles'                     , COUNT(*) FROM articles
    UNION ALL SELECT 'email_logs'                   , COUNT(*) FROM email_logs
    UNION ALL SELECT 'notifications'                , COUNT(*) FROM notifications
) t ORDER BY [table];
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
-- Tạo phòng tự động theo tên dịch vụ bằng ROW_NUMBER() để đảm bảo tính nhất quán
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

-- [06b] Users - DOCTOR (7 khoa x 10 bác sĩ = 70 bác sĩ)
-- Tạo bác sĩ tự động bằng vòng lặp T-SQL để gán tên, email, ngày cấp chứng chỉ hành nghề (licence_issue_date) và thông tin sinh học ngẫu nhiên
DECLARE @DeptId INT = 1;
DECLARE @DocIdx INT = 1;
DECLARE @FirstNames TABLE (idx INT, name NVARCHAR(50));
DECLARE @LastNames TABLE (idx INT, name NVARCHAR(50));
DECLARE @MiddleNames TABLE (idx INT, name NVARCHAR(50));

INSERT INTO @FirstNames VALUES (0, N'An'), (1, N'Bình'), (2, N'Cường'), (3, N'Dũng'), (4, N'Anh'), (5, N'Hương'), (6, N'Linh'), (7, N'Tùng'), (8, N'Tài'), (9, N'Thúy');
INSERT INTO @LastNames VALUES (0, N'Nguyễn'), (1, N'Trần'), (2, N'Lê'), (3, N'Phạm'), (4, N'Hoàng'), (5, N'Vũ'), (6, N'Đỗ'), (7, N'Phan'), (8, N'Lý'), (9, N'Đặng');
INSERT INTO @MiddleNames VALUES (0, N'Văn'), (1, N'Thị'), (2, N'Minh'), (3, N'Quốc'), (4, N'Hoàng'), (5, N'Thu'), (6, N'Khánh'), (7, N'Thanh'), (8, N'Quang'), (9, N'Hồng');

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
        DECLARE @Email VARCHAR(100) = @Username + '@hams.vn';
        DECLARE @FName NVARCHAR(50) = (SELECT name FROM @FirstNames WHERE idx = (@DocIdx - 1));
        DECLARE @MName NVARCHAR(50) = (SELECT name FROM @MiddleNames WHERE idx = (@DeptId + @DocIdx) % 10);
        DECLARE @LName NVARCHAR(50) = (SELECT name FROM @LastNames WHERE idx = (@DeptId - 1));
        DECLARE @LicenseNo VARCHAR(50) = 'LIC-BS-' + RIGHT('000' + CAST((@DeptId * 10 + @DocIdx) AS VARCHAR), 3);
        DECLARE @LicDate DATE = DATEADD(year, - (5 + ((@DeptId + @DocIdx) % 15)), '2026-06-13');

        INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, department_id, license_issue_date, degree, license_number, bio, doctor_status, created_by)
        VALUES (
            @Username, @Email, 
            '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', -- 123456
            'ACTIVE', 1, @FName, @MName, @LName, 
            '090' + CAST((1000000 + @DeptId * 100000 + @DocIdx * 1000) AS VARCHAR(15)),
            CASE WHEN @DocIdx % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
            @DeptId, @LicDate, 
            CASE WHEN @DocIdx % 3 = 0 THEN N'Tiến sĩ Y khoa' WHEN @DocIdx % 3 = 1 THEN N'Thạc sĩ Y khoa' ELSE N'Bác sĩ chuyên khoa I' END,
            @LicenseNo, N'Bác sĩ chuyên khoa khám chữa bệnh tại khoa.', 'ACTIVE', 3
        );
        
        SET @DocIdx = @DocIdx + 1;
    END
    SET @DeptId = @DeptId + 1;
END
GO

-- [06c] Users - RECEPTIONIST (Sinh 10 lễ tân tự động)
DECLARE @RecIdx INT = 1;
WHILE @RecIdx <= 10
BEGIN
    DECLARE @RecUsername VARCHAR(50) = 'recept' + RIGHT('0' + CAST(@RecIdx AS VARCHAR), 2);
    DECLARE @RecEmail VARCHAR(100) = @RecUsername + '@hams.vn';
    
    -- Tạm thời lấy tên từ bảng khai báo phía trên bác sĩ
    DECLARE @RecFName NVARCHAR(50) = CASE @RecIdx 
        WHEN 1 THEN N'Linh' WHEN 2 THEN N'Mai' WHEN 3 THEN N'Tuấn' WHEN 4 THEN N'Hương' WHEN 5 THEN N'Lan'
        WHEN 6 THEN N'Nam' WHEN 7 THEN N'Dũng' WHEN 8 THEN N'Hoa' WHEN 9 THEN N'Hùng' ELSE N'Thảo' END;
    DECLARE @RecMName NVARCHAR(50) = CASE WHEN @RecIdx % 2 = 0 THEN N'Thị' ELSE N'Văn' END;
    DECLARE @RecLName NVARCHAR(50) = CASE WHEN @RecIdx % 2 = 0 THEN N'Nguyễn' ELSE N'Lê' END;

    INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, created_by)
    VALUES (
        @RecUsername, @RecEmail, 
        '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', -- 123456
        'ACTIVE', 1, @RecFName, @RecMName, @RecLName, 
        '091' + CAST((2000000 + @RecIdx * 1000) AS VARCHAR(15)),
        CASE WHEN @RecIdx % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END, 3
    );
    SET @RecIdx = @RecIdx + 1;
END
GO

-- [06d] Users - PATIENT
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
GO

-- [06e] Users - PATIENT (Tạo thêm 500 bệnh nhân test)
DECLARE @i INT = 1;
DECLARE @username VARCHAR(50);
DECLARE @email VARCHAR(100);
DECLARE @phone VARCHAR(20);

WHILE @i <= 500
BEGIN
    SET @username = 'patient.test' + CAST(@i AS VARCHAR(10));
    SET @email = 'patient.test' + CAST(@i AS VARCHAR(10)) + '@gmail.com';
    SET @phone = '099' + RIGHT('0000000' + CAST(@i AS VARCHAR(10)), 7);
    
    INSERT INTO users (username, email, password_hash, status, email_verified, first_name, middle_name, last_name, phone, gender, date_of_birth, blood_type)
    VALUES (@username, @email, '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Test', N'Bệnh Nhân', CAST(@i AS NVARCHAR(10)), @phone, 'MALE', '1990-01-01', 'O+');
    
    SET @i = @i + 1;
END
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

-- Thêm địa chỉ mặc định cho các bệnh nhân có sẵn còn lại
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'Số 1, Phố Cổ, Hà Nội', 1 FROM users WHERE username IN ('patient.nam', 'patient.dung', 'patient.hoa');

-- Thêm địa chỉ tự động cho 500 bệnh nhân test (Chọn province ngẫu nhiên hoặc 1)
INSERT INTO user_addresses (user_id, province_id, address_line, is_default)
SELECT id, 1, N'Nhà số ' + CAST(id AS NVARCHAR(10)) + N', Đường Test', 1 
FROM users 
WHERE username LIKE 'patient.test%';
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
-- Tự động sinh lịch khám cho TẤT CẢ các bác sĩ từ 30 ngày trước đến 90 ngày sau (tổng 121 ngày)
DECLARE @StartDate DATE = CAST(DATEADD(day, -30, GETDATE()) AS DATE);
DECLARE @EndDate DATE = CAST(DATEADD(day, 90, GETDATE()) AS DATE);
DECLARE @CurrDate DATE = @StartDate;

WHILE @CurrDate <= @EndDate
BEGIN
    BEGIN TRAN;
    DECLARE @WeekId BIGINT = (SELECT id FROM week_schedules WHERE @CurrDate BETWEEN week_start_date AND week_end_date);
    
    -- Xếp ca sáng
    INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
    SELECT 
        @WeekId, 
        u.id, 
        -- Chọn phòng khám trùng tên với dịch vụ tim mạch/thần kinh đầu tiên của khoa
        (SELECT TOP 1 id FROM rooms r WHERE r.department_id = u.department_id ORDER BY r.id), 
        @CurrDate, 
        'MORNING', 
        'ACTIVE', 
        (SELECT id FROM users WHERE username = 'admin')
    FROM users u WHERE u.username LIKE 'dr.%';

    -- Xếp ca chiều
    INSERT INTO doctor_schedules (week_schedule_id, doctor_id, room_id, work_date, shift, status, created_by)
    SELECT 
        @WeekId, 
        u.id, 
        (SELECT TOP 1 id FROM rooms r WHERE r.department_id = u.department_id ORDER BY r.id), 
        @CurrDate, 
        'AFTERNOON', 
        'ACTIVE', 
        (SELECT id FROM users WHERE username = 'admin')
    FROM users u WHERE u.username LIKE 'dr.%';

    SET @CurrDate = DATEADD(day, 1, @CurrDate);
    COMMIT TRAN;
END
GO

-- [11] Time Slots
-- Tự động sinh slot cho TẤT CẢ các ca trực vừa tạo
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
WHERE ds.shift = t.shift;
COMMIT TRAN;
GO

-- [12] Appointments (Sinh data mẫu cho 121 ngày từ -30 đến +90, mỗi ngày 200 cuộc hẹn, tổng cộng 24200 cuộc hẹn)
DECLARE @DayOffset INT = -30;
DECLARE @ApptIndex INT = 1;

WHILE @DayOffset <= 90
BEGIN
    BEGIN TRAN;
    DECLARE @TargetDate DATE = CAST(DATEADD(day, @DayOffset, GETDATE()) AS DATE);
    DECLARE @DailyAppts INT = 1;
    
    -- Lưu tạm danh sách slot của ngày hôm nay vào bảng tạm (tăng tốc độ random)
    SELECT ts.id as slot_id, ds.doctor_id 
    INTO #TempDailySlots
    FROM time_slots ts
    JOIN doctor_schedules ds ON ts.schedule_id = ds.id
    WHERE ds.work_date = @TargetDate;

    WHILE @DailyAppts <= 200
    BEGIN
        DECLARE @SelectedSlotId BIGINT;
        DECLARE @SelectedDoctorId BIGINT;
        DECLARE @SelectedServiceId BIGINT;
        
        -- Chọn 1 slot ngẫu nhiên từ bảng tạm (nhanh hơn rất nhiều so với JOIN bảng thật mỗi vòng lặp)
        SELECT TOP 1 
            @SelectedSlotId = slot_id,
            @SelectedDoctorId = doctor_id
        FROM #TempDailySlots
        ORDER BY NEWID();
        
        -- Lấy dịch vụ tương ứng của khoa bác sĩ
        SELECT TOP 1 @SelectedServiceId = ms.id 
        FROM medical_services ms 
        WHERE ms.department_id = (SELECT department_id FROM users WHERE id = @SelectedDoctorId)
        ORDER BY NEWID();
        
        -- Chọn bệnh nhân ngẫu nhiên
        DECLARE @SelectedPatientId BIGINT;
        SELECT TOP 1 @SelectedPatientId = id FROM users WHERE username LIKE 'patient.%' ORDER BY NEWID();
        
        -- Quyết định Status
        DECLARE @ApptStatus VARCHAR(20);
        DECLARE @Rand INT = ABS(CHECKSUM(NEWID())) % 100;
        
        IF @DayOffset < 0
        BEGIN
            -- Quá khứ: COMPLETED (70%), CANCELLED (20%), NO_SHOW (10%)
            IF @Rand < 70 SET @ApptStatus = 'COMPLETED';
            ELSE IF @Rand < 90 SET @ApptStatus = 'CANCELLED';
            ELSE SET @ApptStatus = 'NO_SHOW';
        END
        ELSE IF @DayOffset = 0
        BEGIN
            -- Hôm nay: CONFIRMED (20%), WAITING (20%), EXAMINING (20%), COMPLETED (30%), CANCELLED (10%)
            IF @Rand < 20 SET @ApptStatus = 'CONFIRMED';
            ELSE IF @Rand < 40 SET @ApptStatus = 'WAITING';
            ELSE IF @Rand < 60 SET @ApptStatus = 'EXAMINING';
            ELSE IF @Rand < 90 SET @ApptStatus = 'COMPLETED';
            ELSE SET @ApptStatus = 'CANCELLED';
        END
        ELSE
        BEGIN
            -- Tương lai: CONFIRMED (85%), CANCELLED (15%)
            IF @Rand < 85 SET @ApptStatus = 'CONFIRMED';
            ELSE SET @ApptStatus = 'CANCELLED';
        END
        
        -- Phát sinh mã Appointment
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
        
        -- Insert Appointment
        INSERT INTO appointments (appointment_code, patient_id, doctor_id, service_id, slot_id, booking_date, check_in_time, status, note)
        VALUES (@ApptCode, @SelectedPatientId, @SelectedDoctorId, @SelectedServiceId, @SelectedSlotId, @TargetDate, @CheckInTime, @ApptStatus, N'Khám định kỳ tự động sinh dữ liệu test');
        
        DECLARE @NewApptId BIGINT = SCOPE_IDENTITY();
        
        -- Cập nhật Slot
        IF @ApptStatus <> 'CANCELLED'
        BEGIN
            UPDATE time_slots SET booked_capacity = booked_capacity + 1 WHERE id = @SelectedSlotId;
        END
        
        -- [13 & 14 & 15] Tạo Bệnh án và Hóa đơn nếu trạng thái là COMPLETED
        IF @ApptStatus = 'COMPLETED'
        BEGIN
            INSERT INTO medical_records (appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion, prescription_text, heart_rate, blood_pressure, blood_glucose, weight, status, created_by)
            VALUES (@NewApptId, @SelectedPatientId, @SelectedDoctorId, N'Đau đầu nhẹ và mỏi cơ', N'Theo dõi sức khỏe tổng quát (Z00)', N'Chế độ ăn hợp lý, tái khám khi có bất thường', N'1. Vitamin C 500mg x 1 viên/ngày', 80, '120/80', 5.0, 60.0, 'FINALIZED', @SelectedDoctorId);
            
            DECLARE @NewMedRecId BIGINT = SCOPE_IDENTITY();
            DECLARE @SvcPrice DECIMAL(12,2) = (SELECT reference_price FROM medical_services WHERE id = @SelectedServiceId);
            
            -- Trạng thái Invoice: PAID (80%), UNPAID (20%)
            DECLARE @InvStatus VARCHAR(20) = CASE WHEN ABS(CHECKSUM(NEWID())) % 100 < 80 THEN 'PAID' ELSE 'UNPAID' END;
            DECLARE @PaidAt DATETIME2 = CASE WHEN @InvStatus = 'PAID' THEN @TargetDate ELSE NULL END;
            DECLARE @InvCode VARCHAR(20) = 'INV-' + FORMAT(GETDATE(), 'yyyyMM') + RIGHT('0000' + CAST(@ApptIndex AS VARCHAR), 4);
            
            -- [v9] Chèn appointment_id trực tiếp thay cho medical_record_id
            INSERT INTO invoices (invoice_code, appointment_id, total_amount, payment_method, payment_status, paid_at)
            VALUES (@InvCode, @NewApptId, @SvcPrice, CASE WHEN @InvStatus = 'PAID' THEN 'CASH' ELSE 'PENDING' END, @InvStatus, @PaidAt);
            
            DECLARE @NewInvId BIGINT = SCOPE_IDENTITY();
            
            -- [v9] medical_service_order_id là NULL cho dịch vụ khám ban đầu
            INSERT INTO invoice_items (invoice_id, service_id, medical_service_order_id, item_name, price_applied, quantity, line_total)
            VALUES (@NewInvId, @SelectedServiceId, NULL, (SELECT name FROM medical_services WHERE id = @SelectedServiceId), @SvcPrice, 1, @SvcPrice);
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
    
    DROP TABLE #TempDailySlots;
    COMMIT TRAN;
    
    SET @DayOffset = @DayOffset + 1;
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
