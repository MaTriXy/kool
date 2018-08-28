package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Function;

import org.davidmoten.kool.Stream;

public final class Map<T, R> implements Stream<R> {

    private final Function<? super T, ? extends R> function;
    private final Iterable<T> source;

    public Map(Function<? super T, ? extends R> function, Iterable<T> source) {
        this.function = function;
        this.source = source;

    }

    @Override
    public Iterator<R> iterator() {
        return new Iterator<R>() {

            final Iterator<T> it = source.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public R next() {
                return function.apply(it.next());
            }

        };
    }

}
