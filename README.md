```markdown
# Thông Tin Dự Án

## Mô Tả Cơ Bản

Dự án này xây dựng một hệ thống thu thập, xử lý và trực quan hóa dữ liệu điểm thi THPT quốc gia năm 2024 từ nhiều nguồn báo điện tử khác nhau. Hệ thống bao gồm các thành phần chính:

- **Crawler Services**: Các dịch vụ thu thập dữ liệu từ các trang báo điện tử.
- **Apache Kafka**: Hệ thống message queue để truyền tải dữ liệu giữa các dịch vụ.
- **Kafka Consumer**: Ứng dụng nhận dữ liệu từ Kafka và lưu vào cơ sở dữ liệu.
- **PostgreSQL**: Cơ sở dữ liệu để lưu trữ dữ liệu điểm thi.
- **Visualization Service**: Dịch vụ trực quan hóa dữ liệu, cung cấp API và giao diện để xem biểu đồ.

## Thành Phần Chi Tiết

### 1. Crawler Services

Các crawler được viết bằng Java, sử dụng Maven. Mỗi crawler thu thập dữ liệu từ một nguồn cụ thể:

- `crawler_tuoitrethudo`
- `crawler_congthuong`
- `crawler_dantri`
- `crawler_vnexpress`
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

## Hướng Dẫn Chạy Dự Án

### Yêu Cầu Hệ Thống

- **Docker** và **Docker Compose**
- **Maven** để build các project Java
- **Python 3.x** và các thư viện cần thiết (được cài đặt qua `requirements.txt`)

### Các Bước Thực Hiện

1. **Build Các Project Con:**

   Trong thư mục gốc của dự án, chạy lệnh sau để build tất cả các project con:

   ```bash
   build-all.bat
   ```

   Nếu chỉ muốn build một dự án con, vào thư mục dự án con và chạy lệnh sau:

   ```bash
   mvn clean install -DskipTests
   ```

2. **Khởi Động Các Dịch Vụ Bằng Docker Compose:**

   ```bash
   docker-compose up --build -d
   ```

   **Lưu ý:**
   
   - Khi khởi tạo Database, dữ liệu đã được crawl trước đó sẽ tự động được insert.
   - Nếu muốn chạy lại từ đầu với DB trống, vui lòng bỏ tùy chọn volume trong service `db` trong file `docker-compose.yml`.

3. **Kiểm Tra Các Container Đang Chạy:**

   ```bash
   docker-compose ps
   ```

4. **Truy Cập Dịch Vụ Visualization:**

   Mở trình duyệt và truy cập vào [http://localhost:5000](http://localhost:5000) để sử dụng dịch vụ trực quan hóa.

   **API Documentation:** Truy cập vào file `APIDoc.readme` của dự án.

5. **Sử Dụng API của Kafka Consumer:**

   Truy cập vào [http://localhost:8080](http://localhost:8080) để tương tác với API của Kafka Consumer.

   **API Documentation:** Truy cập vào file `APIDoc.readme` của dự án.

## Liên Hệ

Nếu có thắc mắc hoặc cần hỗ trợ, vui lòng liên hệ đến email: [nguyenngoctam0332003@gmail.com](mailto:nguyenngoctam0332003@gmail.com).

---

*Lưu ý: Thông tin trong file README này mang tính chất tổng quan. Vui lòng tham khảo mã nguồn và tài liệu chi tiết trong từng thư mục con để biết thêm chi tiết về cách cấu hình và sử dụng.*
```