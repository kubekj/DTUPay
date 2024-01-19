FROM eclipse-temurin:21 as jre-build
COPY target/lib /usr/src/lib
COPY target/report-mgmt-service-1.0.0.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx32m -jar report-mgmt-service-1.0.0.jar
