package org.leonardonogueira.application.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.dto.Event;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public String toJson(Object object) {
        try{
            return objectMapper.writeValueAsString(object);
        }catch(Exception e){
            log.error("Could not convert object to JSON", e);
        }
        return "";
    }

    public Event toEvent(String json) {
        try{
            return objectMapper.readValue(json, Event.class);
        }catch(Exception e){
            log.error("Could not convert JSON to Event", e);
        }
        return null;
    }
}
