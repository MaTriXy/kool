package org.davidmoten.kool;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.davidmoten.kool.function.Consumer;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.internal.operators.maybe.MaybeDefer;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnEmpty;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnError;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnValue;
import org.davidmoten.kool.internal.operators.maybe.MaybeError;
import org.davidmoten.kool.internal.operators.maybe.MaybeFlatMap;
import org.davidmoten.kool.internal.operators.maybe.MaybeFlatMapMaybe;
import org.davidmoten.kool.internal.operators.maybe.MaybeFromCallable;
import org.davidmoten.kool.internal.operators.maybe.MaybeIsPresent;
import org.davidmoten.kool.internal.operators.maybe.MaybeIterator;
import org.davidmoten.kool.internal.operators.maybe.MaybeMap;
import org.davidmoten.kool.internal.operators.maybe.MaybeOrElse;
import org.davidmoten.kool.internal.operators.maybe.MaybeSwitchOnError;
import org.davidmoten.kool.internal.operators.maybe.MaybeToStream;
import org.davidmoten.kool.internal.operators.stream.DoOnNext;
import org.davidmoten.kool.internal.util.MaybeImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.davidmoten.guavamini.Preconditions;

public interface Maybe<T> extends StreamIterable<T> {

    Optional<T> get();

    //////////////////
    // Factories
    //////////////////

    public static <T> Maybe<T> of(T value) {
        Preconditions.checkNotNull(value);
        return new MaybeImpl<T>(Optional.of(value));
    }

    public static <T> Maybe<T> fromOptional(Optional<? extends T> optional) {
        if (optional.isPresent()) {
            return Maybe.of(optional.get());
        } else {
            return Maybe.empty();
        }
    }

    public static <T> Maybe<T> fromCallableNullable(Callable<? extends T> callable) {
        return new MaybeFromCallable<T>(callable, true);
    }

    public static <T> Maybe<T> fromCallable(Callable<? extends T> callable) {
        return new MaybeFromCallable<T>(callable, false);
    }

    public static <T> Maybe<T> ofNullable(T value) {
        if (value == null) {
            return empty();
        } else {
            return new MaybeImpl<T>(Optional.of(value));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Maybe<T> empty() {
        return (Maybe<T>) MaybeImpl.EmptyHolder.INSTANCE;
    }

    public static <T> Maybe<T> defer(Callable<? extends Maybe<? extends T>> factory) {
        return new MaybeDefer<T>(factory);
    }

    public static <T> Maybe<T> error(Callable<? extends Throwable> callable) {
        return new MaybeError<T>(callable);
    }

    public static <T> Maybe<T> error(Throwable error) {
        return error(() -> error);
    }

    //////////////////
    // Operators
    //////////////////

    public default <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
        return new MaybeMap<T, R>(this, mapper);
    }

    public default Stream<T> toStream() {
        return new MaybeToStream<T>(this);
    }

    public default <R> Stream<R> flatMap(Function<? super T, ? extends StreamIterable<? extends R>> mapper) {
        return new MaybeFlatMap<T, R>(this, mapper);
    }
    
    public default Maybe<T> doOnValue(Consumer<? super T> consumer) {
        return new MaybeDoOnValue<T>(consumer, this);
    }

    public default Maybe<T> doOnError(Consumer<? super Throwable> consumer) {
        return new MaybeDoOnError<T>(consumer, this);
    }

    public default Maybe<T> doOnEmpty(Runnable action) {
        return new MaybeDoOnEmpty<T>(this, action);
    }

    public default Single<T> orElse(T value) {
        return new MaybeOrElse<T>(this, value);
    }

    default MaybeTester<T> test() {
        return new MaybeTester<T>(this);
    }

    default Single<Boolean> isPresent() {
        return new MaybeIsPresent(this);
    }

    default StreamIterator<T> iterator() {
        return new MaybeIterator<T>(this);
    }

    public default <R> R to(Function<? super Maybe<T>, R> mapper) {
        return mapper.applyUnchecked(this);
    }

    public default void forEach() {
        get();
    }
    
    public default void start() {
        get();
    }
    
    public default void go() {
        get();
    }

    default Maybe<T> switchOnError(Function<? super Throwable, ? extends Maybe<? extends T>> function) {
        return new MaybeSwitchOnError<T>(this, function);
    }

    public default <R> Maybe<R> flatMapMaybe(Function<? super T, ? extends Maybe<? extends R>> mapper) {
        return new MaybeFlatMapMaybe<T, R>(this, mapper);
    }
    
    default Maybe<T> retryWhen(Function<? super Throwable, ? extends Single<?>> function) {
        return toStream().retryWhen(function).maybe();
    }

    default RetryWhenBuilderMaybe<T> retryWhen() {
        return new RetryWhenBuilderMaybe<T>(this);
    }

    default Maybe<T> println() {
        return doOnValue(System.out::println);
    }

}
