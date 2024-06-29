FROM eclipse-temurin:17-jdk
ARG CLOUD_AWS_CREDENTIALS_SECRET_KEY
ARG CLOUD_AWS_CREDENTIALS_ACCESS_KEY
ARG CLOUD_AWS_REGION_STA_TIC
ARG CLOUD_AWS_S3_BUC_KET
ARG GOOGLE_CLIENT_IDD
ARG GOOGLE_CLIENT_SECRET_IDD
ARG GOOGLE_CLIENT_SCOPED
ENV CLOUD_AWS_CREDENTIALS_SECRETKEY=CLOUD_AWS_CREDENTIALS_SECRET_KEY
ENV CLOUD_AWS_CREDENTIALS_ACCESSKEY=CLOUD_AWS_CREDENTIALS_ACCESS_KEY
ENV CLOUD_AWS_REGION_STATIC=CLOUD_AWS_REGION_STA_TIC
ENV CLOUD_AWS_S3_BUCKET=CLOUD_AWS_S3_BUC_KET
ENV SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENTID=GOOGLE_CLIENT_IDD
ENV SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENTSECRET=GOOGLE_CLIENT_SECRET_IDD
ENV SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_SCOPE=GOOGLE_CLIENT_SCOPED
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE_PATH=build/libs/community-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} app.jar
COPY src/main/resources/application.properties /path/to/application.properties
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=/path/to/application.properties"]