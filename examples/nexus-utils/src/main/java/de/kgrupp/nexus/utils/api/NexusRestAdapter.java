package de.kgrupp.nexus.utils.api;

import de.kgrupp.inoksrestutils.UnirestAdapter;
import de.kgrupp.inoksrestutils.builder.Method;
import de.kgrupp.inoksrestutils.builder.RestBuilder;
import de.kgrupp.inoksrestutils.callback.RestSimpleCallback;
import de.kgrupp.inoksrestutils.exception.RestException;
import de.kgrupp.inoksrestutils.model.Authorization;
import de.kgrupp.inoksrestutils.model.RestLoginData;
import de.kgrupp.monads.result.Result;
import de.kgrupp.nexus.utils.api.result.NexusComponent;
import de.kgrupp.nexus.utils.api.result.NexusComponentResult;

import java.util.List;

public class NexusRestAdapter implements AutoCloseable {

    private RestLoginData loginData;

    public NexusRestAdapter(RestLoginData loginData) {
        UnirestAdapter.getInstance().init();
        this.loginData = loginData;
    }

    private static RestBuilder build(Method method, RestLoginData loginData, String endpoint) {
        RestBuilder restBuilder = RestBuilder.build(method, loginData.getRestUrl() + endpoint);
        Authorization authorization = loginData.getAuthorization();
        if (authorization.isBasic()) {
            authorization.applyAuthorization(restBuilder);
        } else {
            throw new RestException("Nexus does not support authorization other than basic auth.");
        }
        return restBuilder;
    }

    public boolean isConnected() {
        return loginData != null && loginData.getAuthorization().isValid();
    }

    public Result<Void> disconnect() {
        this.loginData = null;
        return Result.emptySuccess();
    }

    @Override
    public void close() {
        if (isConnected()) {
            disconnect();
        }
        UnirestAdapter.getInstance().close();
    }

    public Iterable<Result<List<NexusComponent>>> searchByGroup(String repository, String group) {
        RestBuilder request = build(Method.GET, loginData, NexusEntity.SEARCH.getEntry())
                .withParameter(NexusRestApiConstant.REPOSITORY, repository)
                .withParameter(NexusRestApiConstant.GROUP, group);
        return buildIterable(request);
    }

    public Iterable<Result<List<NexusComponent>>> searchByKeyword(String repository, String keyword) {
        RestBuilder request = build(Method.GET, loginData, NexusEntity.SEARCH.getEntry())
                .withParameter(NexusRestApiConstant.REPOSITORY, repository)
                .withParameter(NexusRestApiConstant.KEYWORD, keyword);
        return buildIterable(request);
    }

    public Iterable<Result<List<NexusComponent>>> searchByVersion(String repository, String version) {
        RestBuilder request = build(Method.GET, loginData, NexusEntity.SEARCH.getEntry())
                .withParameter(NexusRestApiConstant.REPOSITORY, repository)
                .withParameter(NexusRestApiConstant.VERSION, version);
        return buildIterable(request);
    }

    private Iterable<Result<List<NexusComponent>>> buildIterable(RestBuilder request) {
        return UnirestAdapter.getInstance()
                .buildIterable(NexusComponentResult.class,
                        request,
                        result -> result.getContinuationToken() != null,
                        NexusComponentResult::getItems,
                        result -> request.withParameter(NexusRestApiConstant.CONTINUATION_TOKEN, result.getContinuationToken()));
    }

    public Result<Void> deleteComponent(String componentId) {
        RestBuilder request = build(Method.DELETE, loginData, NexusEntity.COMPONENTS.getEntry(componentId));
        return request.waitForStringResponse((RestSimpleCallback<String, Void>) response -> null);
    }
}
