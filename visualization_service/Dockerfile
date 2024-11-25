# Dockerfile.python
FROM python:3.12-slim

WORKDIR /app

COPY . .

RUN pip install flask plotly flask-cors watchdog

ENV FLASK_APP=visualization_service.py 
ENV FLASK_ENV=development
ENV PYTHONUNBUFFERED=1

EXPOSE 5000

CMD ["flask", "run", "--host=0.0.0.0"]