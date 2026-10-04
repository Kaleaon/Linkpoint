FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir pymysql

COPY region_server.py /app/region_server.py
COPY entrypoint.sh /app/entrypoint.sh
COPY healthcheck.sh /app/healthcheck.sh

RUN chmod +x /app/region_server.py /app/entrypoint.sh /app/healthcheck.sh

EXPOSE 8003 9000/udp

HEALTHCHECK --interval=5s --timeout=3s --start-period=5s --retries=3 CMD /app/healthcheck.sh

ENTRYPOINT ["/app/entrypoint.sh"]
