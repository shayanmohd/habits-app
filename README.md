# HabitTrack

A native Android habit tracking app built with Kotlin and Jetpack Compose.

## Author

- Name: Mohd Shayan
- Website: https://mohdshayan.com
- GitHub: https://github.com/shayanmohd
- Email: contact@mohdshayan.com
- LinkedIn: https://linkedin.com/in/shayanmohd
- X: @mohdshayanX

## Copyright

Copyright (c) Mohd Shayan. All rights reserved.

## Tech Stack

- Kotlin
- Jetpack Compose
- Room (SQLite)
- MVVM + StateFlow
- Jetpack Glance (Widget)

## Features

- Minimal, CPU-light habit tracking UI
- Habit frequencies: daily, specific weekdays, every N days
- Notes per habit
- Today checklist with completion toggle
- Habit-specific calendar status tracking
- Home screen widget summary
- Multi-language string resources

## Project Structure

- `app/src/main/java/com/shreezy/habittrack/` - app source code
- `app/src/main/res/` - resources, localization, icons
- `app/schemas/` - Room schema export

## Build

```bash
./gradlew assembleDebug
```
