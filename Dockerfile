# Verwende ein leichtgewichtiges JDK 17 Image
FROM openjdk:17-jdk-slim

# Setze das Arbeitsverzeichnis im Container
WORKDIR /app

# Kopiere die Projektdateien in den Container
COPY . .

# Gradle Wrapper ausführbar machen
RUN chmod +x ./gradlew

# Baue das Projekt (wird im build/libs Ordner erzeugt)
RUN ./gradlew clean shadowJar

# Definiere die Umgebungsvariable für den Port
ENV PORT=8080

# Exponiere den Port 8080
EXPOSE 8080

# Starte die JAR-Datei (der Name kann variieren, je nachdem wie du deine Anwendung im build.gradle.kts benannt hast)
CMD ["java", "-jar", "build/libs/com.example.ktor-sample-all.jar"]