package fix.message;

import java.util.HashMap;
import java.util.Map;

public class FixMessageParser {

    public static Map<String, String> parseFixMessage(String fixMessage) {
        Map<String, String> messageMap = new HashMap<>();
        String[] keyValuePairs = fixMessage.split("\\|");
        for (String keyValue : keyValuePairs) {
            String[] pair = keyValue.split("=");
            if (pair.length == 2) {
                messageMap.put(pair[0], pair[1]);
            }
        }
        return messageMap;
    }

    public static String buildFixMessage(Map<String, String> fields) {
        StringBuilder builder = new StringBuilder();
        fields.forEach((key, value) -> builder.append(key).append("=").append(value).append("|"));
        return builder.toString();
    }
}
