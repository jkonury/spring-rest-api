package io.spring.restapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.spring.restapi.accounts.Account;
import io.spring.restapi.accounts.AccountSerializer;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@Entity
public class Event {

  @Id @GeneratedValue private Long id;
  private String name;
  private String description;
  private LocalDateTime beginEnrollmentDateTime;
  private LocalDateTime closeEnrollmentDateTime;
  private LocalDateTime beginEventDateTime;
  private LocalDateTime endEventDateTime;
  private String location; // (optional) 이게 없으면 온라인 모임
  private int basePrice; // (optional)
  private int maxPrice; // (optional)
  private int limitOfEnrollment;
  private boolean offline;
  private boolean free;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private EventStatus eventStatus = EventStatus.DRAFT;

  @ManyToOne
  @JsonSerialize(using = AccountSerializer.class)
  private Account manager;

  public void update() {
    // Update free
    this.free = this.basePrice == 0 && this.maxPrice == 0;
    // Update offline
    this.offline = this.location != null && !this.location.isBlank();
  }
}
