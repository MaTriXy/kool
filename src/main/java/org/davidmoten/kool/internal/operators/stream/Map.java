package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class Map<T, R> implements Stream<R> {

    private final Function<? super T, ? extends R> function;
    private final StreamIterable<T> source;

    public Map(Function<? super T, ? extends R> function, StreamIterable<T> source) {
        this.function = function;
        this.source = source;

    }

    @Override
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            StreamIterator<T> it = source.iteratorChecked();

            @Override
            public boolean hasNext() {
                return it != null && it.hasNext();
            }

            @Override
            public R next() {
                return function.apply(it.nextChecked());
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                }
            }

        };
    }

}
