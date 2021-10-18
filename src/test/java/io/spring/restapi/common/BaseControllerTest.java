package io.spring.restapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
//@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
@ActiveProfiles("test")
@Disabled
public class BaseControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected ModelMapper modelMapper;
}
