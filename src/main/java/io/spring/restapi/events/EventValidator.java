package io.spring.restapi.events;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

  public boolean validate(EventDto eventDto, Errors errors) {
    boolean isValid = true;

    if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
      errors.reject("wrongPrices", "Values fo prices are wrong");
      isValid = false;
    }

    LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
    if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime())
        || endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())
        || endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
      errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
      isValid = false;
    }

    return isValid;
  }
}
