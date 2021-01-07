package vexpress.orders;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pricing")
public class Config {
  private String zipcodeUrl;

  public String getZipcodeUrl() {
    return zipcodeUrl;
  }

  public void setZipcodeUrl(final String zipcodeUrl) {
    this.zipcodeUrl = zipcodeUrl;
  }
}
