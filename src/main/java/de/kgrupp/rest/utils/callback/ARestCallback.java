package de.kgrupp.rest.utils.callback;

import com.mashape.unirest.http.HttpResponse;
import de.kgrupp.monads.result.Result;

/**
 * @author Konstantin
 */
public interface ARestCallback<T, R> {

    Result<R> completed(HttpResponse<T> response) throws Exception;
}
