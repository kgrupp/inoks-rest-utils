package de.kgrupp.unirest.utils.callback;

import com.mashape.unirest.http.HttpResponse;
import de.kgrupp.monads.result.Result;

import java.io.InputStream;

/**
 * @author Konstantin
 */
public interface RestSimpleCallback<T, R> extends ARestCallback<T, R> {

    @Override
    default Result<R> completed(HttpResponse<T> response) throws Exception {
        return Result.of(this.transform(response.getRawBody()));
    }

    R transform(InputStream responseBody) throws Exception;
}
