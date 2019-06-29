package de.kgrupp.inoksrestutils.model;

import de.kgrupp.inoksrestutils.RestConstant;
import de.kgrupp.inoksrestutils.builder.RestBuilder;
import de.kgrupp.monads.result.Result;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class BasicAuthorization implements Authorization {
    public static final String IDENTIFIER = "BASIC_AUTH_V1";
    private static final Logger log = Logger.getLogger(BasicAuthorization.class.getSimpleName());
    private final String userName;
    private final String password;
    private final LocalDateTime initialUsage = LocalDateTime.now();

    public BasicAuthorization(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public static Result<Authorization> deserialize(String userName) {
        return Result.of(new BasicAuthorization(userName, null));
    }

    @Override
    public boolean isBasic() {
        return true;
    }

    @Override
    public void applyAuthorization(RestBuilder builder) {
        log.info(() -> "basic auth applied for " + userName);
        builder.withBasicAuth(userName, password);
    }

    @Override
    public boolean isValid() {
        return password != null && LocalDateTime.now().isBefore(initialUsage.plus(RestConstant.BASIC_AUTH_CREDENTIALS_TIMEOUT));
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getAuthorizationTypeIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Result<String> serialize() {
        return Result.of(userName);
    }
}
