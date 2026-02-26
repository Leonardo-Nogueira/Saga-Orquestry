package org.leonardonogueira.application.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.document.Event;
import org.leonardonogueira.application.dto.EventFilterDTO;
import org.leonardonogueira.application.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventContoller {

    private final EventService eventService;

    @GetMapping
    public List<Event> getEventOrdered() {
        log.info("Received order request");
        return eventService.findAllByOrderByCreateAtDesc();
    }

    @GetMapping("filter")
    public Event getEventByFilter(EventFilterDTO filter) {
        log.info("Received event request");
        return eventService.findEventByFilter(filter);
    }
}
