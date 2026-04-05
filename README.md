# Gita — collector trading (Android, Java)

Gita is a **barter-first** marketplace demo: users list collectibles and propose **trades** (no cash checkout). Data is stored locally with **Room/SQLite**. A stub class documents how **Azure App Service** could sync the same entities later (`com.gita.app.data.remote.AzureSyncStub`).

## Requirements

- **Android Studio** Hedgehog (2023.1.1) or newer (or compatible AGP 8.2 + JDK 17)
- **JDK 17**
- Android SDK **34** (compile/target)

## Open and run

1. In Android Studio: **File → Open** and select this `gita` folder (the one containing `settings.gradle`).
2. Let Gradle sync finish. If prompted, install missing SDK platforms/build-tools.
3. Create/start an **emulator** (API 26+) or plug in a device with USB debugging.
4. Click **Run** (green triangle) or **Run → Run ‘app’**.

First launch: **Register** a user (username unique), then add **at least one item** in **Inventory** before the **Marketplace** tab unlocks. Use a second account (clear app data or reinstall) to see another user’s listings and try trades.

## Architecture

- **MVVM**: `ViewModel` + `LiveData` per feature; **Repository** wraps Room DAOs.
- **Room**: `User`, `Item`, `Trade`, junction tables `TradeOfferedItem` / `TradeRequestedItem`.
- **UI**: `Activities` + `Fragments`, **Material 3**, **BottomNavigationView**, **RecyclerView** / **ViewPager2**.

## Azure note

This project does **not** ship a live Azure backend. SQLite is the source of truth on device. To connect Azure App Service, add REST clients (e.g. Retrofit) and sync from `AzureSyncStub` after login.

## License

Demo/educational use.
