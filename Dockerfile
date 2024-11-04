FROM ubuntu:latest
LABEL authors="Lucas"
# Basis-Image
FROM openjdk:17-jdk-slim

# Arbeitsverzeichnis setzen
WORKDIR /app

# Abhängigkeiten installieren und Projekt bauen
COPY . .
RUN ./gradlew clean installDist

# Port freigeben
EXPOSE 8080

# Start-Befehl für Ktor-Anwendung
CMD ["./build/install/deine-ktor-anwendung/bin/deine-ktor-anwendung"]
