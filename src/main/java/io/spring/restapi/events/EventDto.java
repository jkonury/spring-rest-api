package io.spring.restapi.events;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

  @NotEmpty
  private String name;
  @NotEmpty
  private String description;
  @FutureOrPresent
  private LocalDateTime beginEnrollmentDateTime;
  @FutureOrPresent
  private LocalDateTime closeEnrollmentDateTime;
  @FutureOrPresent
  private LocalDateTime beginEventDateTime;
  @FutureOrPresent
  private LocalDateTime endEventDateTime;
  private String location; // (optional) 이게 없으면 온라인 모임
  @Min(0)
  private int basePrice; // (optional)
  @Min(0)
  private int maxPrice; // (optional)
  @Min(0)
  private int limitOfEnrollment;
}
