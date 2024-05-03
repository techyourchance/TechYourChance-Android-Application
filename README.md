# TechYourChance Android Application
This app demonstrates the best practices for Android development and contains a repository of useful features that you can learn from and use in your projects. It shows what I consider to be a "clean and pragmatic Android code".

The architecture, implementation details, tests, etc. in this application follow the practices and guidelines that I teach in my [Android development courses](https://www.techyourchance.com/courses/).

## Sponsors
<p>
    Huge thanks to Bitrise for sponsoring the Continuous Integration infrastructure in this project!<br><br>
    <a href="https://bitrise.io/?utm_source=twitter&utm_medium=social&utm_campaign=vasiliy-zukanov&utm_content=sponsored-post" target="_blank">
        <img src="https://github.com/techyourchance/TechYourChance-Android-Application/assets/12713429/e5c83f31-6caf-416c-93cf-cef713d0bd3d" width="25%" height="25%">
    </a>
</p>

## Installation

You can download a [prebuilt APK file attached to the latest release](https://github.com/techyourchance/TechYourChance-Android-Application/releases/latest), or clone this repo and build the application from the source code.

## Application Updates

The application includes auto-update feature. It uses a special Android API that will prompt you to approve the installation of each new version of the application. On the first update, this API will also ask you to enable the auto-update feature in device's settings app.

Alternatively, you can download the latest APK from here and update the application manually, or even build the update from the source code.

## Blog posts' tutorials

Parts of the source code in this app correspond to the articles I published on my blog:

| **Blog Post** | **Source Code** |
|---------------|-----------------|
| [Bottom Bar Navigation in Android with Compose Navigation](https://www.techyourchance.com/bottom-bar-navigation-android-compose-navigation/) | [Screen](https://github.com/techyourchance/TechYourChance-Android-Application/tree/master/app/src/main/java/com/techyourchance/android/screens/composenavbottombar)|
| [Foreground Service in Android](https://www.techyourchance.com/foreground-service-in-android/) | [Screen](app/src/main/java/com/techyourchance/android/screens/foregroundservice) <br> [Logic](app/src/main/java/com/techyourchance/android/backgroundwork/foregroundservice) |
| [WorkManager in Android](https://www.techyourchance.com/work-manager-android-tutorial/) | [Screen](app/src/main/java/com/techyourchance/android/screens/workmanager) <br> [Logic](app/src/main/java/com/techyourchance/android/backgroundwork/workmanager) |
| [Android Room Tutorial Part 2 – The Architecture](https://www.techyourchance.com/android-room-tutorial-architecture/) | [Logic](https://github.com/techyourchance/TechYourChance-Android-Application/tree/master/database/src/main/java/com/techyourchance/android/database) |
| [Android NDK Tutorial](https://www.techyourchance.com/android-ndk-tutorial/) | [Screen](app/src/main/java/com/techyourchance/android/screens/ndkbasics) <br> [Logic](app/src/main/java/com/techyourchance/android/ndk) |
| [Biometric Authentication in Android](https://www.techyourchance.com/biometric-authentication-in-android/) | [Screen](app/src/main/java/com/techyourchance/android/screens/biometricauth) <br> [Logic](app/src/main/java/com/techyourchance/android/biometric) |
| [How to Use Jetpack Compose Inside Android Service](https://www.techyourchance.com/jetpack-compose-inside-android-service/) | [Screen](https://github.com/techyourchance/TechYourChance-Android-Application/tree/master/app/src/main/java/com/techyourchance/android/screens/composeoverlay) <br> [Logic](https://github.com/techyourchance/TechYourChance-Android-Application/tree/master/app/src/main/java/com/techyourchance/android/overlay) |
| [Kotlin Coroutines vs Threads Performance Benchmark](https://www.techyourchance.com/kotlin-coroutines-vs-threads-performance-benchmark/) | [Screen](app/src/main/java/com/techyourchance/android/screens/benchmarks/backgroundtasksstartupbenchmark) <br> [Logic](app/src/main/java/com/techyourchance/android/backgroundtasksbenchmark/startup) |
| [Kotlin Coroutines vs Threads Memory Benchmark](https://www.techyourchance.com/kotlin-coroutines-vs-threads-memory-benchmark/) | [Screen](app/src/main/java/com/techyourchance/android/screens/benchmarks/backgroundtasksmemorybenchmark) <br> [Logic](app/src/main/java/com/techyourchance/android/backgroundtasksbenchmark/memory) |


## Architecture

The project is written in Kotlin. 

Some parts of the UI use the "classical" Android UI framework (XMLs + Views), while others use Jetpack Compose. There are also screens that demonstrate alternative implementations of UI components using Views and Compose (i.e. you can switch between different implementations of the same UI component). 

Packages structure of the application follows [package by feature](https://www.techyourchance.com/popular-package-structures/) approach.

Dependency injection architectural pattern is implemented using [Dagger 2 framework](https://www.techyourchance.com/courses/android-dependency-injection-with-dagger-and-hilt/).

Presentation layer logic is organized according to [MVC architectural pattern](https://www.techyourchance.com/mvc-android-1/).

Functional flows in the app are encapsulated into [Use Case](https://www.techyourchance.com/how-to-use-case-interactor-kotlin/) classes.

The logic responsible for [navigation between screens](https://www.techyourchance.com/navigation-between-screens-android/) is encapsulated in `ScreensNavigator` class.

`DialogsNavigator` "facade" class is responsible for showing dialogs.

[Kotlin Coroutines framework](https://www.techyourchance.com/courses/kotlin-coroutines-in-android-course/) is used for concurrency.

## Contribution

Bug reports, code quality feedback and feature suggestions are welcome. Since this project aims to demonstrate a very specific approach to Android development, any external contribution should be discussed and approved in advance.
