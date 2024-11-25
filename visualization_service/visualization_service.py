# FILE: visualization_service.py

from flask import Flask, request, jsonify
import plotly.graph_objects as go
from flask_cors import CORS
import json

app = Flask(__name__)
CORS(app)  # Cho phép tất cả các nguồn. Có thể tùy chỉnh nếu cần.

@app.route('/create-chart', methods=['POST'])
def create_chart():
    data = request.json  # Dữ liệu từ Java
    subjects = list(data.keys())
    
    # Khởi tạo các danh sách để chứa các giá trị thống kê
    averages = []
    medians = []
    mins = []
    maxs = []
    std_devs = []
    
    for subject in subjects:
        stats = data[subject]
        averages.append(stats.get('average', 0.0))
        medians.append(stats.get('median', 0.0))
        mins.append(stats.get('min', 0.0))
        maxs.append(stats.get('max', 0.0))
        std_devs.append(stats.get('standardDeviation', 0.0))
    
    # Tạo đối tượng Figure với nhiều Trace cho các thống kê khác nhau
    fig = go.Figure()
    
    # Thêm Trace cho Average
    fig.add_trace(go.Bar(
        x=subjects,
        y=averages,
        name='Average',
        marker_color='indianred',
        error_y=dict(
            type='data',
            array=std_devs,
            visible=True,
            thickness=1.5,
            width=3,
            color='black'
        )
    ))
    
    # Thêm Trace cho Median
    fig.add_trace(go.Scatter(
        x=subjects,
        y=medians,
        name='Median',
        mode='markers+lines',
        marker=dict(color='blue', size=10),
        line=dict(dash='dash')
    ))
    
    # Thêm Trace cho Min
    fig.add_trace(go.Scatter(
        x=subjects,
        y=mins,
        name='Min',
        mode='markers',
        marker=dict(color='green', size=8),
        line=dict(dash='dot')
    ))
    
    # Thêm Trace cho Max
    fig.add_trace(go.Scatter(
        x=subjects,
        y=maxs,
        name='Max',
        mode='markers',
        marker=dict(color='orange', size=8),
        line=dict(dash='dot')
    ))
    
    # Cập nhật layout của biểu đồ
    fig.update_layout(
        title='Thống Kê Chi Tiết Các Môn Học',
        xaxis_title='Môn Học',
        yaxis_title='Điểm',
        yaxis=dict(range=[0, 10.5]),
        barmode='group',
        hovermode='x unified'
    )
    
    # Chuyển đổi biểu đồ thành HTML
    chart_html = fig.to_html(full_html=False)
    return jsonify({'chart_html': chart_html})

@app.route('/create-distribution-chart', methods=['POST'])
def create_distribution_chart():
    data = request.json  # Dữ liệu từ Java
    distribution = data.get("Phân phối điểm", {})
    
    # Sắp xếp phân phối điểm theo thứ tự tăng dần
    sorted_distribution = dict(sorted(distribution.items(), key=lambda item: float(item[0])))
    
    print(data)

    # Lấy các khóa và giá trị từ phân phối điểm đã sắp xếp
    x = list(sorted_distribution.keys())
    y = list(sorted_distribution.values())
    
    # Lấy các thông tin khác từ dữ liệu
    average_score = data.get("Điểm trung bình", "N/A")
    subject = data.get("Môn học", "N/A")
    total_students = data.get("Tổng số học sinh", "N/A")
    students_below_one = data.get("Số học sinh dưới 1", "N/A")
    median_score = data.get("Trung vị", "N/A")
    students_below_five = data.get("Số học sinh dưới 5", "N/A")

    # Biến để lưu nội dung nhận xét
    commentary = ""  # Thêm nhận xét ở đây
    
    # Tạo biểu đồ cột với các chú thích cụ thể
    fig = go.Figure(data=[
        go.Bar(
            x=x,
            y=y,
            marker_color='indianred',
            text=y,  # Thêm nhãn cho mỗi cột
            textposition='auto',  # Vị trí nhãn tự động
            hovertemplate='Điểm: %{x}<br>Số Học Sinh: %{y}<extra></extra>'
        )
    ])
    fig.update_layout(
        title=f'Phân Phối Điểm - {subject}',
        xaxis_title='',
        yaxis_title='Số Học Sinh',
        annotations=[
            dict(
                x=0.5,
                y=-0.15,
                xref='paper',
                yref='paper',
                text=f"Điểm trung bình: {average_score} | Tổng số học sinh: {total_students} | "
                     f"Số học sinh dưới 1: {students_below_one} | Trung vị: {median_score} | "
                     f"Số học sinh dưới 5: {students_below_five}",
                showarrow=False,
                align='center'
            )
        ],
        uniformtext=dict(minsize=8, mode='hide'),
        margin=dict(b=150),
        template='plotly_white'
    )
    
    chart_html = fig.to_html(full_html=False)
    
    return jsonify({'chart_html': chart_html})
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)