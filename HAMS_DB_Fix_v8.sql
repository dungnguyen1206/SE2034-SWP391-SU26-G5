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
-- 9. week_schedules
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
-- 10. doctor_schedules
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
-- 11. time_slots
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
    status              VARCHAR(20)     NOT NULL        DEFAULT 'CONFIRMED',
    created_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),
    updated_at          DATETIME2       NOT NULL        DEFAULT GETDATE(),

    FOREIGN KEY (patient_id)  REFERENCES users(id),
    FOREIGN KEY (doctor_id)   REFERENCES users(id),
    FOREIGN KEY (service_id)  REFERENCES medical_services(id),
    FOREIGN KEY (slot_id)     REFERENCES time_slots(id)
);
GO

-- ============================================================
-- 13. medical_records
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
-- 14. invoices
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

