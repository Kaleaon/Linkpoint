FROM nginx:alpine

RUN apk add --no-cache openssl curl

COPY nginx.conf /etc/nginx/nginx.conf
COPY entrypoint.sh /app/entrypoint.sh
COPY healthcheck.sh /app/healthcheck.sh

RUN chmod +x /app/entrypoint.sh /app/healthcheck.sh

EXPOSE 80 443

HEALTHCHECK --interval=5s --timeout=3s --start-period=5s --retries=3 CMD /app/healthcheck.sh

ENTRYPOINT ["/app/entrypoint.sh"]
