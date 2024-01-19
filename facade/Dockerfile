FROM eclipse-temurin:21 as jre-build
COPY target/lib /usr/src/lib
COPY target/facade-1.0.0-runner.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx64m -jar facade-1.0.0-runner.jar
