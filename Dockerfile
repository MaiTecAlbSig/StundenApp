# Verwende das offizielle OpenJDK-Image als Basis
FROM openjdk:17-jdk-slim

# Setze das Arbeitsverzeichnis
WORKDIR /app

# Kopiere das gesamte Projekt in den Container
COPY . .

# Gib dem Gradle-Wrapper Ausführungsrechte
RUN chmod +x ./gradlew

# Baue die Anwendung
RUN ./gradlew clean installDist

# Setze den Startbefehl für die Anwendung
CMD ["./build/install/com.example.ktor-sample/bin/com.example.ktor-sample"]

# Exponiere den Port, auf dem die Ktor-Anwendung läuft
EXPOSE 8080

