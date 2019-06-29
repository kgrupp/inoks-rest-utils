package de.kgrupp.nexus.utils.api;

import de.kgrupp.inoksrestutils.entity.EntryBuilder;

public enum NexusEntity {

    COMPONENTS("components"),
    SEARCH("search");

    private static final String NEXUS_REST_PREFIX = "/service/rest/v1/";

    private String[] restKeys;

    NexusEntity(String... restKeys) {
        this.restKeys = restKeys;
    }

    public String getRestKey() {
        return restKeys[restKeys.length - 1];
    }

    public String getEntry(String... id) {
        return NEXUS_REST_PREFIX + EntryBuilder.getEntry(restKeys, id);
    }
}
