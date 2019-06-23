package de.kgrupp.nexus.utils;

import de.kgrupp.monads.result.Result;
import de.kgrupp.nexus.utils.api.NexusRestAdapter;
import de.kgrupp.nexus.utils.api.result.NexusComponent;
import de.kgrupp.unirest.utils.model.Authorization;
import de.kgrupp.unirest.utils.model.BasicAuthorization;
import de.kgrupp.unirest.utils.model.RestLoginData;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class NexusUtilsApplication {

    private static final Logger LOG = Logger.getLogger(NexusUtilsApplication.class.getSimpleName());

    /**
     * Deletes filtered components in a Sonatype Nexus 3 repository according to the defined repository and keyword or group.
     * <p>
     * Directly removes the files from the repository (if the repository allows deletion).
     * <p>
     * Expects as arguments the parameters for the nexus host.
     * args[1] = HOST
     * args[2] = USER_NAME
     * args[3] = PASSWORD
     */
    public static void main(String[] args) {
        RestLoginData loginData = buildRestLoginData(args);
        try (NexusRestAdapter nexusRestAdapter = new NexusRestAdapter(loginData)) {
            removeArpBranchSnapshotsByKeyword(nexusRestAdapter, "my-snapshots", "feature-",
                    component -> component.getName().startsWith("my-example-lib"));
            removeArpBranchSnapshotsByKeyword(nexusRestAdapter, "my-snapshots", "bugfix-",
                    component -> component.getName().startsWith("my-example-lib"));
            removeArpBranchSnapshotsByGroup(nexusRestAdapter, "my-other-snapshots", "com.example", "com.example2");
        }
    }

    private static void removeArpBranchSnapshotsByGroup(NexusRestAdapter nexusRestAdapter, String repository, String... groups) {
        for (String group : groups) {
            workOnComponents(() -> nexusRestAdapter.searchByGroup(repository, group),
                    component -> true,
                    component -> nexusRestAdapter.deleteComponent(component.getId()));
        }
    }

    private static void removeArpBranchSnapshotsByKeyword(NexusRestAdapter nexusRestAdapter, String repository, String keyword, Predicate<NexusComponent> filter) {
        workOnComponents(() -> nexusRestAdapter.searchByKeyword(repository, keyword),
                filter,
                component -> nexusRestAdapter.deleteComponent(component.getId()));
    }

    private static void workOnComponents(
            Supplier<Iterable<Result<List<NexusComponent>>>> componentSupplier,
            Predicate<NexusComponent> filter,
            Consumer<NexusComponent> consumer) {
        Iterable<Result<List<NexusComponent>>> results = componentSupplier.get();
        for (Result<List<NexusComponent>> list : results) {
            long number = list.orElseThrow()
                    .stream()
                    .filter(filter)
                    .map(component -> {
                        LOG.info(String.format("%s - %s - %s - %s",
                                component.getRepository(),
                                component.getGroup(),
                                component.getName(),
                                component.getVersion()));
                        return consumer;
                    }).count();
            LOG.info(String.format("found %s components", number));
        }
    }

    private static RestLoginData buildRestLoginData(String[] args) {
        String url = args[0];
        String user = args[1];
        String pw = args[2];
        Authorization authorization = new BasicAuthorization(user, pw);
        return new RestLoginData(url, authorization);
    }

}
