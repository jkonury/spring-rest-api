package io.spring.restapi.events;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.restapi.common.RestDocsConfiguration;
import io.spring.restapi.common.TestDescription;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  public void createNullEvent() throws Exception {
    mockMvc.perform(post("/api/events")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .accept(MediaTypes.HAL_JSON))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("정상적으로 이벤트를 생성하는 테스트")
  public void createEvent() throws Exception {

    EventDto event = EventDto.builder()
      .name("Spring")
      .description("REST API Development with Spring")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
      .basePrice(100)
      .maxPrice(200)
      .limitOfEnrollment(100)
      .location("Seoul")
      .build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("id").exists())
      .andExpect(header().exists(HttpHeaders.LOCATION))
      .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
      .andExpect(jsonPath("id").value(Matchers.not(10)))
      .andExpect(jsonPath("free").value(false))
      .andExpect(jsonPath("offline").value(true))
      .andExpect(jsonPath("eventStatus").value(Matchers.is("DRAFT")))
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.query-events").exists())
      .andExpect(jsonPath("_links.update-event").exists())
      .andDo(document("create-event"));
  }

//  @Test
  @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
  public void createEvent_BadRequest() throws Exception {

    Event event = Event.builder()
      .id(10L)
      .name("Spring")
      .description("REST API Development with Spring")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
      .basePrice(100)
      .maxPrice(200)
      .limitOfEnrollment(100)
      .location("Seoul")
      .free(true)
      .offline(false)
      .eventStatus(EventStatus.PUBLISHED)
      .build();

    mockMvc.perform(post("/api/events")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .accept(MediaTypes.HAL_JSON)
      .content(objectMapper.writeValueAsString(event)))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
  public void createEvent_BadRequest_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    mockMvc.perform(post("/api/events")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .accept(MediaTypes.HAL_JSON)
      .content(objectMapper.writeValueAsString(eventDto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
  public void createEvent_BadRequest_Wrong_Input() throws Exception {

    EventDto event = EventDto.builder()
      .name("Spring")
      .description("REST API Development with Spring")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 27, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
      .basePrice(10000)
      .maxPrice(200)
      .limitOfEnrollment(100)
      .location("Seoul")
      .build();

    mockMvc.perform(post("/api/events")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .accept(MediaTypes.HAL_JSON)
      .content(objectMapper.writeValueAsString(event)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$[0].objectName").exists())
      .andExpect(jsonPath("$[0].defaultMessage").exists())
      .andExpect(jsonPath("$[0].code").exists());
  }
}
