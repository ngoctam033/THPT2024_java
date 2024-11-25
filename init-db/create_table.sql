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
COPY student_scores (id,ngoai_ngu,dia_ly,gdcd,hoa_hoc,vat_ly,sinh_hoc,student_id,lich_su,toan,ngu_van)
FROM '/docker-entrypoint-initdb.d/data.csv'
DELIMITER ','
CSV HEADER;