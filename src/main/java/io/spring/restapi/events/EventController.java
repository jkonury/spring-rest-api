package io.spring.restapi.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class EventController {

  @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity createEvent(@RequestBody Event event) {

    final URI createUri = linkTo(methodOn(EventController.class).createEvent(null)).slash("{id}").toUri();
    event.setId(10L);

    return ResponseEntity.created(createUri).body(event);
  }
}
