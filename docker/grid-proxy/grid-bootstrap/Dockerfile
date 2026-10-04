FROM python:3.11-slim

WORKDIR /app

RUN pip install --no-cache-dir pymysql

COPY bootstrap.py /app/bootstrap.py
COPY templates/ /app/templates/

RUN chmod +x /app/bootstrap.py

ENTRYPOINT ["python3", "/app/bootstrap.py"]
