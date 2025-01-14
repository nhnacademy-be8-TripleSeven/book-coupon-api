package com.nhnacademy.bookapi.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PageImplDeserializer extends JsonDeserializer<PageImpl<?>> {

    @Override
    public PageImpl<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);

        // Content
        JsonNode contentNode = node.get("content");
        List<?> content = parser.getCodec().treeToValue(contentNode, List.class);

        // Page metadata
        int page = node.get("number").asInt();
        int size = node.get("size").asInt();
        long total = node.get("totalElements").asLong();

        return new PageImpl<>(content, PageRequest.of(page, size), total);
    }
}