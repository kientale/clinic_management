USE clinic_management;
-- 10. Nhật ký hệ thống
CREATE TABLE system_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_code VARCHAR(50),
    action TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_code) REFERENCES account(code)
); 

CREATE TABLE account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,        -- Liên kết với profile
    role ENUM('Admin', 'Doctor', 'Receptionist') NOT NULL
);

-- 3. Hồ sơ người dùng
CREATE TABLE profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_code VARCHAR(50) UNIQUE,
    name VARCHAR(100),
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address VARCHAR(255),
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    citizen_id VARCHAR(20) NOT NULL,
    profile_image VARCHAR(255), -- Đường dẫn ảnh
    FOREIGN KEY (user_code) REFERENCES account(code)
);

CREATE TABLE patient_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,   
    name VARCHAR(100) NOT NULL,                
    email VARCHAR(100),                        
    phone_number VARCHAR(20),                  
    address VARCHAR(255),                     
    date_of_birth DATE,                        
    gender ENUM('Male', 'Female', 'Other'),    
    citizen_id VARCHAR(20) NOT NULL,           
    profile_image VARCHAR(255) DEFAULT '/images/for_avatar/avt_default.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE exam_queue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_code VARCHAR(50) NOT NULL,              
    doctor_code VARCHAR(50) NOT NULL,        
    queue_number INT NOT NULL,                 
    status ENUM('WAITING', 'DONE', 'CANCELLED') DEFAULT 'WAITING',
    created_by VARCHAR(50) NOT NULL,                  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_code VARCHAR(50) NOT NULL,
    doctor_code VARCHAR(50) NOT NULL,
    scheduled_date DATE NOT NULL,
    note TEXT,
    status ENUM('SCHEDULED','CANCELLED','CHECKED-IN') DEFAULT 'SCHEDULED',
    created_by VARCHAR(50) NOT NULL,                  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE appointment 
MODIFY COLUMN created_at DATE;

-- 7. Kết quả khám bệnh
CREATE TABLE medical_result (
    id BIGINT PRIMARY KEY,
    exam_queue_id BIGINT NOT NULL,
    patient_code VARCHAR(50) NOT NULL,                 -- Mã bệnh nhân trực tiếp
    doctor_code VARCHAR(50) NOT NULL,                    -- Bác sĩ khám
    examination_date DATE,
    symptoms TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    FOREIGN KEY (exam_queue_id) REFERENCES exam_queue(id)
);

-- 8. Đơn thuốc
CREATE TABLE prescription (
    id BIGINT PRIMARY KEY,
    medical_result_id BIGINT NOT NULL,
    patient_code VARCHAR(50) NOT NULL,
    prescription_date DATE,
    total_price DECIMAL(12, 2),
    FOREIGN KEY (medical_result_id) REFERENCES medical_result(id)
);

select * from prescription;

-- 9. Chi tiết đơn thuốc
CREATE TABLE prescription_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prescription_id BIGINT NOT NULL,
    medicine_code VARCHAR(50) NOT NULL,
    medicine_name VARCHAR(100) NOT NULL,
    dosage VARCHAR(100),
    quantity INT,
    unit_price DECIMAL(10, 2),
    total_price DECIMAL(12, 2),
    usage_instructions TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescription(id)
);

