// FILE: Subject.java
package com.example.demo.enums;

public enum Subject {
    TOAN("Toán", "toan"),
    VAN("Văn", "ngu_van"),
    ANH("Anh", "ngoai_ngu"),
    LY("Lý", "vat_ly"),
    HOA("Hóa", "hoa_hoc"),
    SINH("Sinh", "sinh_hoc"),
    SU("Sử", "lich_su"),
    DIA("Địa", "dia_ly"),
    GDCD("GDCD", "gdcd");

    private final String displayName;
    private final String columnName;

    Subject(String displayName, String columnName) {
        this.displayName = displayName;
        this.columnName = columnName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColumnName() {
        return columnName;
    }

    /**
     * Lấy enum Subject từ tên môn học (getColumnName), không phân biệt chữ hoa chữ thường.
     *
     * @param subject Tên môn học.
     * @return Enum Subject tương ứng.
     */
    public static Subject fromString(String subject) {
        for (Subject s : Subject.values()) {
            if (s.getColumnName().equalsIgnoreCase(subject.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Môn học không hợp lệ: " + subject);
    }
}