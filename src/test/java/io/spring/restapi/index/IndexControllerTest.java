package io.spring.restapi.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.spring.restapi.common.BaseControllerTest;
import org.junit.jupiter.api.Test;

public class IndexControllerTest extends BaseControllerTest {

  @Test
  public void index() throws Exception {
    mockMvc
        .perform(get("/api/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("_links.events").exists());
  }
}
