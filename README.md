## Jersey Reactor Extension

Extension for [Jersey](https://eclipse-ee4j.github.io/jersey/) framework providing support for integrating with Reactor Mono/Flux (for example, instead of using Spring WebFlux).

Uses Jersey 3 async support with `@Suspended` and `AsyncResponse`

Heavily inspired by and based on https://github.com/alex-shpak/rx-jersey

### [Documentation](https://github.com/chrisribble/jersey-ext-reactor)

## Licence
[MIT](LICENSE)

## Introduction
jersey-ext-reactor is a simple extension for Jersey which makes it possible to return Reactor types (currently `Mono` and `Flux`) directly from your Jersey resource endpoints while preserving asynchronous processing. This allows you to use Reactor without being stuck using Spring Webflux controllers or being resigned to add boilerplate to every resource method to subscribe to the `Mono`/`Flux` and interact with `@Suspended AsyncResponse` explicitly.

### How to:
Add the coordinates to your build.gradle file
```
implementation "io.github.chrisribble:jersey-reactor-server:0.0.1"
```
Register the feature in your Jersey config:
```
register(ReactorServerFeature.class);
```
See example directory for complete demonstration in a Spring Boot 3 application.


## Required Java versions
* Runtime: Java >= 11
* Build: Java >= 17

## Building
```
./gradlew build
```

## Publishing
```
./gradlew publishToCentralPortal -Psign=true
```

## Features
- [x] Mono<T> resource method support
- [x] Flux<T> resource method support (via Mono<List<T>>)

## Roadmap
- [ ] Flux resource method streaming support
- [ ] Client support
