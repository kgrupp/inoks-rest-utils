package de.kgrupp.nexus.utils.api.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class NexusComponent {
    private String id;
    private String repository;
    private String format;
    private String group;
    private String name;
    private String version;
}
