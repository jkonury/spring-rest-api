package io.spring.restapi.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EventTest {

  @Test
  public void builder() {
    final Event event = Event.builder()
      .name("Spring REST API")
      .description("REST API development with Spring")
      .build();
    assertThat(event).isNotNull();
  }

  @Test
  public void javaBean() {
    Event event = new Event();
    String name = "event";
    String description = "Spring";

    event.setName(name);
    event.setDescription(description);

    assertThat(event.getName()).isEqualTo(name);
    assertThat(event.getDescription()).isEqualTo(description);
  }

  @Test
  public void testFree() {
    Event event = Event.builder()
      .basePrice(0)
      .maxPrice(0)
      .build();

    event.update();

    assertThat(event.isFree()).isTrue();
  }
}