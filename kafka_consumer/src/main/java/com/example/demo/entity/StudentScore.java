package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_scores")
public class StudentScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "toan")
    private Float toan;

    @Column(name = "ngu_van")
    private Float van;

    @Column(name = "ngoai_ngu")
    private Float anh;

    @Column(name = "vat_ly")
    private Float ly;

    @Column(name = "hoa_hoc")
    private Float hoa;

    @Column(name = "sinh_hoc")
    private Float sinh;

    @Column(name = "lich_su")
    private Float su;

    @Column(name = "dia_ly")
    private Float dia;

    @Column(name = "gdcd")
    private Float gdcd;

    // Constructor mặc định
    public StudentScore() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Float getToan() {
        return toan;
    }

    public void setToan(Float toan) {
        this.toan = toan;
    }

    public Float getVan() {
        return van;
    }

    public void setVan(Float van) {
        this.van = van;
    }

    public Float getAnh() {
        return anh;
    }

    public void setAnh(Float anh) {
        this.anh = anh;
    }

    public Float getLy() {
        return ly;
    }

    public void setLy(Float ly) {
        this.ly = ly;
    }

    public Float getHoa() {
        return hoa;
    }

    public void setHoa(Float hoa) {
        this.hoa = hoa;
    }

    public Float getSinh() {
        return sinh;
    }

    public void setSinh(Float sinh) {
        this.sinh = sinh;
    }

    public Float getSu() {
        return su;
    }

    public void setSu(Float su) {
        this.su = su;
    }

    public Float getDia() {
        return dia;
    }

    public void setDia(Float dia) {
        this.dia = dia;
    }

    public Float getGdcd() {
        return gdcd;
    }

    public void setGdcd(Float gdcd) {
        this.gdcd = gdcd;
    }
}