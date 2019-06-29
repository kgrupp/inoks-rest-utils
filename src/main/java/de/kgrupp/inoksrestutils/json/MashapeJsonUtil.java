package de.kgrupp.inoksrestutils.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MashapeJsonUtil {

    private MashapeJsonUtil() {
        // utility class
    }

    public static <T> List<T> transform(JSONArray array, JsonTransformer<T> transformer) {
        if (array == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            result.add(transformer.apply(object));
        }
        return result;
    }
}
