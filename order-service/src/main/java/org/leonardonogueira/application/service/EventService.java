package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.document.Event;
import org.leonardonogueira.application.dto.EventFilterDTO;
import org.leonardonogueira.application.repository.EventRepository;
import org.leonardonogueira.config.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.xml.stream.EventFilter;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository repository;

    public List<Event> findAllByOrderByCreateAtDesc() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Event findEventByFilter(EventFilterDTO filter) {
        validateFilters(filter);
        if (!isEmpty(filter.getOrderId())) {
            return findEventByOrder(filter);
        }else{
            return findEventByTransactionId(filter);
        }
    }

    private Event findEventByTransactionId(EventFilterDTO filter) {
        return repository.findTop1ByTransactionIdOrderByCreatedAtDesc(filter.getTransactionId()).orElseThrow(() ->
                new ValidationException("Transaction id not found"));
    }

    private Event findEventByOrder(EventFilterDTO filter) {
        return repository.findTop1ByOrderIdOrderByCreatedAtDesc(filter.getOrderId()).orElseThrow(() ->
                new ValidationException("Order id not found"));
    }

    public Event saveEvent(Event event) {
        return repository.save(event);
    }

    public void notifyEnding(Event event) {
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        saveEvent(event);
        log.info("Order {} has been saved! Transaction Id {}", event.getOrderId(), event.getTransactionId());
    }

    private void validateFilters(EventFilterDTO filter) {
        if (isEmpty(filter.getOrderId()) && isEmpty(filter.getTransactionId())){
           throw new ValidationException("Order or transaction id is required");
        }
    }
}
