package io.spring.restapi.common;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {

  @NotEmpty
  private String adminEmail;

  @NotEmpty
  private String adminPassword;

  @NotEmpty
  private String userEmail;

  @NotEmpty
  private String userPassword;

  @NotEmpty
  private String clientId;

  @NotEmpty
  private String clientSecret;
}
