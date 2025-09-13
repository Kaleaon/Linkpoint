FROM --platform=linux/amd64 openjdk:17-jdk-slim

# Install required packages
RUN apt-get update && apt-get install -y wget unzip curl

# Install Android SDK for x86_64
RUN mkdir -p /opt/android-sdk
WORKDIR /opt/android-sdk
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
RUN unzip commandlinetools-linux-9477386_latest.zip
RUN mkdir -p cmdline-tools && mv cmdline-tools latest && mv latest cmdline-tools/

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses and install Android SDK components
RUN yes | sdkmanager --licenses 2>/dev/null || true
RUN sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Set executable permissions
RUN chmod +x gradlew

# Build the APK
CMD ["./gradlew", "assembleDebug", "--no-daemon"]