package de.kgrupp.nexus.utils.api.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class NexusComponentResult {

    private List<NexusComponent> items;
    private String continuationToken;
}