INSERT INTO account (username, password_hash, code, role) VALUES
('admin1', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U001', 'Admin'),
('doctor1', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U002', 'Doctor'),
('reception1', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U003', 'Receptionist'),
('doctor2', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U004', 'Doctor'),
('admin2', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U005', 'Admin'),
('reception2', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U006', 'Receptionist'),
('doctor3', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U007', 'Doctor'),
('admin3', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U008', 'Admin'),
('reception3', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U009', 'Receptionist'),
('doctor4', '$2a$12$pXiJCHaccaT2GldFYloQKuFTb00Y8EM80YyfoYjAakX3LJ.yRYsaO', 'U010', 'Doctor');

SET SQL_SAFE_UPDATES = 0;
delete from account;


-- Profile người dùng
INSERT INTO profile (
    user_code, name, email, phone_number, address,
    date_of_birth, gender, citizen_id, profile_image
) VALUES
('U001', 'Nguyễn Văn An', 'admin1@example.com', '0909123456', 'Hà Nội', '1990-01-01', 'Male', '012345678901', '/images/for_avatar/avt_1.png'),
('U002', 'Trần Thị Bình', 'doctor1@example.com', '0912345678', 'TP. Hồ Chí Minh', '1985-02-15', 'Female', '023456789012', '/images/for_avatar/avt_2.png'),
('U003', 'Lê Văn Cường', 'reception1@example.com', '0923456789', 'Đà Nẵng', '1992-03-20', 'Male', '034567890123', '/images/for_avatar/avt_3.png'),
('U004', 'Phạm Thị Dung', 'doctor2@example.com', '0934567890', 'Hải Phòng', '1988-04-25', 'Female', '045678901234', '/images/for_avatar/avt_4.png'),
('U005', 'Vũ Văn Duy', 'admin2@example.com', '0945678901', 'Cần Thơ', '1991-05-10', 'Male', '056789012345', '/images/for_avatar/avt_5.png'),
('U006', 'Ngô Thị Hạnh', 'reception2@example.com', '0956789012', 'Huế', '1993-06-30', 'Female', '067890123456', '/images/for_avatar/avt_6.png'),
('U007', 'Đinh Văn Giang', 'doctor3@example.com', '0967890123', 'Nghệ An', '1987-07-12', 'Male', '078901234567', '/images/for_avatar/avt_7.png'),
('U008', 'Bùi Thị Hồng', 'admin3@example.com', '0978901234', 'Quảng Ninh', '1994-08-18', 'Female', '089012345678', '/images/for_avatar/avt_8.png'),
('U009', 'Hoàng Văn Ích', 'reception3@example.com', '0989012345', 'Lâm Đồng', '1990-09-09', 'Male', '090123456789', '/images/for_avatar/avt_default.png'),
('U010', 'Đoàn Thị Lan', 'doctor4@example.com', '0990123456', 'Bình Dương', '1989-10-22', 'Female', '101234567890', '/images/for_avatar/avt_default.png');

SET SQL_SAFE_UPDATES = 0;
delete from profile;

INSERT INTO patient_profile 
(code, name, email, phone_number, address, date_of_birth, gender, citizen_id, profile_image) 
VALUES
('P016', 'Nguyễn Văn P', 'vanp@example.com', '0901111111', 'Hà Nội', '1985-02-14', 'Male', '211111111111', '/images/for_avatar/avt_patient.png'),
('P017', 'Trần Thị Q', 'thiq@example.com', '0912222222', 'TP.HCM', '1991-04-25', 'Female', '222222222222', '/images/for_avatar/avt_patient.png'),
('P018', 'Lê Văn R', 'vanr@example.com', '0923333333', 'Đà Nẵng', '1987-06-10', 'Male', '233333333333', '/images/for_avatar/avt_patient.png'),
('P019', 'Phạm Thị S', 'this@example.com', '0934444444', 'Hải Phòng', '1993-08-21', 'Female', '244444444444', '/images/for_avatar/avt_patient.png'),
('P020', 'Vũ Văn T', 'vant@example.com', '0945555555', 'Cần Thơ', '1981-10-05', 'Male', '255555555555', '/images/for_avatar/avt_patient.png'),
('P021', 'Ngô Thị U', 'thiu@example.com', '0956666666', 'Huế', '1996-12-18', 'Female', '266666666666', '/images/for_avatar/avt_patient.png'),
('P022', 'Đinh Văn V', 'vanv@example.com', '0967777777', 'Nghệ An', '1989-03-09', 'Male', '277777777777', '/images/for_avatar/avt_patient.png'),
('P023', 'Bùi Thị W', 'thiw@example.com', '0978888888', 'Quảng Ninh', '1994-05-23', 'Female', '288888888888', '/images/for_avatar/avt_patient.png'),
('P024', 'Hoàng Văn X', 'vanx@example.com', '0989999999', 'Lâm Đồng', '1992-07-14', 'Male', '299999999999', '/images/for_avatar/avt_patient.png'),
('P025', 'Đoàn Thị Y', 'thiy@example.com', '0990000000', 'Bình Dương', '1988-09-01', 'Female', '300000000000', '/images/for_avatar/avt_patient.png'),
('P026', 'Phan Văn Z', 'vanz@example.com', '0909876543', 'Thanh Hóa', '1983-11-30', 'Male', '311111111111', '/images/for_avatar/avt_patient.png'),
('P027', 'Mai Thị AA', 'thiaa@example.com', '0918765432', 'Nam Định', '1995-01-05', 'Female', '322222222222', '/images/for_avatar/avt_patient.png'),
('P028', 'Tô Văn BB', 'vanbb@example.com', '0927654321', 'Bắc Giang', '1986-04-12', 'Male', '333333333333', '/images/for_avatar/avt_patient.png'),
('P029', 'Nguyễn Thị CC', 'thicc@example.com', '0936543210', 'Khánh Hòa', '1993-06-20', 'Female', '344444444444', '/images/for_avatar/avt_patient.png'),
('P030', 'Trần Văn DD', 'vandd@example.com', '0945432109', 'Phú Thọ', '1982-08-29', 'Male', '355555555555', '/images/for_avatar/avt_patient.png'),
('P031', 'Đỗ Thị EE', 'thiee@example.com', '0954321098', 'Bắc Ninh', '1991-10-11', 'Female', '366666666666', '/images/for_avatar/avt_patient.png'),
('P032', 'Nguyễn Văn FF', 'vanff@example.com', '0963210987', 'Hưng Yên', '1987-12-24', 'Male', '377777777777', '/images/for_avatar/avt_patient.png'),
('P033', 'Trịnh Thị GG', 'thigg@example.com', '0972109876', 'Hà Tĩnh', '1994-02-17', 'Female', '388888888888', '/images/for_avatar/avt_patient.png'),
('P034', 'Phạm Văn HH', 'vanhh@example.com', '0981098765', 'Bình Định', '1985-04-04', 'Male', '399999999999', '/images/for_avatar/avt_patient.png'),
('P035', 'Lê Thị II', 'thiii@example.com', '0999988776', 'Long An', '1992-06-06', 'Female', '400000000000', '/images/for_avatar/avt_patient.png');

SET SQL_SAFE_UPDATES = 0;
DELETE FROM patient_profile;

-- 4. Thuốc
CREATE TABLE medicine (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100),
    description VARCHAR(255),
    unit VARCHAR(50),
    price DECIMAL(10,2),
    max_age INT,
    min_age INT,
    quantity INT
);

INSERT INTO medicine (code,name, description, unit, price, max_age, min_age, quantity) VALUES
('MED001','Paracetamol 500mg', 'Thuốc hạ sốt, giảm đau', 'viên', 1500.00, 65, 6, 500),
('MED002','Amoxicillin 250mg', 'Kháng sinh điều trị nhiễm khuẩn', 'viên', 2500.00, 60, 1, 300),
('MED003','Vitamin C 500mg', 'Tăng đề kháng, phòng cảm cúm', 'viên', 1800.00, 99, 5, 800),
('MED004','Salonpas', 'Miếng dán giảm đau cơ, đau lưng', 'miếng', 3000.00, 70, 12, 200),
('MED005','Telfast 60mg', 'Chống dị ứng, ngứa', 'viên', 5200.00, 65, 12, 150),
('MED006','Decolgen', 'Giảm triệu chứng cảm lạnh', 'viên', 1700.00, 60, 12, 400),
('MED007','Obimin', 'Bổ sung vitamin cho phụ nữ mang thai', 'viên', 4800.00, 45, 18, 120),
('MED008','Panadol Extra', 'Giảm đau, hạ sốt có chứa caffeine', 'viên', 2200.00, 65, 12, 350),
('MED009','Acemuc 200mg', 'Tiêu đờm, điều trị ho có đờm', 'gói', 2100.00, 65, 6, 280),
('MED0010','Smecta', 'Chống tiêu chảy, bảo vệ niêm mạc ruột', 'gói', 2300.00, 60, 1, 600);


