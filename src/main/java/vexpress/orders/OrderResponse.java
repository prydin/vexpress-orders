package vexpress.orders;

public class OrderResponse {
  private String status;

  private String trackingNumber;

  public OrderResponse(final String status, final String trackingNumber) {
    this.status = status;
    this.trackingNumber = trackingNumber;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public String getTrackingNumber() {
    return trackingNumber;
  }

  public void setTrackingNumber(final String trackingNumber) {
    this.trackingNumber = trackingNumber;
  }
}
