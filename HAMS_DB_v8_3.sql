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
    experience_years    INT             NOT NULL        DEFAULT 0,
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
-- 9. week_schedules  [MỚI - v8.2, giữ nguyên v8.3]
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
-- 10. doctor_schedules  [CẬP NHẬT - v8.3]
-- Thêm FK week_schedule_id để gắn shift vào tuần
-- Thêm note (max 256 ký tự) cho Manager ghi chú khi tạo shift
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
    note                NVARCHAR(256)   NULL,               -- [MỚI v8.3] Ghi chú của Manager
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
-- 11. time_slots
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
-- 12. appointments
-- status: PENDING / CONFIRMED / COMPLETED / CANCELLED
--   PENDING   : Bệnh nhân vừa đặt, chờ xác nhận
--   CONFIRMED : Đã xác nhận lịch hẹn
--   COMPLETED : Đã khám xong
--   CANCELLED : Đã hủy (bởi patient hoặc do shift bị cancel)
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
-- 13. medical_records (Quan hệ 1-1 STRICT với Appointment)
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
-- 14. invoices (Quan hệ 1-1 STRICT với Medical Record)
-- ============================================================
CREATE TABLE invoices (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    invoice_code        VARCHAR(20)     NOT NULL,
    medical_record_id   BIGINT          NOT NULL        UNIQUE,
    total_amount        DECIMAL(12,2)   NOT NULL        DEFAULT 0.00,
    payment_method      VARCHAR(20)     NOT NULL        DEFAULT 'PENDING',
    payment_status      VARCHAR(20)     NOT NULL        DEFAULT 'UNPAID',
    paid_at             DATETIME2       NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
);
GO

-- ============================================================
-- 15. invoice_items
-- ============================================================
CREATE TABLE invoice_items (
    id              BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    invoice_id      BIGINT          NOT NULL,
    service_id      BIGINT          NOT NULL,
    item_name       NVARCHAR(150)   NOT NULL,
    price_applied   DECIMAL(12,2)   NOT NULL,
    quantity        INT             NOT NULL        DEFAULT 1,
    line_total      DECIMAL(12,2)   NOT NULL,

    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (service_id) REFERENCES medical_services(id)
);
GO

-- ============================================================
-- 16. articles
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

-- ============================================================
-- 17. news
-- ============================================================
CREATE TABLE news (
    id           BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    title        NVARCHAR(200)   NOT NULL,
    slug         VARCHAR(250)    NOT NULL,
    content      NVARCHAR(MAX)   NOT NULL,
    image_url    VARCHAR(500)    NULL,
    type         VARCHAR(20)     NOT NULL        DEFAULT 'NEWS',
    status       VARCHAR(20)     NOT NULL        DEFAULT 'DRAFT',
    created_by   BIGINT          NOT NULL,
    published_at DATETIME2       NULL,
    created_at   DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at   DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (created_by) REFERENCES users(id)
);
GO

-- ============================================================
-- 18. email_logs
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
-- QUICK VERIFY
-- ============================================================
SELECT [table], [rows] FROM (
    SELECT 'roles'              AS [table], COUNT(*) AS [rows] FROM roles
    UNION ALL SELECT 'users'                , COUNT(*) FROM users
    UNION ALL SELECT 'user_roles'           , COUNT(*) FROM user_roles
    UNION ALL SELECT 'user_addresses'       , COUNT(*) FROM user_addresses
    UNION ALL SELECT 'departments'          , COUNT(*) FROM departments
    UNION ALL SELECT 'rooms'               , COUNT(*) FROM rooms
    UNION ALL SELECT 'provinces'           , COUNT(*) FROM provinces
    UNION ALL SELECT 'medical_services'    , COUNT(*) FROM medical_services
    UNION ALL SELECT 'week_schedules'      , COUNT(*) FROM week_schedules
    UNION ALL SELECT 'doctor_schedules'    , COUNT(*) FROM doctor_schedules
    UNION ALL SELECT 'time_slots'          , COUNT(*) FROM time_slots
    UNION ALL SELECT 'appointments'        , COUNT(*) FROM appointments
    UNION ALL SELECT 'medical_records'     , COUNT(*) FROM medical_records
    UNION ALL SELECT 'invoices'            , COUNT(*) FROM invoices
    UNION ALL SELECT 'invoice_items'       , COUNT(*) FROM invoice_items
    UNION ALL SELECT 'articles'            , COUNT(*) FROM articles
    UNION ALL SELECT 'news'               , COUNT(*) FROM news
    UNION ALL SELECT 'email_logs'         , COUNT(*) FROM email_logs
    UNION ALL SELECT 'notifications'      , COUNT(*) FROM notifications
) t ORDER BY [table];
GO

-- ============================================================
-- SEED DATA: roles
-- ============================================================
INSERT INTO roles (name, description) VALUES
('ADMIN',        N'Quản trị hệ thống'),
('MANAGER',      N'Quản lý bệnh viện'),
('DOCTOR',       N'Bác sĩ'),
('PATIENT',      N'Bệnh nhân'),
('RECEPTIONIST', N'Lễ tân');
GO

-- ============================================================
-- SEED DATA: users  (password cho tất cả: 123456)
-- ============================================================
INSERT INTO users
(username, email, password_hash, status, email_verified, first_name, last_name, gender, experience_years)
VALUES
('admin',    'admin@hams.com',    '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'System',   N'Admin',   'MALE',   0),
('manager1', 'manager1@hams.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hospital', N'Manager', 'MALE',   0),
('doctor1',  'doctor1@hams.com',  '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Nguyen',   N'An',      'MALE',   5),
('doctor2',  'doctor2@hams.com',  '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Tran',     N'Binh',    'FEMALE', 8),
('patient1', 'patient1@hams.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Le',       N'Cuong',   'MALE',   0),
('patient2', 'patient2@hams.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Pham',     N'Dung',    'FEMALE', 0),
('patient3', 'patient3@hams.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Hoang',    N'Giang',   'MALE',   0),
('patient4', 'patient4@hams.com', '$2a$10$cyfYZQXK42uxMzPJL3eicOLBcgFVSgNoyKghJ0yKtHydyn2qRBqoK', 'ACTIVE', 1, N'Vu',       N'Huong',   'FEMALE', 0);
GO

-- ============================================================
-- SEED DATA: user_roles  (gán role cho từng user)
-- ============================================================
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'admin'    AND r.name = 'ADMIN';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'manager1' AND r.name = 'MANAGER';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'doctor1'  AND r.name = 'DOCTOR';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'doctor2'  AND r.name = 'DOCTOR';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'patient1' AND r.name = 'PATIENT';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'patient2' AND r.name = 'PATIENT';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'patient3' AND r.name = 'PATIENT';
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r WHERE u.username = 'patient4' AND r.name = 'PATIENT';
GO

-- ============================================================
-- VERIFY SEED DATA
-- ============================================================
SELECT u.username, r.name AS role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r       ON r.id = ur.role_id
ORDER BY u.id;
GO
