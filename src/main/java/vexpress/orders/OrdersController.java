package vexpress.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersController {
  @Autowired private Config config;
  @Autowired RabbitTemplate rabbitTemplate;

  @GetMapping(path = "/healthcheck", produces = "text/plain")
  public String healthCheck() {
    return "OK";
  }

  @PostMapping(path = "/order", consumes = "application/json", produces = "application/json")
  public OrderResponse placeOrder(@RequestBody final SchedulingRequest order)
      throws JsonProcessingException {
    order.setTrackingNumber(UUID.randomUUID().toString());
    order.setTimeSubmitted(System.currentTimeMillis());
    final ObjectMapper om = new ObjectMapper();
    rabbitTemplate.convertAndSend("vexpress", "scheduling.request", om.writeValueAsString(order));
    return new OrderResponse("OK", order.getTrackingNumber());
  }
}
