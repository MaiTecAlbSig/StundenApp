FROM ubuntu:latest
LABEL authors="Lucas"
# Basis-Image
FROM openjdk:17-jdk-slim

# Arbeitsverzeichnis setzen
WORKDIR /app

# Kopiere alle Dateien und setze die Berechtigungen für gradlew
COPY . .
RUN chmod +x ./gradlew

# Abhängigkeiten installieren und Projekt bauen
RUN ./gradlew clean installDist

# Port freigeben
EXPOSE 8080

# Start-Befehl für Ktor-Anwendung
CMD ["./build/install/deine-ktor-anwendung/bin/deine-ktor-anwendung"]
