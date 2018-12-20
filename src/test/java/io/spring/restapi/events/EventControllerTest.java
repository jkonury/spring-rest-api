package io.spring.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
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
      .andExpect(jsonPath("free").value(false))
      .andExpect(jsonPath("offline").value(true))
      .andExpect(jsonPath("eventStatus").value(Matchers.is("DRAFT")))
      .andDo(document("create-event",
        links(
          linkWithRel("self").description("link to self"),
          linkWithRel("query-events").description("link to query events"),
          linkWithRel("update-event").description("link to update an existing event"),
          linkWithRel("profile").description("link to profile")
        ),
        requestHeaders(
          headerWithName(HttpHeaders.ACCEPT).description("accept header"),
          headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
        ),
        requestFields(
          fieldWithPath("name").description("Name of new event"),
          fieldWithPath("description").description("description of new event"),
          fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
          fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
          fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
          fieldWithPath("endEventDateTime").description("date time of end of new event"),
          fieldWithPath("location").description("location of new event"),
          fieldWithPath("basePrice").description("base price of new event"),
          fieldWithPath("maxPrice").description("max price of new event"),
          fieldWithPath("limitOfEnrollment").description("limit of enrollment")
        ),
        responseHeaders(
          headerWithName(HttpHeaders.LOCATION).description("Location header"),
          headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
        ),
        responseFields(
          fieldWithPath("id").description("identifier of new event"),
          fieldWithPath("name").description("Name of new event"),
          fieldWithPath("description").description("description of new event"),
          fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
          fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
          fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
          fieldWithPath("endEventDateTime").description("date time of end of new event"),
          fieldWithPath("location").description("location of new event"),
          fieldWithPath("basePrice").description("base price of new event"),
          fieldWithPath("maxPrice").description("max price of new event"),
          fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
          fieldWithPath("free").description("it tells if this event is free or not"),
          fieldWithPath("offline").description("it tells if this event is offline event or not"),
          fieldWithPath("eventStatus").description("event status"),
          fieldWithPath("_links.self.href").description("link to self"),
          fieldWithPath("_links.query-events.href").description("link to query event list"),
          fieldWithPath("_links.update-event.href").description("link to update existing event"),
          fieldWithPath("_links.profile.href").description("link to profile")
        )
      ));
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
      .andExpect(jsonPath("content[0].objectName").exists())
      .andExpect(jsonPath("content[0].defaultMessage").exists())
      .andExpect(jsonPath("content[0].code").exists())
      .andExpect(jsonPath("_links.index").exists());
  }
}
