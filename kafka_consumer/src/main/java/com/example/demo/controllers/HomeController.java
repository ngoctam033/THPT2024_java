package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.example.demo.service.StatisticsService;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/")
    public String index(Model model) {

        String chartHtml = statisticsService.generateChart();
        model.addAttribute("chartHtml", chartHtml);

        List<String> subjects = Arrays.asList("toan", "ngu_van", "ngoai_ngu", "vat_ly", "hoa_hoc", "sinh_hoc", "lich_su", "dia_ly", "gdcd");
        // List lưu trữ biểu đồ HTML cho từng môn học
        List<String> charts = new ArrayList<>();

        for (String subject : subjects) {
            try {
                // Gọi hàm để lấy biểu đồ phân phối điểm cho từng môn
                String subjectChartHtml = statisticsService.getScoreDistributionChart(subject);
                charts.add(subjectChartHtml);
            } catch (IllegalArgumentException e) {
                // Xử lý môn học không hợp lệ
                charts.add("<p>Môn học không hợp lệ: " + subject + "</p>");
            }
        }
        // Thêm List vào mô hình để truy cập từ index.html
        model.addAttribute("charts", charts);

        return "index"; // Trả về templates/index.html
    }
}