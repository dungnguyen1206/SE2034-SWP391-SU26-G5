-- ============================================================
--  HOSPITAL APPOINTMENT & MEDICAL RECORD MANAGEMENT SYSTEM
--  Database Design Document - DDL Script
--  Phiên bản: 5.2 (Revised from v5.1)
--  Hệ quản trị CSDL: Microsoft SQL Server 2016+
--  ORM: Spring Data JPA / Hibernate
--
--  Changelog từ v4.4 → v5.0:
--  [1] doctor_schedules  : Thêm created_by, đổi shift EVENING → FULL_DAY
--  [2] doctors           : Thêm ON_LEAVE vào status, license_number, avatar
--  [3] articles          : Thêm category, summary, view_count; tách author_id
--  [4] invoices          : Thêm PENDING vào payment_status
--  [5] receptionists     : Thêm created_at, updated_at
--  [6] users             : Xóa flow role_change_requests
--  [7] appointments      : Thêm doctor_id trực tiếp
--
--  Changelog từ v5.0 → v5.1:
--  [1] departments       : Thêm image_url (Cloudinary)
--  [2] medical_services  : Thêm image_url (Cloudinary)
--  [3] articles          : Thêm thumbnail_url (Cloudinary)
--
--  Changelog từ v5.1 → v5.2:
--  [01] users            : Thêm email_verified, email_verified_at
--  [02] receptionists    : Thêm status
--  [03] rooms            : Bảng MỚI — quản lý phòng khám theo khoa
--  [04] doctor_schedules : Đổi room (text) → room_id FK, thêm status + updated_at
--  [05] time_slots       : Thêm status
--  [06] medical_services : Thêm created_at, updated_at
--  [07] appointments     : Bỏ created_by, thêm check_in_time
--  [08] medical_records  : Thêm status, updated_by; bỏ prescriptions/details
--                          → gộp vào prescription_text (không quản lý thuốc)
--  [09] invoices         : Thêm invoice_code, bỏ created_by
--  [10] invoice_items    : Thêm item_name, line_total
--  [11] articles         : Thêm slug
--  [12] email_logs       : Thêm subject
--  [13] news             : Bảng MỚI — tin tức/khuyến mãi (gộp NewsPromotions)
--  [14] notifications    : Bảng MỚI — thông báo in-app cho user
-- ============================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'hams_db')
BEGIN
    CREATE DATABASE hams_db;
END;
GO
USE hams_db;
GO

-- ============================================================
-- 1. BẢNG roles
-- ============================================================
CREATE TABLE roles (
    id          INT             IDENTITY(1,1)   PRIMARY KEY,
    name        NVARCHAR(50)    NOT NULL        UNIQUE,
    description NVARCHAR(255)   NULL
);
GO

