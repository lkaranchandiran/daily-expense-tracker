# Daily Expense Tracker 💰

A fast, lightweight, and completely on-device personal expense tracker for Android built with modern Kotlin and Jetpack Compose. Designed with a clean Material 3 financial aesthetic (Emerald & Mint), zero third-party subscription costs, and persistent local storage powered by Room SQLite.

---

## 📱 Daily Workflow
> **Open app → Tap Add Expense → Enter amount → Select category → Save.**
> *No unnecessary typing, no mandatory descriptions, no slow network calls, zero AI credit usage.*

---

## ✨ Features

- **⚡ Instant Quick Expense Entry:**
  - Automatic focus on numeric keypad.
  - Large currency amount display (defaults to Indian Rupee `₹`).
  - 8 One-tap category chips with custom icons: **Groceries**, **Food**, **Travel**, **Shopping**, **Bills**, **Entertainment**, **Health**, and **Other**.
  - Optional notes/descriptions.
  - Defaults to today's date with a built-in calendar picker for logging past expenses.
  - Full edit and delete support.

- **📊 Comprehensive Home Dashboard:**
  - High-impact daily metrics: **Today's Spend** and **This Month's Total**.
  - Dynamic **Monthly Budget Progress Card** showing spending vs target, remaining balance, and safe daily spend allowance.
  - Visual color-coded alert indicators (Green under 70%, Amber warning at 75-99%, Red alert when exceeded).
  - Quick access to recent transactions.

- **🔍 Searchable & Filterable Expense History:**
  - Real-time search across descriptions, categories, and amounts.
  - One-tap category filter carousel.
  - Summary header showing matching count and total amount.
  - Delete with confirmation dialog to prevent accidental data loss.

- **📈 Monthly Spending Reports & Category Analytics:**
  - Interactive Month Switcher (`< Previous Month | Current | Next Month >`).
  - Key financial metrics: Total Spent, Daily Average, Total Transactions, and Highest Single Expense.
  - Visual category breakdown with percentage bars and item counts.

- **🎯 Budget & Custom Settings:**
  - Configurable monthly budget with quick presets (₹10k, ₹20k, ₹30k, ₹50k, ₹75k).
  - Default currency: Indian Rupee (INR - ₹).
  - Data privacy: 100% on-device, zero external server tracking, zero AI costs.

- **💾 Data Reliability & CSV Export:**
  - Local Room SQLite database with full ACID compliance. Data survives app updates and restarts.
  - Export all transaction history to `.csv` format via standard Android Share sheet (WhatsApp, Email, Google Drive, Files).
  - Safe data reset option with confirmation alert.

---

## 🛠️ Architecture & Tech Stack

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** Clean MVVM (Model-View-ViewModel) with unidirectional reactive data flow
- **Database:** Android Room 2.7.0 (SQLite) with KSP code generation
- **State Management:** Kotlin Coroutines & `StateFlow` with lifecycle-aware collection
- **Testing:** JUnit 4 & Robolectric 4.16 for fast, local JVM testing

---

## 🚀 Getting Started & Installation

### Prerequisites
- **Android Studio** (Ladybug | 2024.2.1 or newer recommended)
- **JDK 17** or **JDK 21**
- **Android SDK:** `compileSdk 36`, `minSdk 24` (Android 7.0+)

### Building Locally

1. **Clone or download the repository:**
   ```bash
   git clone https://github.com/<your-username>/daily-expense-tracker.git
   cd daily-expense-tracker
   ```

2. **Open the project in Android Studio:**
   - Launch Android Studio.
   - Select **File > Open...** and choose the project root folder.
   - Wait for Gradle sync to complete.

3. **Run on Device or Emulator:**
   - Connect an Android device with USB debugging enabled or launch an Android Virtual Device (AVD).
   - Press **Shift + F10** or tap the **Run** button.

---

## 🧪 Testing

The application includes automated Unit and Robolectric tests validating:
- Room SQLite database CRUD operations (Insert, Update, Delete, Batch Clear)
- User budget preferences persistence
- Currency and date formatting
- Budget calculations (Remaining, Percentage, Daily Allowance)
- CSV generation and data escaping

Run tests from the command line:
```bash
gradle :app:testDebugUnitTest
```

Or from Android Studio:
- Right-click `app/src/test/java/com/example/` and select **Run 'Tests in 'com.example''**.

---

## 📦 Production Build & APK Generation

To generate a production-ready signed APK:

1. **Build Release APK:**
   ```bash
   gradle :app:assembleRelease
   ```
   The APK will be generated at `app/build/outputs/apk/release/app-release.apk`.

2. **Build Android App Bundle (for Google Play):**
   ```bash
   gradle :app:bundleRelease
   ```
   The `.aab` file will be generated at `app/build/outputs/bundle/release/app-release.aab`.

---

## 🌐 Pushing to GitHub

To push this codebase to your own GitHub account:

1. Create a new repository on GitHub named `daily-expense-tracker` (leave it empty without initializing README).
2. Run the following commands in your terminal:
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Production-ready Daily Expense Tracker Android App"
   git branch -M main
   git remote add origin https://github.com/<your-username>/daily-expense-tracker.git
   git push -u origin main
   ```

---

## 🔒 Security & Privacy

- **No API keys or cloud tokens required.**
- **No data leaves your device:** All expenses and budget targets are stored strictly in the sandboxed local SQLite database.
- `.gitignore` is pre-configured to exclude all build caches, keystores, and local environment files.

---

## 📄 License
Open source and free to use under the MIT License.
