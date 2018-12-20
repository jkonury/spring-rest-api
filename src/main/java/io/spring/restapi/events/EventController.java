package io.spring.restapi.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import io.spring.restapi.common.ErrorResource;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@Slf4j
public class EventController {

  private final EventRepository eventRepository;
  private final ModelMapper modelMapper;
  private final EventValidator eventValidator;

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
    if (errors.hasErrors() || !eventValidator.validate(eventDto, errors)) {
      errors.getAllErrors().forEach(e -> log.error("{} : {}", e.getCode(), e.getDefaultMessage()));
      return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }

    Event event = modelMapper.map(eventDto, Event.class);
    event.update();
    Event newEvent = eventRepository.save(event);
    final ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
    final URI createUri = selfLinkBuilder.toUri();

    final EventResource eventResource = new EventResource(event);
    eventResource.add(linkTo(EventController.class).withRel("query-events"));
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
    return ResponseEntity.created(createUri).body(eventResource);
  }
}
