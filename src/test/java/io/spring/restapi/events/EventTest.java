package io.spring.restapi.events;

import static org.assertj.core.api.Assertions.assertThat;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
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
  @Parameters({
    "0, 0, true",
    "100, 0, false",
    "0, 100, false",
  })
  public void testFree(int basePrice, int maxPrice, boolean isFree) {
    // Given
    Event event = Event.builder()
      .basePrice(basePrice)
      .maxPrice(maxPrice)
      .build();

    // When
    event.update();

    // Then
    assertThat(event.isFree()).isEqualTo(isFree);
  }

  @Test
  @Parameters
  public void testOffline(String location, boolean isOffline) {
    // Given
    Event event = Event.builder()
      .location(location)
      .build();

    // When
    event.update();

    // Then
    assertThat(event.isOffline()).isEqualTo(isOffline);
  }

  private Object[] parametersForTestOffline() {
    return new Object[] {
      new Object[] {"강남", true},
      new Object[] {null, false},
      new Object[] {"       ", false}
    };
  }
}