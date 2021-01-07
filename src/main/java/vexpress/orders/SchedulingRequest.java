package vexpress.orders;

public class SchedulingRequest {
  private int fromZip;

  private int toZip;

  private double weight;

  private String trackingNumber;

  private long timeSubmitted;

  public SchedulingRequest() {}

  public SchedulingRequest(
      final int fromZip,
      final int toZip,
      final double weight,
      final String trackingNumber,
      final long timeSubmitted) {
    this.fromZip = fromZip;
    this.toZip = toZip;
    this.weight = weight;
    this.trackingNumber = trackingNumber;
    this.timeSubmitted = timeSubmitted;
  }

  public int getFromZip() {
    return fromZip;
  }

  public void setFromZip(final int fromZip) {
    this.fromZip = fromZip;
  }

  public int getToZip() {
    return toZip;
  }

  public void setToZip(final int toZip) {
    this.toZip = toZip;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(final double weight) {
    this.weight = weight;
  }

  public String getTrackingNumber() {
    return trackingNumber;
  }

  public void setTrackingNumber(final String trackingNumber) {
    this.trackingNumber = trackingNumber;
  }

  public long getTimeSubmitted() {
    return timeSubmitted;
  }

  public void setTimeSubmitted(final long timeSubmitted) {
    this.timeSubmitted = timeSubmitted;
  }
}
