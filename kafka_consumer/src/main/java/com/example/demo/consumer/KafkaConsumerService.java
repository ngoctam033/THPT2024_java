package com.example.demo.consumer;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.example.demo.model.ModelScore;
import com.example.demo.entity.StudentScore;
// import com.example.demo.repository.StudentScoreRepository;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    
    @Autowired
    // private StudentScoreRepository studentScoreRepository;

    private static final Map<String, String> SUBJECT_KEY_MAPPING = Map.ofEntries(
        Map.entry("Anh", "Anh"),
        Map.entry("Ngoại Ngữ", "Anh"),
        Map.entry("Ngoại ngữ", "Anh"),

        Map.entry("Toán", "Toan"),

        Map.entry("Văn", "Van"),
        Map.entry("Ngữ văn", "Van"),

        Map.entry("Lý", "Ly"),
        Map.entry("Vật lý", "Ly"),

        Map.entry("Hóa", "Hoa"),
        Map.entry("Hóa học", "Hoa"),

        Map.entry("Sinh", "Sinh"),
        Map.entry("Sinh học", "Sinh"),

        Map.entry("Sử", "Su"),
        Map.entry("Môn khác", "Su"),

        Map.entry("Địa", "Dia"),
        Map.entry("Địa lý", "Dia"),

        Map.entry("GDCD", "Gdcd")

    );

    @KafkaListener(topics = "thpt_2024", groupId = "score-group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            String message = record.value();
            System.out.println("Received message: " + message);
            
            Gson gson = new Gson();
            ModelScore modelScore = gson.fromJson(message, ModelScore.class);

            // Access score map
            Map<String, String> scores = modelScore.getScore();

            // Chuẩn hóa các key trong map điểm số
            Map<String, String> normalizedScores = normalizeScores(scores);

                // // Tạo đối tượng StudentScore và thiết lập các giá trị
            StudentScore studentScore = new StudentScore();
            studentScore.setStudentId(modelScore.getId());
            studentScore.setToan(parseFloat(normalizedScores.get("Toan")));
            studentScore.setVan(parseFloat(normalizedScores.get("Van")));
            studentScore.setAnh(parseFloat(normalizedScores.get("Anh")));
            studentScore.setLy(parseFloat(normalizedScores.get("Ly")));
            studentScore.setHoa(parseFloat(normalizedScores.get("Hoa")));
            studentScore.setSinh(parseFloat(normalizedScores.get("Sinh")));
            studentScore.setSu(parseFloat(normalizedScores.get("Su")));
            studentScore.setDia(parseFloat(normalizedScores.get("Dia")));
            studentScore.setGdcd(parseFloat(normalizedScores.get("Gdcd")));

            // Save to database
            // studentScoreRepository.save(studentScore);

            // In ra điểm số của học sinh
            System.out.println("ID: " + studentScore.getStudentId());
            System.out.println("Toán: " + studentScore.getToan());
            System.out.println("Ngữ văn: " + studentScore.getVan());
            System.out.println("Tiếng Anh: " + studentScore.getAnh());
            System.out.println("Vật lý: " + studentScore.getLy());
            System.out.println("Hóa học: " + studentScore.getHoa());
            System.out.println("Sinh học: " + studentScore.getSinh());
            System.out.println("Lịch sử: " + studentScore.getSu());
            System.out.println("Địa lý: " + studentScore.getDia());
            System.out.println("GDCD: " + studentScore.getGdcd());

            acknowledgment.acknowledge();

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private Map<String, String> normalizeScores(Map<String, String> scores) {
        Map<String, String> normalizedScores = new HashMap<>();
        
        if (scores == null) {
            System.err.println("Scores map is null.");
            return normalizedScores; // Trả về map rỗng hoặc xử lý theo cách khác
        }
        
        for (Map.Entry<String, String> entry : scores.entrySet()) {
            String originalKey = entry.getKey();
            String value = entry.getValue();
            
            if (originalKey == null) {
                System.err.println("Found null key in scores map. Skipping entry.");
                continue; // Bỏ qua các entry có key null
            }
            
            // Bỏ qua các entry có value null hoặc không hợp lệ
            if (value == null || value.trim().isEmpty()) {
                System.err.println("Found null or empty value for key: " + originalKey + ". Skipping entry.");
                continue;
            }
            
            String normalizedKey = SUBJECT_KEY_MAPPING.getOrDefault(originalKey, originalKey);
            normalizedScores.put(normalizedKey, value);
        }
        
        return normalizedScores;
    }

    private Float parseFloat(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            // Chuẩn hóa giá trị bằng cách thay thế dấu phẩy bằng dấu chấm
            String normalizedValue = value.replace(',', '.');
            return Float.parseFloat(normalizedValue);
        } catch (NumberFormatException e) {
            System.err.println("Invalid float value: " + value);
            return null;
        }
    }
}