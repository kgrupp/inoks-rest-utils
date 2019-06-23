package de.kgrupp.unirest.utils.callback;

import com.mashape.unirest.http.HttpResponse;
import de.kgrupp.inoksjavautils.transform.JsonUtils;
import de.kgrupp.monads.result.Result;

import java.util.function.Function;

/**
 * @author Konstantin
 */
public class RestJsonCallback<T, R> implements ARestCallback<String, R> {

    private final Class<T> responseClass;
    private final Function<T, Result<R>> transformer;

    private RestJsonCallback(Class<T> responseClass, Function<T, Result<R>> transformer) {
        this.responseClass = responseClass;
        this.transformer = transformer;
    }

    public static <T, R> RestJsonCallback<T, R> of(Class<T> responseClass, Function<T, R> transformer) {
        return new RestJsonCallback<>(responseClass, object -> Result.of(transformer.apply(object)));
    }

    public static <T> RestJsonCallback<T, T> of(Class<T> responseClass) {
        return new RestJsonCallback<>(responseClass, Result::of);
    }

    @Override
    public Result<R> completed(HttpResponse<String> response) {
        Result<T> convertedResponse = JsonUtils.convertToObject(response.getBody(), responseClass);
        return convertedResponse.flatMap(transformer);
    }
}
