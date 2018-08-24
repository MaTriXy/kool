package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.davidmoten.kool.internal.operators.Constants;

public interface Seq<T> extends Iterable<T> {

    boolean isEmpty();

    Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer);

    <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer);

    <R> R reduce(Supplier<R> initialValueFactory, BiFunction<? super R, ? super T, ? extends R> reducer);

    <R> R collect(Supplier<R> factory, BiConsumer<? super R, ? super T> collector);

    <R> Seq<R> map(Function<? super T, ? extends R> function);

    default ArrayList<T> toJavaArrayList() {
        return toJavaArrayList(Constants.DEFAULT_BUFFER_SIZE);
    }

    ArrayList<T> toJavaArrayList(int sizeHint);

    Seq<T> filter(Predicate<? super T> function);

    /**
     * Returns the number of elements in the list. O(N) algorithmic complexity.
     * 
     * @return the number of elements in the list
     */
    long count();

    Seq<T> prepend(T value);

    Seq<T> prepend(T[] values);

    Seq<T> prepend(List<? extends T> values);

    <R> Seq<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function);

    Maybe<T> findFirst(Predicate<? super T> predicate);

    Iterator<T> iterator();
    
    Maybe<T> first();
    
    Maybe<T> last();
    
    Maybe<T> get(int index);

}