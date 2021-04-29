package io.spring.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.spring.restapi.accounts.Account;
import io.spring.restapi.accounts.CurrentUser;
import io.spring.restapi.common.ErrorResource;
import io.spring.restapi.index.IndexController;
import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class EventController {

  private final EventRepository eventRepository;
  private final ModelMapper modelMapper;
  private final EventValidator eventValidator;

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                    Errors errors,
                                    @CurrentUser Account account) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("{}", authentication);

    if (errors.hasErrors() || !eventValidator.validate(eventDto, errors)) {
      errors.getAllErrors().forEach(e -> log.error("{} : {}", e.getCode(), e.getDefaultMessage()));
      final EntityModel<Errors> errorResource = ErrorResource.of(errors);
      errorResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
      return ResponseEntity.badRequest().body(errorResource);
    }

    Event event = modelMapper.map(eventDto, Event.class);
    event.update();
    event.setManager(account);
    Event newEvent = eventRepository.save(event);
    final WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
    final URI createUri = selfLinkBuilder.toUri();

    final EntityModel<Event> eventResource = EventResource.of(event);
    eventResource.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    eventResource.add(linkTo(EventController.class).withRel("query-events"));
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
    return ResponseEntity.created(createUri).body(eventResource);
  }

  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<Event>>> queryEvents(Pageable pageable,
                                    PagedResourcesAssembler<Event> assembler,
                                    @CurrentUser Account account) {

    Page<Event> page = eventRepository.findAll(pageable);
    PagedModel<EntityModel<Event>> pagedResources = assembler.toModel(page, event -> {
      final EntityModel<Event> eventResource = EventResource.of(event);
      eventResource.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
      return eventResource;
    });

    if (account != null) {
      pagedResources.add(linkTo(EventController.class).withRel("create-event"));
    }

    pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));

    return ResponseEntity.ok(pagedResources);
  }

  @GetMapping("/{id}")
  public ResponseEntity getEvent(@PathVariable Long id,
                                 @CurrentUser Account account) {
    Optional<Event> optionalEvent = eventRepository.findById(id);
    if (optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Event event = optionalEvent.get();
    final EntityModel<Event> eventResource = EventResource.of(event);
    eventResource.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

    if (event.getManager().equals(account)) {
      eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
    }

    return ResponseEntity.ok(eventResource);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateEvent(@PathVariable Long id,
                                    @RequestBody @Valid EventDto eventDto,
                                    Errors errors,
                                    @CurrentUser Account account) {
    if (errors.hasErrors() || !eventValidator.validate(eventDto, errors)) {
      errors.getAllErrors().forEach(e -> log.error("{} : {}", e.getCode(), e.getDefaultMessage()));
      final EntityModel<Errors> errorResource = ErrorResource.of(errors);
      errorResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
      return ResponseEntity.badRequest().body(errorResource);
//      return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }
    
    Optional<Event> optionalEvent = eventRepository.findById(id);
    if (optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Event existingEvent = optionalEvent.get();
    if (!existingEvent.getManager().equals(account)) {
      return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    modelMapper.map(eventDto, existingEvent);
    Event event = eventRepository.save(existingEvent);

    final EntityModel<Event> eventResource = EventResource.of(event);
    eventResource.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
    return ResponseEntity.ok(eventResource);
  }
}
