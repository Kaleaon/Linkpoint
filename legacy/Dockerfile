FROM --platform=linux/amd64 openjdk:17-jdk-slim

# Install required packages
RUN apt-get update && apt-get install -y wget unzip curl git && \
    rm -rf /var/lib/apt/lists/*

# Install Android SDK for x86_64
RUN mkdir -p /opt/android-sdk
WORKDIR /opt/android-sdk
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q commandlinetools-linux-11076708_latest.zip -d /tmp/android-cmdline-tools && \
    mkdir -p cmdline-tools && \
    mv /tmp/android-cmdline-tools/cmdline-tools cmdline-tools/latest && \
    rm -rf /tmp/android-cmdline-tools && \
    rm commandlinetools-linux-11076708_latest.zip

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0

# Accept licenses and install Android SDK components
RUN yes | sdkmanager --licenses 2>/dev/null || true
RUN sdkmanager --update && \
    sdkmanager \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0" \
    "ndk;25.2.9519653" \
    "cmake;3.22.1"

# Set working directory
WORKDIR /app

# Copy Gradle wrapper first for better caching
COPY Linkpoint/gradle ./Linkpoint/gradle/
COPY Linkpoint/gradlew Linkpoint/gradle.properties Linkpoint/settings.gradle.kts ./Linkpoint/

# Set executable permissions
RUN chmod +x Linkpoint/gradlew

# Copy build files
COPY Linkpoint/build.gradle.kts Linkpoint/proguard-rules.pro ./Linkpoint/

# Download dependencies (this layer will be cached)
RUN cd Linkpoint && ./gradlew dependencies --no-daemon || true

# Copy source code
COPY Linkpoint/src ./Linkpoint/src/

# Build the APK
WORKDIR /app/Linkpoint
CMD ["./gradlew", "assembleDebug", "--no-daemon", "--stacktrace"]
