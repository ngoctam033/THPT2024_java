from flask import Flask, request, jsonify
import plotly.graph_objects as go
from flask_cors import CORS  # Thêm import
import json

app = Flask(__name__)
CORS(app)  # Cho phép tất cả các nguồn. Có thể tùy chỉnh nếu cần.

@app.route('/create-chart', methods=['POST'])
def create_chart():
    data = request.json  # Dữ liệu từ Java
    subjects = list(data.keys())
    averages = list(data.values())
    
    print(data)
    
    fig = go.Figure(data=[go.Bar(x=subjects, y=averages, marker_color='indianred')])
    fig.update_layout(
        title='Điểm Trung Bình Các Môn Học',
        xaxis_title='Môn Học',
        yaxis_title='Điểm Trung Bình',
        yaxis=dict(range=[0, 10])
    )
    
    chart_html = fig.to_html(full_html=False)
    return jsonify({'chart_html': chart_html})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)