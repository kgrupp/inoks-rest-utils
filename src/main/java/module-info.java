module inoks.rest.utils {
    requires transitive inoks.java.utils;

    requires java.logging;
    requires lombok;
    requires unirest.java;
    requires httpcore;
    requires httpasyncclient;
    requires json;

    exports de.kgrupp.inoksrestutils;
    exports de.kgrupp.inoksrestutils.builder;
    exports de.kgrupp.inoksrestutils.callback;
    exports de.kgrupp.inoksrestutils.entity;
    exports de.kgrupp.inoksrestutils.exception;
    exports de.kgrupp.inoksrestutils.json;
    exports de.kgrupp.inoksrestutils.model;
}