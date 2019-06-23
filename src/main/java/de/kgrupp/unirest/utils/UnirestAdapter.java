package de.kgrupp.unirest.utils;

import com.mashape.unirest.http.Unirest;
import de.kgrupp.monads.result.Result;
import de.kgrupp.unirest.utils.builder.RestBuilder;
import de.kgrupp.unirest.utils.callback.RestJsonCallback;
import de.kgrupp.unirest.utils.exception.RestException;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class UnirestAdapter implements AutoCloseable {

    private static final UnirestAdapter instance = new UnirestAdapter();

    private CloseableHttpAsyncClient httpAsyncClient;

    private UnirestAdapter() {
        // singleton class
    }

    public static UnirestAdapter getInstance() {
        return instance;
    }

    public synchronized void init() {
        if (httpAsyncClient == null) {
            Unirest.setTimeouts(10, 10);
            httpAsyncClient = HttpAsyncClients.createMinimal();
            Unirest.setAsyncHttpClient(httpAsyncClient);
        }
    }

    public synchronized void close() {
        if (httpAsyncClient != null) {
            try {
                httpAsyncClient.close();
                Unirest.shutdown();
            } catch (IOException e) {
                throw new RestException(e);
            }
            Unirest.clearDefaultHeaders();
        }
    }

    public <L, T> Iterable<Result<List<T>>> buildIterable(Class<L> clazz,
                                                          RestBuilder builder,
                                                          Predicate<L> hasNext,
                                                          Function<L, List<T>> getElements,
                                                          Consumer<L> applyParameters) {
        return () -> new Iterator<>() {
            private boolean isInitial = true;
            private L lastItem = null;
            private RestBuilder request = builder;

            @Override
            public boolean hasNext() {
                return isInitial || (lastItem != null && hasNext.test(lastItem));
            }

            @Override
            public Result<List<T>> next() {
                isInitial = false;
                if (lastItem != null) {
                    applyParameters.accept(lastItem);
                    lastItem = null;
                }
                return request.waitForJsonResponse(RestJsonCallback.of(clazz))
                        .map(result -> {
                            lastItem = result;
                            return getElements.apply(result);
                        });
            }
        };
    }
}
