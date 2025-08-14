@echo off
echo Downloading required JAR files...

mkdir lib 2>nul

echo Downloading Spring Boot JAR files...
curl -L "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-web/3.1.5/spring-boot-starter-web-3.1.5.jar" -o "lib/spring-boot-starter-web-3.1.5.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/spring-web/6.0.13/spring-web-6.0.13.jar" -o "lib/spring-web-6.0.13.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/spring-webmvc/6.0.13/spring-webmvc-6.0.13.jar" -o "lib/spring-webmvc-6.0.13.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/spring-core/6.0.13/spring-core-6.0.13.jar" -o "lib/spring-core-6.0.13.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/spring-context/6.0.13/spring-context-6.0.13.jar" -o "lib/spring-context-6.0.13.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/spring-beans/6.0.13/spring-beans-6.0.13.jar" -o "lib/spring-beans-6.0.13.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/3.1.5/spring-boot-3.1.5.jar" -o "lib/spring-boot-3.1.5.jar"
curl -L "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-autoconfigure/3.1.5/spring-boot-autoconfigure-3.1.5.jar" -o "lib/spring-boot-autoconfigure-3.1.5.jar"
curl -L "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/10.1.15/tomcat-embed-core-10.1.15.jar" -o "lib/tomcat-embed-core-10.1.15.jar"
curl -L "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar" -o "lib/h2-2.2.224.jar"
curl -L "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.3/jackson-core-2.15.3.jar" -o "lib/jackson-core-2.15.3.jar"
curl -L "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.3/jackson-databind-2.15.3.jar" -o "lib/jackson-databind-2.15.3.jar"
curl -L "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.3/jackson-annotations-2.15.3.jar" -o "lib/jackson-annotations-2.15.3.jar"

echo JAR files downloaded successfully!
pause
