# Use a base image with Ubuntu 22.04
FROM ubuntu:22.04

# Set environment variables
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Install necessary dependencies
RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Download and extract JDK 23
RUN curl -L https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23+37/OpenJDK23U-jdk_x64_linux_hotspot_23_37.tar.gz \
    | tar xz -C /opt/java/openjdk --strip-components=1

# Set the working directory
WORKDIR /app

# Copy your application files
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/jobportal-0.0.1-SNAPSHOT.jar"]