-- ============================================================
-- 2. BẢNG users
-- [v5.2] Thêm email_verified + email_verified_at
--        Khi Patient tự đăng ký → email_verified = 0, hệ thống
--        gửi link xác thực. Chưa verify thì không đặt lịch được.
--        Doctor/Receptionist do Manager tạo → mặc định verified = 1.
-- ============================================================
CREATE TABLE users (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    username            VARCHAR(50)     NOT NULL        UNIQUE,
    email               VARCHAR(100)    NOT NULL        UNIQUE,
    password_hash       VARCHAR(255)    NOT NULL,
    role_id             INT             NOT NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    email_verified      BIT             NOT NULL        DEFAULT 0,      -- [v5.2] 0=chưa xác thực
    email_verified_at   DATETIME2       NULL,                           -- [v5.2] thời điểm xác thực
    created_by          BIGINT          NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (role_id)    REFERENCES roles(id),
    FOREIGN KEY (created_by) REFERENCES users(id),

    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
GO

-- ============================================================
-- 3. BẢNG departments
-- [v5.1] Thêm image_url (Cloudinary)
-- ============================================================
CREATE TABLE departments (
    id          INT             IDENTITY(1,1)   PRIMARY KEY,
    name        NVARCHAR(100)   NOT NULL        UNIQUE,
    description NVARCHAR(MAX)   NULL,
    image_url   VARCHAR(500)    NULL,
    status      VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',

    CONSTRAINT chk_dept_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
GO

-- ============================================================
-- 4. BẢNG rooms  ← MỚI v5.2
-- Mô tả: Phòng khám thuộc từng khoa.
--        Manager tạo và quản lý. Doctor_schedules liên kết qua room_id.
--        Bệnh nhân và bác sĩ đều biết phòng khi xem lịch.
-- ============================================================
CREATE TABLE rooms (
    id              INT             IDENTITY(1,1)   PRIMARY KEY,
    department_id   INT             NOT NULL,
    name            NVARCHAR(100)   NOT NULL,                       -- vd: Phòng khám Tim mạch 1
    room_number     VARCHAR(20)     NOT NULL,                       -- vd: 101, B205
    status          VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    created_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (department_id) REFERENCES departments(id),

    CONSTRAINT uq_room_number   UNIQUE (room_number),
    CONSTRAINT chk_room_status  CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE'))
);
GO

-- ============================================================
-- 5. BẢNG patients
-- ============================================================
CREATE TABLE patients (
    id              BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    user_id         BIGINT          NOT NULL        UNIQUE,
    name            NVARCHAR(100)   NOT NULL,
    phone           VARCHAR(15)     NOT NULL,
    address         NVARCHAR(255)   NULL,
    gender          VARCHAR(20)     NOT NULL,
    date_of_birth   DATE            NOT NULL,
    avatar          VARCHAR(255)    NULL,
    blood_type      VARCHAR(10)     NULL,
    created_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at      DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_patient_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);
GO

-- ============================================================
-- 6. BẢNG doctors
-- [v5.0] Thêm ON_LEAVE, license_number, avatar, updated_at
-- ============================================================
CREATE TABLE doctors (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    user_id             BIGINT          NOT NULL        UNIQUE,
    department_id       INT             NOT NULL,
    name                NVARCHAR(100)   NOT NULL,
    phone               VARCHAR(15)     NOT NULL,
    gender              VARCHAR(20)     NOT NULL,
    experience_years    INT             NOT NULL        DEFAULT 0,
    degree              NVARCHAR(100)   NOT NULL,
    license_number      VARCHAR(50)     NULL,
    avatar              VARCHAR(255)    NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',
    bio                 NVARCHAR(MAX)   NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (user_id)       REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id),

    CONSTRAINT chk_doctor_gender  CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_doctor_status  CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE')),
    CONSTRAINT chk_exp_years      CHECK (experience_years >= 0)
);
GO

-- ============================================================
-- 7. BẢNG receptionists
-- [v5.0] Thêm created_at, updated_at
-- [v5.2] Thêm status — đồng bộ với soft delete pattern toàn hệ thống
-- ============================================================
CREATE TABLE receptionists (
    id          BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    user_id     BIGINT          NOT NULL        UNIQUE,
    name        NVARCHAR(100)   NOT NULL,
    phone       VARCHAR(15)     NOT NULL,
    gender      VARCHAR(20)     NOT NULL,
    avatar      VARCHAR(255)    NULL,
    status      VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',    -- [v5.2]
    created_at  DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at  DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_receptionist_gender  CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_receptionist_status  CHECK (status IN ('ACTIVE', 'INACTIVE'))  -- [v5.2]
);
GO

-- ============================================================
-- 8. BẢNG medical_services
-- [v5.1] Thêm image_url (Cloudinary)
-- [v5.2] Thêm created_at, updated_at
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
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),   -- [v5.2]
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),   -- [v5.2]

    FOREIGN KEY (department_id) REFERENCES departments(id),

    CONSTRAINT chk_service_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_price          CHECK (reference_price >= 0)
);
GO

