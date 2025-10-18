package soat_fiap.siaes.shared.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonPageUtils {

    private JsonPageUtils() {}

    public static <T> List<T> getContentFromPage(ObjectMapper objectMapper, String json, Class<T> clazz) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        return objectMapper.convertValue(
                rootNode.get("content"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }
}