package vexpress.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(Config.class)
public class OrdersApplication {
  public static void main(final String[] args) {
    SpringApplication.run(OrdersApplication.class, args);
  }
}
