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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)