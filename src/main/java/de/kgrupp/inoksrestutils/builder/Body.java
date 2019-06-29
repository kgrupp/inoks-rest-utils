package de.kgrupp.inoksrestutils.builder;

import de.kgrupp.inoksjavautils.transform.JsonUtils;
import de.kgrupp.monads.result.Result;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Body {

    private static final Logger log = Logger.getLogger(Body.class.getSimpleName());

    private static final String APPLICATION_JSON = "application/json";

    private final String bodyString;
    private final String bodyType;

    private Body(String bodyString) {
        this.bodyString = bodyString;
        this.bodyType = APPLICATION_JSON;
    }

    public static Body json(String body) {
        return new Body(body);
    }

    public static Body toJson(Object body) {
        Result<String> result = JsonUtils.convertToJsonString(body);
        result.consumeOrThrow(bodyString -> log.log(Level.INFO, () -> "body=" + bodyString));
        return json(result.getObject());
    }

    String getBodyString() {
        return bodyString;
    }

    String getBodyType() {
        return bodyType;
    }
}
