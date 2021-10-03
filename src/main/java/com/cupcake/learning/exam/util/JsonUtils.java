package com.cupcake.learning.exam.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JsonUtils {
    private ObjectMapper mapper;

    public JsonUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<String> extractImageLinksFromJsonText(String text) {
        try {
            JsonNode jsonNode = mapper.readTree(text);
            var imageLinks = new ArrayList<String>();
            extractImageLinks(imageLinks, jsonNode);
            return imageLinks;
        }
        catch (IllegalArgumentException | JsonProcessingException e) {
            return List.of();
        }
    }

    private void extractImageLinks(List<String> links, JsonNode jsonNode) {
        if (jsonNode.isArray() || jsonNode.isObject()) {
            for (JsonNode element : jsonNode) {
                extractImageLinks(links, element);
            }
        }

        if (jsonNode.isObject()) {
            var linkTextNode = jsonNode.get("image");
            if (linkTextNode != null && linkTextNode.isTextual()) {
                var link = linkTextNode.toString();
                if (!link.isBlank()) {
                    links.add(link);
                }
            }
        }
    }
}
