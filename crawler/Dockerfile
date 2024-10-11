# Sử dụng image chính thức của OpenJDK làm base image
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép tệp JAR vào thư mục làm việc
COPY target/crawler-1.0-SNAPSHOT.jar /app/crawler.jar

# Chỉ định lệnh để chạy chương trình Java
CMD ["java", "-jar", "crawler.jar"]