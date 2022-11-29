package io.spring.restapi.index;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import io.spring.restapi.events.EventController;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

  @GetMapping({"/api", "/api/"})
  public RepresentationModel<?> index() {
   return new RepresentationModel<>(List.of(linkTo(EventController.class).withRel("events")));
  }
}
