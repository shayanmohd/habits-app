# Habits

A native Android habit tracker focused on minimalist design, smooth performance, and full offline usage.

## Overview

- App name: `Habits`
- Application ID: `com.mohdshayan.habits`
- Platform: Android (Kotlin + Jetpack Compose)
- Data model: Local Room (SQLite), no backend

## Features

- Add habits with notes and frequency:
- Daily
- Specific weekdays
- Every N days
- Habit-specific calendar status view
- Today checklist with completion toggles
- Long-press to select and delete a habit
- Motivational discipline quote on launch
- Home screen summary widget (Glance)
- Multi-language strings (EN, ES, HI, FR, DE, AR, JA, ZH, PT)

## Tech Stack

- Kotlin
- Jetpack Compose
- Room (SQLite)
- MVVM + StateFlow
- Jetpack Glance (Widget)

## Privacy

This app is designed to run fully offline and keep user habit data on-device.
See [PRIVACY_POLICY.md](PRIVACY_POLICY.md).

## Build

Requirements:
- JDK 17+
- Android SDK installed

Commands:

```bash
./gradlew assembleDebug
./gradlew bundleRelease
```

## Project Structure

- `app/src/main/java/com/mohdshayan/habits/` - app source code
- `app/src/main/res/` - resources, localization, icons
- `app/schemas/` - Room schema export

## Author

- Mohd Shayan
- Website: https://mohdshayan.com
- GitHub: https://github.com/shayanmohd
- Email: contact@mohdshayan.com
- LinkedIn: https://linkedin.com/in/shayanmohd
- X: @mohdshayanX

## License

MIT License. See [LICENSE](LICENSE).
