# SimpleStock

SimpleStock is an Android application designed to help users manage and visualize sales and stock data efficiently. It
features a modern UI, interactive charts, and robust data management capabilities.

## Features

- View and manage sales records
- Visualize sales data with interactive charts (powered by MPAndroidChart)
- Data persistence using a local database
- Intuitive navigation and modern Material Design

## Getting Started

### Prerequisites

- Android Studio (latest recommended)
- JDK 17 or later
- Android device or emulator (API 24+)

### Configuration

Before building and running the project, you must create an `application.properties` file (not versioned) in the
following locations:

- `app/src/main/assets/application.properties` — for runtime configuration
- `app/src/test/resources/application.properties` — for test configuration

These files should contain any required configuration properties for your environment.

```properties
db.url=
db.username=
db.password=
db.pool.size=
```

### Building the Project

1. Clone the repository:
   ```sh
   git clone https://github.com/rusted-mg/simple-stock-app
   cd SimpleStock
   ```
2. Open the project in Android Studio.
3. Let Gradle sync and download dependencies.
4. Build and run the app on your device or emulator.

### Project Structure

- `app/src/main/java/io/github/rusted/simplestock/` — Main application source code
- `app/src/main/res/` — Resources (layouts, drawables, values)
- `app/build.gradle.kts` — App-level Gradle configuration

## Libraries Used

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) — Charting library for Android
- AndroidX libraries

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
