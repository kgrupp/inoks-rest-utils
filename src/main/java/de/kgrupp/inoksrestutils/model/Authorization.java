package de.kgrupp.inoksrestutils.model;

import de.kgrupp.inoksrestutils.builder.RestBuilder;
import de.kgrupp.monads.result.Result;

public interface Authorization {
    boolean isBasic();

    void applyAuthorization(RestBuilder builder);

    boolean isValid();

    String getUserName();

    String getAuthorizationTypeIdentifier();

    Result<String> serialize();
}
