package de.kgrupp.unirest.utils.builder;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import de.kgrupp.monads.result.Result;
import de.kgrupp.unirest.utils.RestConstant;
import de.kgrupp.unirest.utils.callback.ARestCallback;
import de.kgrupp.unirest.utils.callback.RestJsonCallback;
import de.kgrupp.unirest.utils.exception.RestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestBuilder {

    private static final Logger log = Logger.getLogger(RestBuilder.class.getSimpleName());

    private final Method method;
    private final String fullUrl;

    private List<Header> headers = new ArrayList<>();
    private String basicAuthUser;
    private String basicAuthPw;

    private Map<String, String> parameters = new HashMap<>();
    private Body body;

    private Set<Integer> acceptedHttpStatus = new HashSet<>(RestConstant.ACCEPTED_HTTP_STATUS);

    private RestBuilder(Method method, String fullUrl) {
        this.method = method;
        this.fullUrl = fullUrl;
    }

    public static RestBuilder build(Method method, String fullUrl) {
        log.info(() -> method.name() + " " + fullUrl);
        return new RestBuilder(method, fullUrl);
    }

    public RestBuilder withBasicAuth(String userName, String password) {
        this.basicAuthUser = userName;
        this.basicAuthPw = password;
        return this;
    }

    public RestBuilder withHeader(Header header) {
        this.headers.add(header);
        return this;
    }

    public RestBuilder withParameter(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public RestBuilder withBody(Body body) {
        this.body = body;
        return this;
    }

    public RestBuilder withAdditionalAcceptedHttpStatus(int httpStatus) {
        acceptedHttpStatus.add(httpStatus);
        return this;
    }

    public <R> Result<R> waitForJsonResponse(ARestCallback<JsonNode, R> callback) {
        return waitForResponseWithLogging(build().asJsonAsync(), callback);
    }

    public <R> Result<R> waitForStringResponse(ARestCallback<String, R> callback) {
        return waitForResponseWithLogging(build().asStringAsync(), callback);
    }

    public <T, R> Result<R> waitForJsonResponse(RestJsonCallback<T, R> callback) {
        return waitForResponseWithLogging(build().asStringAsync(), callback);
    }

    private <T, R> Result<R> waitForResponseWithLogging(Future<HttpResponse<T>> responseFuture, ARestCallback<T, R> callback) {
        Result<R> result = waitForResponse(responseFuture, callback);
        if (result.isError()) {
            log.info(result::getErrorMessage);
        }
        return result;
    }

    private <T, R> Result<R> waitForResponse(Future<HttpResponse<T>> responseFuture, ARestCallback<T, R> callback) {
        try {
            HttpResponse<T> response = responseFuture.get();
            return handleResponse(response, callback);
        } catch (ExecutionException e) {
            return Result.fail(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.fail(e.getMessage());
        }
    }

    private <T, R> Result<R> handleResponse(HttpResponse<T> response, ARestCallback<T, R> callback) {
        try {
            log.info(() -> "Response: Http Status " + response.getStatus());
            if (acceptedHttpStatus.contains(response.getStatus())) {
                return callback.completed(response);
            } else {
                return Result.fail("Request failed with code " + response.getStatus() + ": " + response.getBody());
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Reading HttpResponse failed!", e);
            return Result.fail("Reading HttpResponse failed!");
        }
    }

    @Override
    public String toString() {
        return "RestBuilder{" +
                "method=" + method +
                ", fullUrl='" + fullUrl + '\'' +
                ", headers=" + headers +
                ", basicAuthUser='" + basicAuthUser + '\'' +
                ", basicAuthPw='" + basicAuthPw + '\'' +
                ", parameters=" + parameters +
                ", body=" + body +
                '}';
    }

    private BaseRequest build() {
        return new RequestBuilder().build();
    }

    private class RequestBuilder {

        private static final String NOT_SUPPORTED_BY = "not supported by ";
        BaseRequest uniRequest;

        RequestBuilder() {
            uniRequest = buildBaseRequest();
        }

        BaseRequest build() {
            headers.forEach(this::applyHeader);
            applyBasicAuth();
            parameters.forEach(this::applyParameter);
            applyBody();
            return uniRequest;
        }

        private BaseRequest buildBaseRequest() {
            switch (method) {
                case GET:
                    return Unirest.get(fullUrl);
                case POST:
                    return Unirest.post(fullUrl);
                case PUT:
                    return Unirest.put(fullUrl);
                case PATCH:
                    return Unirest.patch(fullUrl);
                case DELETE:
                    return Unirest.delete(fullUrl);
            }
            throw new RestException("Method was not set.");
        }

        private void applyHeader(Header header) {
            applyHeader(header.getKey(), header.getValue(), header.isShowValueInLog());
        }

        private void applyHeader(String key, String value, boolean logValue) {
            log.info(() -> String.format("Header: %s=%s", key, logValue ? value : "(hidden)"));
            if (uniRequest instanceof HttpRequest) {
                uniRequest = ((HttpRequest) uniRequest).header(key, value);
            } else {
                throw new RestException(NOT_SUPPORTED_BY + uniRequest.getClass().getName());
            }
        }

        private void applyBasicAuth() {
            if (basicAuthUser != null && basicAuthPw != null) {
                log.info(() -> "BasicAuth for user (" + basicAuthUser + ") applied");
                if (uniRequest instanceof HttpRequest) {
                    uniRequest = ((HttpRequest) uniRequest).basicAuth(basicAuthUser, basicAuthPw);
                } else if (uniRequest instanceof MultipartBody) {
                    uniRequest = ((MultipartBody) uniRequest).basicAuth(basicAuthUser, basicAuthPw);
                } else {
                    throw new RestException(NOT_SUPPORTED_BY + uniRequest.getClass().getName());
                }
            }
        }

        private void applyParameter(String name, String value) {
            log.info(() -> "Parameter: " + name + "=" + value);
            if (uniRequest instanceof GetRequest) {
                uniRequest = ((GetRequest) uniRequest).queryString(name, value);
            } else if (uniRequest instanceof HttpRequestWithBody) {
                uniRequest = ((HttpRequestWithBody) uniRequest).queryString(name, value);
            } else {
                throw new RestException(NOT_SUPPORTED_BY + uniRequest.getClass().getName());
            }
        }

        private void applyBody() {
            if (body != null) {
                log.info(() -> String.format("Body applied (Content-Type: %s)%n%s", body.getBodyType(), body.getBodyString()));
                final String bodyType = body.getBodyType();
                if (bodyType != null) {
                    applyHeader(new Header("Content-Type", bodyType, true));
                }
                if (uniRequest instanceof HttpRequestWithBody) {
                    uniRequest = ((HttpRequestWithBody) uniRequest).body(body.getBodyString());
                } else if (uniRequest instanceof RequestBodyEntity) {
                    uniRequest = ((RequestBodyEntity) uniRequest).body(body.getBodyString());
                } else {
                    throw new RestException(NOT_SUPPORTED_BY + uniRequest.getClass().getName());
                }
            }
        }
    }
}