-- ============================================================
-- 9. BẢNG doctor_schedules
-- [v5.0] Thêm created_by, đổi EVENING → FULL_DAY
-- [v5.2] Đổi room (NVARCHAR) → room_id FK → rooms(id)
-- [v5.2] Thêm status — Manager có thể cancel 1 ngày trực nếu cần
-- [v5.2] Thêm updated_at
-- ============================================================
CREATE TABLE doctor_schedules (
    id          BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    doctor_id   BIGINT          NOT NULL,
    room_id     INT             NOT NULL,                            -- [v5.2] FK → rooms
    work_date   DATE            NOT NULL,
    shift       VARCHAR(20)     NOT NULL,
    status      VARCHAR(20)     NOT NULL        DEFAULT 'ACTIVE',   -- [v5.2]
    created_by  BIGINT          NULL,
    created_at  DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at  DATETIME2       NOT NULL        DEFAULT GETDATE(),   -- [v5.2]

    FOREIGN KEY (doctor_id)  REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)    REFERENCES rooms(id),                  -- [v5.2]
    FOREIGN KEY (created_by) REFERENCES users(id),

    CONSTRAINT uq_doctor_date       UNIQUE (doctor_id, work_date),
    CONSTRAINT chk_schedule_shift   CHECK (shift IN ('MORNING', 'AFTERNOON', 'FULL_DAY')),
    CONSTRAINT chk_schedule_status  CHECK (status IN ('ACTIVE', 'CANCELLED'))  -- [v5.2]
);
GO

-- ============================================================
-- 10. BẢNG time_slots
-- [v5.2] Thêm status — slot có thể bị block (bác sĩ nghỉ đột xuất)
-- ============================================================
CREATE TABLE time_slots (
    id              BIGINT      IDENTITY(1,1)   PRIMARY KEY,
    schedule_id     BIGINT      NOT NULL,
    start_time      TIME        NOT NULL,
    end_time        TIME        NOT NULL,
    booked_capacity INT         NOT NULL        DEFAULT 0,
    max_capacity    INT         NOT NULL        DEFAULT 5,
    status          VARCHAR(20) NOT NULL        DEFAULT 'AVAILABLE',    -- [v5.2]
    version         BIGINT      NOT NULL        DEFAULT 0,              -- Optimistic Locking

    FOREIGN KEY (schedule_id) REFERENCES doctor_schedules(id) ON DELETE CASCADE,

    CONSTRAINT chk_capacity     CHECK (booked_capacity <= max_capacity),
    CONSTRAINT chk_max_capacity CHECK (max_capacity > 0),
    CONSTRAINT chk_slot_status  CHECK (status IN ('AVAILABLE', 'FULL', 'BLOCKED'))  -- [v5.2]
);
GO

-- ============================================================
-- 11. BẢNG appointments
-- [v5.0] Thêm doctor_id trực tiếp
-- [v5.2] Bỏ created_by (không cần track ai tạo lịch hẹn)
-- [v5.2] Thêm check_in_time — thời điểm bệnh nhân check-in tại quầy
-- ============================================================
CREATE TABLE appointments (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    appointment_code    VARCHAR(20)     NOT NULL        UNIQUE,     -- vd: APT-2026052101
    patient_id          BIGINT          NOT NULL,
    doctor_id           BIGINT          NOT NULL,
    service_id          BIGINT          NOT NULL,
    slot_id             BIGINT          NOT NULL,
    booking_date        DATE            NOT NULL,
    check_in_time       DATETIME2       NULL,                       -- [v5.2] NULL cho đến khi check-in
    note                NVARCHAR(MAX)   NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'PENDING',
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (patient_id)  REFERENCES patients(id),
    FOREIGN KEY (doctor_id)   REFERENCES doctors(id),
    FOREIGN KEY (service_id)  REFERENCES medical_services(id),
    FOREIGN KEY (slot_id)     REFERENCES time_slots(id),

    CONSTRAINT chk_appointment_status CHECK (
        status IN (
            'PENDING',          -- Vừa đặt, chờ xác nhận
            'CONFIRMED',        -- Receptionist xác nhận
            'REJECTED',         -- Receptionist từ chối
            'CHECKED_IN',       -- Bệnh nhân đã check-in tại quầy
            'IN_EXAMINATION',   -- Đang khám
            'COMPLETED',        -- Hoàn thành, bệnh án đã tạo
            'CANCELLED',        -- Bệnh nhân/Receptionist hủy
            'NO_SHOW'           -- Không đến, không hủy
        )
    )
);
GO

