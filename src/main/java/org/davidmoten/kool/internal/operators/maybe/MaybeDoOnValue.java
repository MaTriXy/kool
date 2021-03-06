package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.function.Consumer;

import org.davidmoten.kool.Maybe;

public final class MaybeDoOnValue<T> implements Maybe<T> {

    private final Consumer<? super T> consumer;
    private final Maybe<T> maybe;

    public MaybeDoOnValue(Consumer<? super T> consumer, Maybe<T> maybe) {
        this.consumer = consumer;
        this.maybe = maybe;
    }

    @Override
    public Optional<T> get() {
        Optional<T> v = maybe.get();
        if (v.isPresent()) {
            consumer.accept(v.get());
        } 
        return v;
    }

}
