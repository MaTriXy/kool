package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.function.Function;

import org.davidmoten.kool.Maybe;

public final class MaybeSwitchOnError<T> implements Maybe<T> {

    private final Maybe<T> maybe;
    private final Function<? super Throwable, ? extends Maybe<? extends T>> function;

    public MaybeSwitchOnError(Maybe<T> maybe, Function<? super Throwable, ? extends Maybe<? extends T>> function) {
        this.maybe = maybe;
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> get() {
        try {
        return maybe.get();
        } catch (Throwable e) {
            return (Optional<T>) function.apply(e).get();
        }
    }

}
