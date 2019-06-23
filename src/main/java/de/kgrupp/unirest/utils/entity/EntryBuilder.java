package de.kgrupp.unirest.utils.entity;

public final class EntryBuilder {

    private EntryBuilder() {
        // utility class
    }

    public static String getEntry(String[] restKeys, String[] ids) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < restKeys.length; i++) {
            if (builder.length() != 0) {
                builder.append("/");
            }
            builder.append(restKeys[i]);
            if (i < ids.length) {
                builder.append("/").append(ids[i]);
            } else {
                break;
            }
        }
        return builder.toString();
    }

    public static String getEntry(String[] restKeys, Object[] ids) {
        String[] stringIds = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            stringIds[i] = ids[i].toString();
        }
        return getEntry(restKeys, stringIds);
    }
}
