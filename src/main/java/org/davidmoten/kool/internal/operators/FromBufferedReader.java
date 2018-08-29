package org.davidmoten.kool.internal.operators;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromBufferedReader implements Stream<String> {

    private final Supplier<BufferedReader> readerFactory;

    public FromBufferedReader(Supplier<BufferedReader> readerFactory) {
        this.readerFactory = readerFactory;
    }

    @Override
    public StreamIterator<String> iterator() {
        return new StreamIterator<String>() {
            
            String line;
            BufferedReader reader = readerFactory.get();

            @Override
            public boolean hasNext() {
                if (line == null) {
                    try {
                        return (line = reader.readLine())!= null;
                    } catch (IOException e) {
                       throw new RuntimeException(e);
                    }
                } else {
                    return true;
                }
            }

            @Override
            public String next() {
                if (line == null) {
                    throw new NoSuchElementException();
                } else {
                    String s = line;
                    line = null;
                    return s;
                }
            }

            @Override
            public void cancel() {
                // do nothing because did not create BufferedReader
                // That is what Using operator is for
            }

        };
    }

}
