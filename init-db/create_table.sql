-- File: create_table.sql

CREATE TABLE student_scores (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL UNIQUE,
    toan REAL,
    ngu_van REAL,
    ngoai_ngu REAL,
    vat_ly REAL,
    hoa_hoc REAL,
    sinh_hoc REAL,
    lich_su REAL,
    dia_ly REAL,
    gdcd REAL
);

-- Chèn dữ liệu từ file CSV vào bảng
COPY student_scores (student_id,toan,ngu_van,ngoai_ngu,vat_ly,hoa_hoc,sinh_hoc,lich_su,dia_ly,gdcd)
FROM '/docker-entrypoint-initdb.d/data.csv'
DELIMITER ','
CSV HEADER;