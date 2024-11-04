# Schritt 1: Verwende ein Basis-Image mit JDK 17
FROM openjdk:17-jdk-slim as build

# Setze das Arbeitsverzeichnis
WORKDIR /app

# Kopiere die Projektdateien
COPY . .

# Führe Gradle-Build aus, um die Anwendung zu bauen und zu installieren
RUN chmod +x ./gradlew
RUN ./gradlew clean installDist

# Schritt 2: Erstelle ein schlankes Image für die Bereitstellung
FROM openjdk:17-jdk-slim

# Setze das Arbeitsverzeichnis im schlanken Image
WORKDIR /app

# Kopiere die gebauten Dateien vom Build-Image
COPY --from=build /app/build/install/com.example.ktor-sample/app

# Exponiere den Port, auf dem die Ktor-Anwendung läuft (z. B. 8080)
EXPOSE 8080

# Starte die Anwendung
CMD ["./bin/com.example.ktor-sample"]
