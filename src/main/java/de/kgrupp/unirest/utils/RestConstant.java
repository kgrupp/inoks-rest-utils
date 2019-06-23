package de.kgrupp.unirest.utils;

import org.apache.http.HttpStatus;

import java.time.Duration;
import java.util.Set;

public final class RestConstant {

    public static final Set<Integer> ACCEPTED_HTTP_STATUS = Set.of(HttpStatus.SC_OK, HttpStatus.SC_CREATED, HttpStatus.SC_ACCEPTED, HttpStatus.SC_NO_CONTENT);
    public static final Duration BASIC_AUTH_CREDENTIALS_TIMEOUT = Duration.ofSeconds(3600);

    private RestConstant() {
        // utility class
    }
}
