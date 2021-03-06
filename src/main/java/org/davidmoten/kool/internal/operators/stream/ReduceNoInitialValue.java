package org.davidmoten.kool.internal.operators.stream;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiFunction;

import com.github.davidmoten.guavamini.Preconditions;

public final class ReduceNoInitialValue<T> implements Maybe<T> {

    private final BiFunction<? super T, ? super T, ? extends T> reducer;
    private final StreamIterable<T> source;

    public ReduceNoInitialValue(BiFunction<? super T, ? super T, ? extends T> reducer, StreamIterable<T> source) {
        this.reducer = reducer;
        this.source = source;
    }

    @Override
    public Optional<T> get() {
        StreamIterator<T> it = source.iteratorNullChecked();
        T a, b;
        if (it.hasNext()) {
            a = it.nextNullChecked();
        } else {
            return Optional.empty();
        }
        if (it.hasNext()) {
            b = it.nextNullChecked();
        } else {
            return Optional.empty();
        }
        T v = reducer.applyUnchecked(a, b);
        while (it.hasNext()) {
            v = Preconditions.checkNotNull(reducer.applyUnchecked(v, it.nextNullChecked()));
        }
        return Optional.of(v);
    }

}
