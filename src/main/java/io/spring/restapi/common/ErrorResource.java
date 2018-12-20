package io.spring.restapi.common;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import io.spring.restapi.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

public class ErrorResource extends Resource<Errors> {

  public ErrorResource(Errors errors, Link... links) {
    super(errors, links);
    add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
  }
}
