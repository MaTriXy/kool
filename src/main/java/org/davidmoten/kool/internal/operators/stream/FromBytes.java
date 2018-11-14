package org.davidmoten.kool.internal.operators.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.exceptions.CompositeException;
import org.davidmoten.kool.exceptions.UncheckedException;

import com.github.davidmoten.guavamini.Preconditions;

public final class FromBytes implements Stream<ByteBuffer> {

    private final Callable<? extends InputStream> provider;
    private final int bufferSize;

    public FromBytes(Callable<? extends InputStream> provider, int bufferSize) {
        Preconditions.checkNotNull(provider);
        Preconditions.checkArgument(bufferSize > 0);
        this.provider = provider;
        this.bufferSize = bufferSize;
    }

    @Override
    public StreamIterator<ByteBuffer> iterator() {
        try {
            return new StreamIterator<ByteBuffer>() {

                InputStream is = Preconditions.checkNotNull(provider.call());
                ByteBuffer next;

                @Override
                public boolean hasNext() {
                    load();
                    return next != null;
                }

                @Override
                public ByteBuffer next() {
                    load();
                    ByteBuffer v = next;
                    if (v == null) {
                        throw new NoSuchElementException();
                    }
                    next = null;
                    return v;
                }

                @Override
                public void dispose() {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            throw new UncheckedException(e);
                        }
                        next = null;
                    }
                }

                private void load() {
                    if (is != null && next == null) {
                        byte[] b = new byte[bufferSize];
                            int n;
                            try {
                                n = is.read(b);
                            } catch (IOException e) {
                                Throwable closeError = null;
                                try {
                                    is.close();
                                } catch (Throwable e2) {
                                    closeError = e2;
                                }
                                if (closeError == null) {
                                    throw new UncheckedException(e);
                                } else {
                                    throw new CompositeException(e, closeError);
                                }
                            }
                            if (n == -1) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    throw new UncheckedException(e);
                                }
                                is = null;
                                next = null;
                            } else {
                                next = ByteBuffer.wrap(b, 0, n);
                            }
                    }
                }

            };
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

}