-- ============================================================
-- 12. BẢNG medical_records
-- [v5.2] Thêm status — bệnh án có thể DRAFT (đang viết) hoặc FINALIZED
-- [v5.2] Thêm updated_by — track Doctor nào chỉnh sửa lần cuối
-- [v5.2] Bỏ FK prescriptions/prescription_details
--        → Gộp vào prescription_text (text đơn giản)
--        Lý do: hệ thống không quản lý kho thuốc, thêm 2 bảng prescriptions
--        chỉ để lưu text là dư thừa.
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
    prescription_text   NVARCHAR(MAX)   NULL,                       -- [v5.2] đơn thuốc dạng text
    notes               NVARCHAR(MAX)   NULL,
    status              VARCHAR(20)     NOT NULL        DEFAULT 'DRAFT', -- [v5.2]
    created_by          BIGINT          NULL,                       -- Doctor tạo
    updated_by          BIGINT          NULL,                       -- [v5.2] Doctor chỉnh sửa cuối
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (patient_id)     REFERENCES patients(id),
    FOREIGN KEY (doctor_id)      REFERENCES doctors(id),
    FOREIGN KEY (created_by)     REFERENCES users(id),
    FOREIGN KEY (updated_by)     REFERENCES users(id),              -- [v5.2]

    CONSTRAINT chk_mr_status CHECK (status IN ('DRAFT', 'FINALIZED'))  -- [v5.2]
);
GO

-- ============================================================
-- 13. BẢNG invoices
-- [v5.0] Thêm PENDING vào payment_status
-- [v5.2] Thêm invoice_code — mã hóa đơn hiển thị cho bệnh nhân (vd: INV-0056)
-- [v5.2] Bỏ created_by — không cần track, invoice luôn do Receptionist tạo
-- ============================================================
CREATE TABLE invoices (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    invoice_code        VARCHAR(20)     NOT NULL        UNIQUE,     -- [v5.2] vd: INV-0056
    medical_record_id   BIGINT          NOT NULL        UNIQUE,     -- Strict 1-1
    total_amount        DECIMAL(12,2)   NOT NULL        DEFAULT 0.00,
    payment_method      VARCHAR(20)     NOT NULL        DEFAULT 'PENDING',
    payment_status      VARCHAR(20)     NOT NULL        DEFAULT 'UNPAID',
    paid_at             DATETIME2       NULL,
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,

    CONSTRAINT chk_invoice_method CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'PENDING')),
    CONSTRAINT chk_invoice_status  CHECK (payment_status IN ('PAID', 'UNPAID', 'PENDING')),
    CONSTRAINT chk_total_amount    CHECK (total_amount >= 0)
);
GO

-- ============================================================
-- 14. BẢNG invoice_items
-- [v5.2] Thêm item_name — snapshot tên dịch vụ tại thời điểm xuất hóa đơn
--        (tránh sai khi Manager đổi tên dịch vụ sau này)
-- [v5.2] Thêm line_total — DECIMAL(12,2), = price_applied * quantity
--        Lưu sẵn để tránh tính toán lại mỗi lần render hóa đơn
-- ============================================================
CREATE TABLE invoice_items (
    id              BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    invoice_id      BIGINT          NOT NULL,
    service_id      BIGINT          NOT NULL,
    item_name       NVARCHAR(150)   NOT NULL,                       -- [v5.2] snapshot tên dịch vụ
    price_applied   DECIMAL(12,2)   NOT NULL,                       -- snapshot giá lúc khám
    quantity        INT             NOT NULL        DEFAULT 1,
    line_total      DECIMAL(12,2)   NOT NULL,                       -- [v5.2] = price_applied * quantity

    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES medical_services(id),

    CONSTRAINT chk_item_price   CHECK (price_applied >= 0),
    CONSTRAINT chk_item_qty     CHECK (quantity > 0),
    CONSTRAINT chk_line_total   CHECK (line_total >= 0)             -- [v5.2]
);
GO

