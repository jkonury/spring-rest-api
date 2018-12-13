package io.spring.restapi.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {

  private final EventRepository eventRepository;
  private final ModelMapper modelMapper;

  @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
    Event event = modelMapper.map(eventDto, Event.class);
    Event newEvent = eventRepository.save(event);
    final URI createUri = linkTo(methodOn(EventController.class).createEvent(null)).slash(newEvent.getId()).toUri();

    return ResponseEntity.created(createUri).body(event);
  }
}
