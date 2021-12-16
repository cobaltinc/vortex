<h1 align='center'>
  Vortex 🌪️
</h1>

<p align="center"><strong>Kotlin functional extensions for Spring Webflux</strong></p>

<p align='center'>
  <a href="https://cobalt.run">
    <img src="https://cobalt-static.s3.ap-northeast-2.amazonaws.com/cobalt-badge.svg" />
  </a>
  <a href="">
    <img src='https://img.shields.io/maven-central/v/run.cobalt/vortex' alt='Latest version'>
  </a>
  <a href="https://github.com/cobaltinc/vortex/blob/master/.github/CONTRIBUTING.md">
    <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg" alt="PRs welcome" />
  </a>
</p>

## :rocket: Getting started
depend via Maven:
```xml
<dependency>
  <groupId>run.cobalt</groupId>
  <artifactId>vortex</artifactId>
  <version>0.0.2</version>
</dependency>
```
or Gradle:
```kotlin
implementation("run.cobalt:vortex:0.0.2")
```

## :sparkles: Usecase
### Condition
#### throwIf
```kotlin
Mono
  .just(inputNumber)
  .throwIf(Exception("Only take odd numbers")) { it % 2 == 0 }
```
Or,
```kotlin
this.userRepository
  .findById(userId)
  .throwIf(Exception("Banned user")) { it.banned }
```

#### iif
```kotlin
Mono
  .just(inputNumber)
  .iif(Mono.just("Even"), Mono.just("Odd")) { it % 2 == 0 }
```

### Pipe
```kotlin
Flux
  .fromIterable(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
  .pipe(
    MonoExt.filter { it % 2 == 0 },
    MonoExt.reduce { t, t2 -> t + t2 }
    MonoExt.map { it.toString() }
  )
```
Or,
```kotlin
pipe(
  Flux.fromIterable(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
  FluxExt.filter { it % 2 == 0 },
  FluxExt.reduce { t, t2 -> t + t2 }
  MonoExt.map { it.toString() }
)
```

### Function Composite
```kotlin
val sumToString = FluxExt.reduce<Int> { a, b -> a + b } then MonoExt.map { it.toString() }
Flux
  .fromIterable(listOf(1, 2, 3, 4, 5))
  .pipe(
    FluxExt.map { it.inc() },
    sumToString
  )
```

## :page_facing_up: License

Bloom is made available under the [MIT License](./LICENSE).
