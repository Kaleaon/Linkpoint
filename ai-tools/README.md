# AI-Assisted Build Tools for Linkpoint

This directory contains tools to help automate the build process for the Linkpoint/Lumiya project in a safe and reliable way.

## Quick Start

The primary tool is the `setup-and-build.sh` script. This script will guide you through the process of building the Linkpoint APK.

### Prerequisites

Before running the script, please ensure you have the following installed and configured:

1.  **Java 17:** The build process requires Java 17. You can check your version with `java -version`.
2.  **Android SDK:** You need the Android SDK installed on your system.
3.  **`ANDROID_HOME` Environment Variable:** You must have the `ANDROID_HOME` environment variable set to the path of your Android SDK.

### How to Build

1.  **Make the script executable:**
    ```bash
    chmod +x ai-tools/setup-and-build.sh
    ```

2.  **Run the script:**
    ```bash
    ./ai-tools/setup-and-build.sh
    ```

### What the Script Does

The `setup-and-build.sh` script will:

1.  **Verify Prerequisites:** It will check if Java 17 is installed and if the `ANDROID_HOME` variable is set. If not, it will provide instructions on how to fix this.
2.  **Check Configuration:** It will check for a `local.properties` file in the project root. If the file doesn't exist, it will create a default one for you. If the file exists but is missing the required `sdk.dir` property, it will guide you on how to fix it.
3.  **Run the Build:** It will execute the standard Gradle `assembleDebug` task to build the application. All output from the build will be displayed on the console.
4.  **Locate the APK:** If the build is successful, it will print the path to the generated APK file (`app/build/outputs/apk/debug/app-debug.apk`).

This script is designed to be safe and non-destructive. It will not install any software on your system or overwrite any critical configuration files without first checking with you. This makes it suitable for use by both human developers and AI assistants.
