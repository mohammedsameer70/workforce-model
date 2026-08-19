package com.boostphysioclinic.workforceapplication.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PredictionListDeserializer extends JsonDeserializer<List<PredictionResultDTO>> {

    @Override
    public List<PredictionResultDTO> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            List<PredictionResultDTO> result = new ArrayList<>();
            
            for (JsonNode element : arrayNode) {
                if (element.isNumber()) {
                    // Handle case where predictions is just an array of numbers
                    PredictionResultDTO dto = new PredictionResultDTO();
                    dto.setPredictedDemand(element.asDouble());
                    result.add(dto);
                } else if (element.isObject()) {
                    // Handle case where predictions is an array of objects
                    PredictionResultDTO dto = p.getCodec().treeToValue(element, PredictionResultDTO.class);
                    result.add(dto);
                }
            }
            
            return result;
        }
        
        return new ArrayList<>();
    }
}
