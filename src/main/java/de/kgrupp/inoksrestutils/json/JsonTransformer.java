package de.kgrupp.inoksrestutils.json;

import org.json.JSONObject;

/**
 * @author Konstantin
 */
public interface JsonTransformer<T> {

    T apply(JSONObject jsonObject);
}
