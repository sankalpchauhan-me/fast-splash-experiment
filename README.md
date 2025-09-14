# FastSplash: Fast, Non-Blocking Event Splash for Android
FastSplash is an Android sample that explores fast startup and animated, configurable **event splashes that run in parallel** with your primary content. It showcases Clean Architecture, Jetpack Compose and modular design via two reusable modules:
- **EventSplash** — lightweight splash/transition library (Compose-first, fluent API, Lottie/Image support).
- **PerfTracker** — tiny tracing utility for startup, FCP, FPT and custom marks.

The sample app is a movie browser that integrates both libraries and ships with repeatable benchmarks and scripts so you can reproduce the numbers on your device.

![FastSplash Architecture](https://github.com/sankalpchauhan-me/fast-splash-experiment/blob/main/diagrams/app_architecture_hld.png?raw=true)

## TL;DR
- ✅ Compose-first, animated splash screens (EventSplash)
- ✅ Repeatable startup metrics (PerfTracker + scripts)
- ✅ Clean Architecture + Hilt + Retrofit
- ✅ Reproducible results with Macrobenchmark / adb script

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [High-Level Design](#high-level-design)
- [Why EventSplash](#why-eventsplash-not-a-replacement-for-androidx-splashscreen)
- [Modules Deep Dive](#modules-deep-dive)
- [EventSplash Library](#eventsplash-library)
- [Performance Tracking](#perftracker-module)
- [Benchmark Results](#benchmark-results)
- [Setup Instructions](#setup-instructions)
- [Tech Stack](#tech-stack)
- [Learning Objectives](#learning-objectives)

## Architecture Overview

This project follows **Clean Architecture** principles with clear separation of concerns across multiple layers. The architecture is designed to be scalable, testable and maintainable while demonstrating modern Android development practices.

### Architecture Layers

- **Presentation Layer**: Jetpack Compose UI with ViewModels
- **Domain Layer**: Business logic and repository interfaces
- **Data Layer**: API services, data sources and repository implementations
- **Custom Libraries**: EventSplash for splash screens, PerfTracker for performance monitoring

![Clean Architecture](https://github.com/sankalpchauhan-me/fast-splash-experiment/blob/main/diagrams/clean_architecture.png)


## Project Structure

```
FastSplash/
├── app/                                    # Main application module
│   ├── src/main/java/me/sankalpchauhan/fastsplash/
│   │   ├── data/                          # Data layer
│   │   │   ├── api/                       # API services and interceptors
│   │   │   ├── di/                        # Data dependency injection
│   │   │   └── model/                     # Data models and DTOs
│   │   ├── domain/                        # Domain layer
│   │   │   ├── model/                     # Domain models
│   │   │   └── repository/                # Repository interfaces
│   │   ├── presentation/                  # Presentation layer
│   │   │   ├── base/                      # Base UI components and theme
│   │   │   └── listing/                   # Movie listing feature
│   │   ├── di/                           # Application dependency injection
│   │   └── utils/                        # Utility classes
│   └── build.gradle.kts                  # App module build configuration
├── eventsplash/                          # Custom splash screen library
│   ├── src/main/java/me/sankalpchauhan/eventsplash/
│   │   ├── core/                         # Core splash implementation
│   │   ├── model/                        # Configuration models
│   │   ├── utils/                        # Animation utilities
│   │   └── viewprovider/                 # Splash view providers
│   └── build.gradle.kts                  # Library build configuration
├── PerfTracker/                          # Performance tracking library
│   ├── src/main/java/me/sankalpchauhan/perftracker/
│   │   └── PerfTrace.kt                  # Performance tracing implementation
│   └── build.gradle.kts                  # Library build configuration
├── build.gradle.kts                     # Project build configuration
├── settings.gradle.kts                  # Project settings
└── local.properties.template            # Template for API keys
```

## High-Level Design

The following diagram illustrates the high-level architecture and data flow of the FastSplash application:

![Basic HLD](https://github.com/sankalpchauhan-me/fast-splash-experiment/blob/main/diagrams/basic_hld.png?raw=true)

## Why EventSplash (Not a Replacement for AndroidX SplashScreen)

**EventSplash** focuses on *event* splashes that run **in parallel** with your app’s main content render.  
It **does not route through a separate splash Activity** and it **does not block** the primary page load.  
Typical routing/“splash activity” patterns delay FCP/FPT; EventSplash is designed to avoid that.

## Modules Deep Dive

### Main App Module

The main application module demonstrates a complete implementation of a movie browsing app using modern Android development practices.

#### Key Components

**MainActivity**: The single activity that hosts the entire application using Jetpack Compose. It demonstrates:
- Integration with the EventSplash library for custom splash screens
- Performance tracking using PerfTracker
- Edge-to-edge display implementation
- Proper lifecycle management

**MainViewModel**: Implements the MVVM pattern with:
- StateFlow for reactive UI updates
- Coroutines for asynchronous operations
- Search functionality with debouncing
- Pagination support
- Error handling and loading states

**Repository Pattern**: Clean separation between data sources and business logic:
- MoviesRepository interface in the domain layer
- Repository implementation in the data layer
- Dependency injection using Dagger Hilt

#### Data Flow

1. **User Interaction**: User interacts with Compose UI
2. **ViewModel Processing**: ViewModel processes user actions and updates state
3. **Repository Call**: ViewModel calls repository methods
4. **API Request**: Repository makes network calls via Retrofit
5. **State Update**: Results flow back through StateFlow to update UI

### EventSplash Library

A custom library for creating beautiful and performant splash screens with various animation options.

#### Features

- **Multiple Splash Types**: Default, Image and Lottie animation support
- **Customizable Animations**: Fade out, zoom in, slide up, slide left
- **Fluent API**: Easy-to-use builder pattern
- **Jetpack Compose**: Built entirely with Compose for modern UI
- **Lifecycle Aware**: Proper cleanup and memory management

#### Class Structure

![Class Diagram](https://github.com/sankalpchauhan-me/fast-splash-experiment/blob/main/diagrams/eventsplash_class_diagram.png)

#### EventSplash — API at a glance

```kotlin
// Default 
EventSplashApi.attachTo(this).show()

// Lottie
val lottieConfig = LottieConfig(
    outAnimation = OutAnimType.SLIDE_LEFT,
    outDuration = 200,
    bgColor = listOf("#FFFF00"),
    lottieComposition = composition
)
EventSplashApi.attachTo(this).with(lottieConfig).show()

// Image
val imageConfig = ImageConfig(
    outAnimation = OutAnimType.FADE_OUT,
    outDuration = 500,
    bgColor = listOf("#FF5722", "#FFC107"),
    drawable = customDrawable,
    showDuration = 2000
)
EventSplashApi.attachTo(this).with(imageConfig).show()
```

### PerfTracker Module

A lightweight performance tracking library for measuring application startup and rendering performance.

#### Capabilities

- **Startup Time Tracking**: Measure app launch performance
- **First Contentful Paint (FCP)**: Track when first content appears
- **Fully Painted Time (FPT)**: Measure complete page rendering
- **Custom Metrics**: Create custom performance traces

#### Implementation

```kotlin
class FastSplashApplication: Application() {
    val fcp = PerfTrace("FCP")
    val pageRender = PerfTrace("RenderTrace")
    val fpt = PerfTrace("FPT")
    
    override fun onCreate() {
        super.onCreate()
        fcp.startTrace()
        pageRender.startTrace()
        fpt.startTrace()
    }
}
```


## Benchmark Results

> **Methodology:** Cold launches · identical device & build · 2s pause between runs · 35 runs (30 considered)
> **Env:** Xiaomi Poco F1 · Android 10 · Release build. 
> **Capture:** PerfTracker logs (**Page Load**, **First Contentful Paint (FCP)**, **Fully Painted Time (FPT)**). 
> Scripts are included so you can reproduce these numbers locally:- Script: `perf_loop.sh`
 - It launches the app 35 times, waits for timeout, parses PERF logs:
    ```
    PERF  Page Ready <ms>
    PERF  First Contentful Paint <ms>
    PERF  Fully Painted Time <ms>
    PERF  TRACE_STOPPED
    ```
> Run: `bash perf_loop.sh` → outputs `perf_runs.csv`
>
**Definitions:**  
*Page Load* = `PerfTracker` “Page Ready” marker.  
*FCP* = first meaningful content visible.  
*FPT* = first frame where target screen is fully painted.

### A) Lottie Splash — Blocking vs Non-Blocking (same device, same build)

| Metric    | Blocking (ms) | Non-Blocking (ms) | Reduction |
|-----------|---------------:|------------------:|----------:|
| Page Load | 2228           | 109               | **95.1%** |
| FCP       | 2347           | 312               | **86.7%** |
| FPT       | 3524           | 1467              | **58.4%** |

> Non-blocking Lottie keeps the splash visual **without** delaying first meaningful paint.
> **Note on Lottie:** Case A Blocking includes the **Lottie animation duration** by design.
For apples-to-apples “no animation budget” comparisons, see **B** and **C**.

### B) Default Splash — Blocking vs Non-Blocking (same device, same build)

| Metric    | Blocking (ms) | Non-Blocking (ms) | Reduction |
|-----------|---------------:|------------------:|----------:|
| Page Load | 366           | 37               | **89.8%** |
| FCP       | 744           | 164               | **77.9%** |
| FPT       | 2195           | 1295              | **41.0%** |

> Non-blocking Splash keeps the splash visual **without** delaying first meaningful paint.

### C) Default Blocking vs Lottie Non-Blocking

| Metric    | Default Blocking (ms) | Lottie Non-Blocking (ms) | Reduction |
|-----------|----------------------:|-------------------------:|----------:|
| Page Load | 366                   | 109                       | **70.3%** |
| FCP       | 744                   | 312                      | **58.0%** |
| FPT       | 2195                  | 1467                     | **33.2%** |

> Even against a “best-case” default blocking splash, the non-blocking event splash reduces user-perceived wait.

**Chart**  
![Blocking vs Non-Blocking](https://github.com/sankalpchauhan-me/fast-splash-experiment/blob/main/benchmarks/benchmark.png)


## Setup Instructions

### 1. Get TMDB API Key
- Visit [The Movie Database](https://www.themoviedb.org/settings/api)
- Create an account and request a free API key

### 2. Configure API Key
- Copy `local.properties.template` to `local.properties`
- Replace `YOUR_TMDB_API_KEY_HERE` with your actual API key

### 3. Build and Run
- Open in Android Studio
- Sync project
- Run the app


## Tech Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Coroutines & Flow**: Asynchronous programming
- **Dagger Hilt**: Dependency injection framework

### Networking & Data
- **Retrofit**: HTTP client for API calls
- **OkHttp**: HTTP client with interceptors
- **Kotlinx Serialization**: JSON serialization

### UI & Design
- **Material Design 3**: Design system
- **Coil**: Image loading library
- **Lottie**: Animation library
- **Core SplashScreen**: System splash screen API


## Learning Objectives

This project serves as a comprehensive learning resource covering:

### Architecture Patterns
- Clean Architecture implementation
- MVVM pattern with Jetpack Compose
- Repository pattern for data management
- Dependency injection with Dagger Hilt

### Android Development Skills
- Modern UI development with Jetpack Compose
- Reactive programming with Coroutines and Flow
- Network programming with Retrofit
- Performance optimization techniques

### Library Development
- Creating reusable Android libraries
- API design and documentation
- Module separation and dependency management
- Publishing and distribution strategies

### Best Practices
- Code organization and structure
- Error handling and edge cases
- Performance monitoring and optimization

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## If this project helps you in any way, show your love ❤️ by putting a ⭐ on this project ✌️

## Connect with me
If you want to connect or read more about the experiments, check out the links below:
- Blog: [sankalpchauhan.com](https://sankalpchauhan.com/)
- More about me: [sankalpchauhan.me](https://www.sankalpchauhan.me)