-- ============================================================
-- 15. BẢNG articles
-- [v5.0] Thêm category, summary, view_count; tách doctor_author_id
-- [v5.1] Thêm thumbnail_url (Cloudinary)
-- [v5.2] Thêm slug — URL-friendly identifier (vd: heart-health-5-daily-habits)
--        Dùng cho public URL: /articles/{slug}
-- ============================================================
CREATE TABLE articles (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    title               NVARCHAR(200)   NOT NULL,
    slug                VARCHAR(250)    NOT NULL        UNIQUE,     -- [v5.2] vd: heart-health-5-daily-habits
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

    FOREIGN KEY (doctor_author_id) REFERENCES doctors(id),
    FOREIGN KEY (created_by)       REFERENCES users(id),

    CONSTRAINT chk_article_status CHECK (status IN ('DRAFT', 'PUBLISHED'))
);
GO

-- ============================================================
-- 16. BẢNG news  ← MỚI v5.2
-- Mô tả: Tin tức và khuyến mãi của bệnh viện (NewsPromotions).
--        Tách riêng với articles vì khác nghiệp vụ:
--          articles = bài viết y khoa chuyên sâu, có tác giả Doctor
--          news     = tin tức sự kiện, khuyến mãi, thông báo chung
--        type phân biệt NEWS (tin tức) và PROMOTION (khuyến mãi).
-- ============================================================
CREATE TABLE news (
    id          BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    title       NVARCHAR(200)   NOT NULL,
    slug        VARCHAR(250)    NOT NULL        UNIQUE,             -- URL-friendly
    content     NVARCHAR(MAX)   NOT NULL,
    image_url   VARCHAR(500)    NULL,                               -- Cloudinary URL
    type        VARCHAR(20)     NOT NULL        DEFAULT 'NEWS',     -- NEWS | PROMOTION
    status      VARCHAR(20)     NOT NULL        DEFAULT 'DRAFT',
    created_by  BIGINT          NOT NULL,
    published_at DATETIME2      NULL,
    created_at  DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at  DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (created_by) REFERENCES users(id),

    CONSTRAINT chk_news_type    CHECK (type IN ('NEWS', 'PROMOTION')),
    CONSTRAINT chk_news_status  CHECK (status IN ('DRAFT', 'PUBLISHED'))
);
GO

-- ============================================================
-- 17. BẢNG email_logs
-- [v5.2] Thêm subject — tiêu đề email, hữu ích khi debug/audit
-- ============================================================
CREATE TABLE email_logs (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    email_type          VARCHAR(50)     NOT NULL,
    recipient           VARCHAR(100)    NOT NULL,
    subject             NVARCHAR(200)   NULL,                       -- [v5.2] tiêu đề email
    sent_at             DATETIME2       NOT NULL        DEFAULT GETDATE(),
    status              VARCHAR(20)     NOT NULL,
    error_message       NVARCHAR(MAX)   NULL,
    related_entity_id   BIGINT          NULL,
    related_entity_type VARCHAR(50)     NULL,

    CONSTRAINT chk_email_status CHECK (status IN ('SUCCESS', 'FAILED'))
);
GO

