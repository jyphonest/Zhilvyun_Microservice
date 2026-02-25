FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/eclipse-temurin:17-jdk
LABEL maintainer="your-name"
COPY target/app.jar /app.jar
EXPOSE 9004
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/app.jar"]