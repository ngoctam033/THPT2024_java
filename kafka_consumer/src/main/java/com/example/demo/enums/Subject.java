// FILE: Subject.java
package com.example.demo.enums;

public enum Subject {
    TOAN("toan"),
    VAN("van"),
    ANH("anh"),
    LY("ly"),
    HOA("hoa"),
    SINH("sinh"),
    SU("su"),
    DIA("dia"),
    GDCD("gdcd");

    private final String columnName;

    Subject(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    /**
     * Lấy enum Subject từ tên môn học, không phân biệt chữ hoa chữ thường.
     *
     * @param subject Tên môn học.
     * @return Enum Subject tương ứng.
     */
    public static Subject fromString(String subject) {
        try {
            return Subject.valueOf(subject.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Môn học không hợp lệ: " + subject);
        }
    }
}