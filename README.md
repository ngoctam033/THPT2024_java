# Thông tin dự án

## Mô tả cơ bản

Dự án này xây dựng một hệ thống thu thập, xử lý và trực quan hóa dữ liệu điểm thi THPT quốc gia năm 2024 từ nhiều nguồn báo điện tử khác nhau. Hệ thống bao gồm các thành phần chính:

- **Crawler Services**: Các dịch vụ thu thập dữ liệu từ các trang báo điện tử.
- **Apache Kafka**: Hệ thống message queue để truyền tải dữ liệu giữa các dịch vụ.
- **Kafka Consumer**: Ứng dụng nhận dữ liệu từ Kafka và lưu vào cơ sở dữ liệu.
- **PostgreSQL**: Cơ sở dữ liệu để lưu trữ dữ liệu điểm thi.
- **Visualization Service**: Dịch vụ trực quan hóa dữ liệu, cung cấp API và giao diện để xem biểu đồ.

## Thành phần chi tiết

### 1. Crawler Services

Các crawler được viết bằng Java, sử dụng Maven, mỗi crawler thu thập dữ liệu từ một nguồn cụ thể:

- 

crawler_tuoitrethudo


- 

crawler_congthuong


- 

crawler_dantri


- 

crawler_vnexpress


- ...

Các crawler sẽ lấy dữ liệu điểm thi và gửi tới Kafka topic `thpt_2024`.

### 2. Apache Kafka và Zookeeper

Sử dụng Kafka làm hệ thống message queue và Zookeeper để quản lý. Được cấu hình và chạy bằng Docker Compose.

### 3. Kafka Consumer

Ứng dụng Java sử dụng Spring Boot để nhận dữ liệu từ Kafka và lưu vào PostgreSQL.

### 4. PostgreSQL

Cơ sở dữ liệu quan hệ để lưu trữ dữ liệu điểm thi thu thập được.

### 5. Visualization Service

Ứng dụng Flask (Python) sử dụng Plotly để tạo biểu đồ. Cung cấp API để hiển thị thống kê và biểu đồ liên quan đến điểm thi.

## Hướng dẫn chạy dự án

### Yêu cầu hệ thống

- **Docker** và **Docker Compose**
- **Maven** để build các project Java
- **Python 3.x** và các thư viện cần thiết (được cài đặt qua `requirements.txt`)

### Các bước thực hiện

1. **Build các project con:**

   Trong thư mục gốc của dự án, chạy lệnh sau để build tất cả các project con:

   ```bash
   mvn clean install
   ```

2. **Khởi động các dịch vụ bằng Docker Compose:**

   ```bash
   docker-compose up --build -d
   ```

3. **Kiểm tra các container đang chạy:**

   ```bash
   docker-compose ps
   ```

4. **Truy cập dịch vụ Visualization:**

   Mở trình duyệt và truy cập vào 

http://localhost:5000

 để sử dụng dịch vụ trực quan hóa.

5. **Sử dụng API của Kafka Consumer:**

   Truy cập 

http://localhost:8080

 để tương tác với API của Kafka Consumer.

## Cấu trúc thư mục

- 

crawler_tuoitrethudo


- 

crawler_congthuong


- 

crawler_dantri


- 

crawler_vnexpress


- 

kafka_consumer


- 

visualization_service


- 

docker-compose.yml

 (file cấu hình Docker Compose)

## Liên hệ

Nếu có thắc mắc hoặc cần hỗ trợ, vui lòng liên hệ nhóm phát triển dự án.

---

*Lưu ý: Thông tin trong file README này mang tính chất tổng quan. Vui lòng tham khảo mã nguồn và tài liệu chi tiết trong từng thư mục con để biết thêm chi tiết về cách cấu hình và sử dụng.*