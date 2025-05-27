---

# Hockey-APP-Live

Hockey-APP-Live is a mobile application developed in Kotlin with the Jtepack compose framework for the User interface which makes it posible to create UI elements using composable functions in kotlin. The application is designed to offer real-time hockey match updates, comprehensive statistics, and a smooth user experience for hockey for the Namibian Hockey Union. It shows the latest event updates, player details, or match schedules.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features

- Live hockey event updates
- There are two users (The coach user and admin user)
- Detailed team details (Team name,players, player positions, coach details etc.)
- Team and player profiles
- Upcoming match schedules and historical results
- Intuitive user interface


## Getting Started

Follow these instructions to set up and run the project on your local machine for development and testing.

### Prerequisites

- Android Studio (latest version recommended)
- Java 8 or above
- Kotlin plugin enabled
- Android SDK 21 or above

### Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/intellectualtech/Hockey-APP-Live.git
    ```

2. **Open in Android Studio:**

    - Launch Android Studio.
    - Click on `Open an existing Android Studio project`.
    - Select the cloned `Hockey-APP-Live` folder.

3. **Build the project:**

    - Allow Gradle to sync and download dependencies.
    - Connect your Android device or start an emulator.
    - Click `Run` (▶️) to build and launch the app.

## Usage

- Open the app to view live hockey scores and recent match updates.
- Explore team and player stats from the navigation menu.
- Check schedules to see upcoming games.
- Enable notifications to get real-time alerts for your favorite teams or matches.

## Technologies Used

- **Languages:** Java (83.7%), Kotlin (16.3%)
- **Frameworks:** Android SDK, Jetpack libraries
- **Tools:** Android Studio, Gradle
- **APIs:** [Add details if you use any hockey data APIs]
- **Other:** [Add other tools or libraries if relevant: Retrofit, Glide, Room, etc.]
- **Backend:**
  **SQLite -** 
  The application uses SQLite for the backend whereby the the users and teams details are stored in a database locally on the device being used, this is done using raw       SQLite via SQLiteOpenHelper in DatabaseHelper.




## License

This project is licensed under the Intellectualtechnology License. See the [LICENSE](LICENSE) file for more information.

---
