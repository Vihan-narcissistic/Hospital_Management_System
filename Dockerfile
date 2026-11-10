
# Stage 1: Build the Java WAR application
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application on Tomcat 10.1
FROM tomcat:10.1-jdk17-temurin

# Remove Tomcat's default ROOT application
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Deploy our existing WAR
COPY --from=build /app/target/HospitalManagement.war \
    /usr/local/tomcat/webapps/HospitalManagement.war

EXPOSE 8080

# Use Render's assigned port
CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"'\"${PORT:-8080}\"'\"/' \"$CATALINA_HOME/conf/server.xml\" && exec catalina.sh run"]
