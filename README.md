## Jersey Reactor Extension

Extension for [Jersey](https://eclipse-ee4j.github.io/jersey/) framework providing support for integrating with Reactor Mono/Flux (for example, instead of using Spring WebFlux).

Uses Jersey 3 async support with `@Suspended` and `AsyncResponse`

Heavily inspired by and based on https://github.com/alex-shpak/rx-jersey

### [Documentation](https://github.com/chrisribble/jersey-ext-reactor)

## Features
- [x] Mono<T> resource method support
- [x] Flux<T> resource method support (via Mono<List<T>>)

## Roadmap
- [ ] Flux resource method streaming support
- [ ] Client support

## Licence
[MIT](LICENSE)

## Required Java versions
* Runtime: Java >= 11
* Build: Java >= 21
  * Publish plugin requires Java 21 currently; hopefully this will be fixed soon

## Building
```
./gradlew build
```

## Publishing
```
./gradlew publishToCentralPortal -Psign=true
```
