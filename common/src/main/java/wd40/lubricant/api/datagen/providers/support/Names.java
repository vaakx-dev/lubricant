package wd40.lubricant.api.datagen.providers.support;

/** Internal helpers shared by built-in providers. */
public final class Names {

    private Names() {}

    /** Convert {@code golden_dandelion} to {@code Golden Dandelion}. */
    public static String titleCase(String snakeCase) {
        StringBuilder sb = new StringBuilder();
        for (String part : snakeCase.split("_")) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return sb.toString().trim();
    }
}
