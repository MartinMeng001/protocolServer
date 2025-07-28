FROM dockerproxy.net/library/openjdk:16-jdk
WORKDIR /app

# 设置时区环境变量
ENV TZ=Asia/Shanghai

COPY ./target/protocol-server-1.0.0.jar app.jar
# COPY ./CleanRobosService/config.xml config.xml

EXPOSE 8081
EXPOSE 10003

VOLUME /app/data


# 在ENTRYPOINT中指定配置文件的位置
# ENTRYPOINT ["java", "-jar", "app.jar", "-Dconfig.file=/app/config.xml"]
ENTRYPOINT ["java", "-jar", "app.jar"]
