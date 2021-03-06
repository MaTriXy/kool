# kool
<a href="https://travis-ci.org/davidmoten/kool"><img src="https://travis-ci.org/davidmoten/kool.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool)<br/>
[![codecov](https://codecov.io/gh/davidmoten/kool/branch/master/graph/badge.svg)](https://codecov.io/gh/davidmoten/kool)


Alternative to `java.util.stream.Stream`:

* is sometimes much faster for synchonronous use ([benchmarks](benchmarks.md))
* has many **more operators** and is generally less verbose
* streams are **reusable**
* **disposes** resources
* is designed for synchronous use only
* models 0..1 and 1 element streams explicitly with **`Maybe`** and **`Single`**.
* does not support streams of nulls (use `Optional` or `Maybe`)
* 35% faster on *Shakespeare Plays Scrabble* [benchmark](benchmarks.md)
* has time-based operators

Status: *available on Maven Central* 

Maven site reports are [here](https://davidmoten.github.io/kool) including [javadoc](https://davidmoten.github.io/kool/apidocs/index.html).

If you need non-blocking and/or asynchronous streaming use [RxJava](https://github.com/ReactiveX/RxJava).

Note also that [IxJava](https://github.com/akarnokd/ixjava) predates this library and is also a pull-based and iterator-based library for reusable streams but does not model `Maybe` and `Single`.

## How to build
```bash
mvn clean install
```

## Getting started
Add this dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>kool</artifactId>
    <version>VERSION_HERE</version>
</dependency>
```
## Example
```java
import org.davidmoten.kool.Stream;

Stream //
  .range(1, 10)
  .flatMap(n -> Stream
      .range(1, n)
      .reduce(0, (a, b) -> a + b))
  .mapWithIndex(1)
  .println()
  .forEach();
```

output:
```
Indexed[index=1, value=1]
Indexed[index=2, value=3]
Indexed[index=3, value=6]
Indexed[index=4, value=10]
Indexed[index=5, value=15]
Indexed[index=6, value=21]
Indexed[index=7, value=28]
Indexed[index=8, value=36]
Indexed[index=9, value=45]
Indexed[index=10, value=55]
```

## Time-based operators
This library has a number of time-based operators. For example `Single.timer("a", 1, TimeUnit.SECONDS).get()` emits `a` one second after starting. 

Use of time-based operators is not optimal for production code because the current thread is blocked (by a `Thread.sleep`). If you are happy to wear a bit of extra complexity but win on efficiency then use `RxJava` for this scenario.

The time-based operators are:
* `Single.timer`
* `Stream.interval`
* `Stream.retryWhen`
* `Stream.delayStart`

## RetryWhen
The `retryWhen` operator differs subtly from the RxJava implementation in that when no more retries will occur the last error is emitted (thrown, possibly wrapped to make unchecked). The operator has a helpful builder for common scenarios:

### Limit retries
```java
stream
  .retryWhen()
  .maxRetries(10)
  .build()
  .forEach();
```
### Set delay between retries
```java
stream
  .retryWhen()
  .maxRetries(6)
  .delay(5, TimeUnit.SECONDS)
  .build()
  .forEach();
```

### Set variable delay between retries
Let's do capped exponential back-off:
```java
stream
  .retryWhen()
  .delays(Stream.of(1L, 2L, 4L, 8L, 16L, 30L).repeatLast(), TimeUnit.SECONDS)
  .build()
  .forEach();
```

## Benchmarks
JMH is used for benchmarks. 

The Shakespeare Plays Scrabble benchmark uses the following factories and operators: `of`, `from`, `chars`, `map`, `flatMap`, `collect`, `reduce`, `take`, `filter`.

## Checklist for new operators
* use `it.nextNullChecked()` instead of `it.next()` and `stream.iteratorNullChecked()` instead of `stream.iterator()`
* wrap calls to function parameters passed to operator with `Preconditions.checkNotNull` where appropriate
* dispose upstream iterables as soon as no longer required (but only if a call to dispose from downstream does not ensue immediately)
* set upstream iterable reference to null (to help gc) when no longer required