-- ============================================================
-- 18. BẢNG notifications  ← MỚI v5.2
-- Mô tả: Thông báo in-app gửi đến Doctor, Receptionist, Patient.
--        user_id: người nhận thông báo (FK → users)
--        notification_type: phân loại để frontend render icon/màu sắc
--        related_entity_id + related_entity_type: liên kết đến entity
--        cụ thể (appointment, invoice...) để bấm vào redirect đúng trang.
--        is_read: 0 = chưa đọc (hiển thị badge đỏ), 1 = đã đọc
-- ============================================================
CREATE TABLE notifications (
    id                  BIGINT          IDENTITY(1,1)   PRIMARY KEY,
    user_id             BIGINT          NOT NULL,                   -- người nhận
    title               NVARCHAR(200)   NOT NULL,
    message             NVARCHAR(MAX)   NOT NULL,
    notification_type   VARCHAR(50)     NOT NULL,                   -- vd: APPOINTMENT_CONFIRMED
    is_read             BIT             NOT NULL        DEFAULT 0,
    related_entity_id   BIGINT          NULL,                       -- vd: appointment_id
    related_entity_type VARCHAR(50)     NULL,                       -- vd: 'APPOINTMENT'
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_notif_type CHECK (
        notification_type IN (
            'APPOINTMENT_CONFIRMED',    -- Lễ tân xác nhận lịch hẹn     → gửi Patient
            'APPOINTMENT_REJECTED',     -- Lễ tân từ chối lịch hẹn       → gửi Patient
            'APPOINTMENT_CANCELLED',    -- Hủy lịch hẹn                  → gửi Patient + Doctor
            'APPOINTMENT_REMINDER',     -- Nhắc lịch khám ngày mai        → gửi Patient
            'APPOINTMENT_NEW',          -- Có lịch hẹn mới cần xử lý     → gửi Receptionist
            'CHECKIN_DONE',             -- Bệnh nhân đã check-in          → gửi Doctor
            'MEDICAL_RECORD_CREATED',   -- Bệnh án đã được tạo            → gửi Patient
            'INVOICE_CREATED',          -- Hóa đơn sẵn sàng thanh toán   → gửi Patient
            'INVOICE_PAID',             -- Bệnh nhân đã thanh toán        → gửi Receptionist
            'SCHEDULE_ASSIGNED'         -- Được phân công lịch trực       → gửi Doctor
        )
    )
);
GO

-- ============================================================
-- INDEX
-- ============================================================

-- users
CREATE UNIQUE INDEX idx_user_username   ON users(username);
CREATE UNIQUE INDEX idx_user_email      ON users(email);

-- profile tables
CREATE UNIQUE INDEX idx_patient_user    ON patients(user_id);
CREATE UNIQUE INDEX idx_doctor_user     ON doctors(user_id);

-- rooms
CREATE UNIQUE INDEX idx_room_number     ON rooms(room_number);

-- schedule & appointment
CREATE INDEX idx_sched_doc_date         ON doctor_schedules(doctor_id, work_date);
CREATE UNIQUE INDEX idx_app_code        ON appointments(appointment_code);
CREATE INDEX idx_app_patient_status     ON appointments(patient_id, status);
CREATE INDEX idx_app_doctor_date        ON appointments(doctor_id, booking_date);

-- invoices
CREATE UNIQUE INDEX idx_invoice_code    ON invoices(invoice_code);
CREATE INDEX idx_invoice_status         ON invoices(payment_status);

-- articles & news
CREATE UNIQUE INDEX idx_article_slug    ON articles(slug);
CREATE UNIQUE INDEX idx_news_slug       ON news(slug);

-- notifications (query thường: lấy tất cả thông báo chưa đọc của user)
CREATE INDEX idx_notif_user_read        ON notifications(user_id, is_read);
GO

-- ============================================================
-- SEED DATA — Roles
-- ============================================================
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN',          N'Quản trị hệ thống, quản lý user và phân quyền'),
    ('ROLE_MANAGER',        N'Quản lý bệnh viện, tạo tài khoản Doctor/Receptionist, quản lý lịch trực'),
    ('ROLE_DOCTOR',         N'Bác sĩ, khám bệnh và tạo hồ sơ bệnh án'),
    ('ROLE_RECEPTIONIST',   N'Lễ tân, xác nhận lịch hẹn và thu tiền'),
    ('ROLE_PATIENT',        N'Bệnh nhân, tự đăng ký và đặt lịch khám');
GO