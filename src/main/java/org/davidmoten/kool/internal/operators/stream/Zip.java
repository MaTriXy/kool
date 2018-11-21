package org.davidmoten.kool.internal.operators.stream;

import java.util.function.BiFunction;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class Zip<R, S, T> implements Stream<S> {

    private final Stream<T> source1;
    private final Stream<? extends R> source2;
    private final BiFunction<T, R, S> combiner;

    public Zip(Stream<T> source1, Stream<? extends R> source2, BiFunction<T, R, S> combiner) {
        this.source1 = source1;
        this.source2 = source2;
        this.combiner = combiner;
    }

    @Override
    public StreamIterator<S> iterator() {
        return new StreamIterator<S>() {

            StreamIterator<T> a = source1.iteratorChecked();
            StreamIterator<? extends R> b = source2.iteratorChecked();

            @Override
            public boolean hasNext() {
                boolean hasA = a.hasNext();
                boolean hasB = b.hasNext();
                if (hasA && hasB || !hasA && !hasB) {
                    return hasA;
                } else {
                    a.dispose();
                    b.dispose();
                    throw new RuntimeException("streams must have same length to be zipped");
                }
            }

            @Override
            public S next() {
                return combiner.apply(a.nextChecked(), b.nextChecked());
            }

            @Override
            public void dispose() {
                a.dispose();
                b.dispose();
            }

        };
    }

}
