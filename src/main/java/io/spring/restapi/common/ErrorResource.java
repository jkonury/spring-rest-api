package io.spring.restapi.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import io.spring.restapi.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

public class ErrorResource extends EntityModel<Errors> {

  public ErrorResource(Errors errors, Link... links) {
    super(errors, links);
    add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
  }
}
