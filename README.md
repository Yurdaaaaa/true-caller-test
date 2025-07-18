# 📱 Android App

An Android application built using the MVI architecture, leveraging a single-activity approach and Conductor for managing screen navigation and lifecycle. This project demonstrates clean code practices, unidirectional data flow, and reactive UI handling.


## 🔧 Tech Stack

- **Single-Activity Architecture** – all navigation and UI is managed within a single activity
- **MVI Architecture** – unidirectional data flow ensures predictable and testable state management
- **[Conductor](https://github.com/bluelinelabs/Conductor)** – a lightweight navigation and lifecycle framework for managing view controllers
- **[RxKotlin](https://github.com/ReactiveX/RxKotlin)** –  reactive programming built on top of RxJava
- **[RxRelay](https://github.com/JakeWharton/RxRelay)** – event and state handling without completion
- **[Retrofit](https://square.github.io/retrofit/)** – HTTP client for interacting with REST APIs
- **[Dagger](https://github.com/google/dagger)** –  Dependency injection
- **[JUnit](https://github.com/junit-team)** – Unit Testing

👉 For the complete list of libraries, refer to [`libs.versions.toml`](./gradle/libs.versions.toml)

## ⚙️ Build Info

- **Target SDK:** 36
- **Java version:** 17
- **Kotlin version:** 1.8.22
  > Selected for faster build times compared to newer versions

## 🧠 Threading & Use Case Execution

HTML processing use cases run on a **computation thread**, since they involve CPU-bound operations like parsing, character extraction or mapping.  
In contrast, HTML downloading is an **I/O-bound operation** and therefore runs on the **IO thread**, similarly to other asynchronous operations such as:

- database access
- reading/writing to `SharedPreferences`
- file system operations

Threading is explicitly controlled using a shared `SchedulerModule`, allowing clean separation of concerns and predictable performance.

## 🧩 Core Dependency Injection Modules

The `core` module defines three key Dagger modules located in the package `com.zj.hometest.core.di`:

1. [`HtmlUseCaseModule`](core/src/main/java/com/zj/hometest/core/di/HtmlUseCaseModule.kt)  
   Provides all use cases responsible for HTML content processing (parsing, word counting, etc.)

2. [`NetModule`](core/src/main/java/com/zj/hometest/core/di/NetModule.kt)  
   Configures the networking layer, including the HTTP client, API interfaces, and interceptors.

3. [`SchedulerModule`](core/src/main/java/com/zj/hometest/core/di/SchedulerModule.kt)  
   Defines `IO` and `Computation` schedulers injected into use cases and networking manager to ensure correct threading.

These modules promote testability, clarity, and modularity across the application.

## Testing

Unit testing is implemented using JUnit and TestScheduler from io.reactivex.schedulers. The core business logic is thoroughly tested through the following unit test classes:

- [`/core/Html15thCharacterUseCaseTest`](core/src/test/java/com/zj/hometest/core/Html15thCharacterUseCaseTest.kt)
- [`/core/HtmlEvery15thCharacterUseCaseTest`](core/src/test/java/com/zj/hometest/core/HtmlEvery15thCharacterUseCaseTest.kt)
- [`/core/HtmlWordCounterUseCaseTest`](core/src/test/java/com/zj/hometest/core/HtmlWordCounterUseCaseTest.kt)

## 📊 Benchmark: `HtmlWordCounterUseCase`

**Test Scenario:**
- Input: A single large HTML string with **158,678 characters**
- Tokenization resulted in: **9,798 words**
- Phone: [old pal Xiaomi Redmi PRO 9]
- Measurement method: `System.nanoTime()` around `execute(...)`

**Chunking Strategies Tested:**

| Chunk Count | Strategy                   | Observed Time (ms) |
|-------------|----------------------------|--------------------|
| 1 Chunk     | No splitting               | ~1000 ms (slowest) |
| 2 Chunks    | Split in half (len / 2)    | ✅ **~350 ms** (best) |
| 3 Chunks    | Split in thirds (len / 3)  | ~750 ms            |

**Conclusion:**
- The best performance was achieved with **2 chunks**, balancing parallelism and thread overhead.
- **1 chunk** suffers from single-threaded bottleneck.
- **3 chunks** introduce more coordination overhead without better speed.
- This validates the heuristic:  
  → `len < 10_000 → words.chunked(len / 2)`

**Implemented Strategy Justification:**
```kotlin
return when {
    len < 4_000 -> listOf(words)
    len < 10_000 -> words.chunked(len / 2)
    len < 15_000 -> words.chunked(len / 3)
    else -> words.chunked(len / effectiveProcessors)
}