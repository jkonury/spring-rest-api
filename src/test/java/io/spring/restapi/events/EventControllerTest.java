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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.spring.restapi.accounts.Account;
import io.spring.restapi.accounts.AccountRepository;
import io.spring.restapi.accounts.AccountRole;
import io.spring.restapi.accounts.AccountService;
import io.spring.restapi.common.BaseControllerTest;
import io.spring.restapi.common.TestDescription;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

public class EventControllerTest extends BaseControllerTest {

  @Autowired
  EventRepository eventRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  AccountService accountService;

  @Before
  public void setUp() {
    eventRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void createNullEvent() throws Exception {
    mockMvc.perform(post("/api/events")
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
          fieldWithPath("manager").description("event manager"),
          fieldWithPath("_links.self.href").description("link to self"),
          fieldWithPath("_links.query-events.href").description("link to query event list"),
          fieldWithPath("_links.update-event.href").description("link to update existing event"),
          fieldWithPath("_links.profile.href").description("link to profile")
        )
      ));
  }

  private String getBearerToken() throws Exception {
    return "Bearer " + getAccessToken();
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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

  private String getAccessToken() throws Exception {
    // given
    final String email = "test@email.com";
    final String password = "password";
    Account account = Account.builder()
      .email(email)
      .password(password)
      .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
      .build();

    accountService.saveAccount(account);

    String clientId = "myApp";
    String clientSecret = "pass";

    final ResultActions perform = mockMvc.perform(post("/oauth/token")
      .with(httpBasic(clientId, clientSecret))
      .param("username", email)
      .param("password", password)
      .param("grant_type", "password"));

    final String responseBody = perform.andReturn().getResponse().getContentAsString();
    Jackson2JsonParser parser = new Jackson2JsonParser();

    return parser.parseMap(responseBody).get("access_token").toString();
  }

  @Test
  @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEvents() throws Exception {
    // given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // when & then
    mockMvc.perform(get("/api/events")
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .param("page", "1")
        .param("size", "10")
        .param("sort", "name,DESC"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("page").exists())
      .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.profile").exists())
      .andDo(document("query-events"));
  }

  @Test
  @TestDescription("없는 이벤트 하나 조회하기")
  public void getEvent404() throws Exception {
    mockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @TestDescription("이벤트 하나 조회하기")
  public void getEvent() throws Exception {
    // given
    Event event = generateEvent(100);

    // when & then
    mockMvc.perform(get("/api/events/{id}", event.getId())
        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("name").exists())
      .andExpect(jsonPath("id").exists())
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.profile").exists());
  }

  @Test
  @TestDescription("이벤트 수정하기")
  public void updateEvent() throws Exception {
    // given
    Event event = generateEvent(100);

    EventDto eventDto = modelMapper.map(event, EventDto.class);
    eventDto.setName("Update Event");

    // when & then
    final String bearerToken = getBearerToken();
    mockMvc.perform(get("/api/events/{id}", event.getId())
        .header(HttpHeaders.AUTHORIZATION, bearerToken))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("name").exists())
      .andExpect(jsonPath("id").exists())
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.profile").exists());

    mockMvc.perform(put("/api/events/{id}", event.getId())
        .header(HttpHeaders.AUTHORIZATION, bearerToken)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(eventDto)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("name").exists())
      .andExpect(jsonPath("id").exists())
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.profile").exists())
      .andDo(document("update-event"))
    ;
  }

  private Event generateEvent(int index) {
    Event event = Event.builder()
        .name("event " + index)
        .description("test event")
        .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
        .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
        .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
        .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .free(false)
        .offline(true)
        .eventStatus(EventStatus.DRAFT)
        .build();

    return eventRepository.save(event);
  }
}
