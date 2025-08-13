package ru.rpovetkin.paymentservice.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentPostRequest
 */

@JsonTypeName("_payment_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-13T14:58:27.854220+07:00[Asia/Novosibirsk]", comments = "Generator version: 7.4.0")
public class PaymentPostRequest {

  private Double amountForPay;

  public PaymentPostRequest amountForPay(Double amountForPay) {
    this.amountForPay = amountForPay;
    return this;
  }

  /**
   * Get amountForPay
   * @return amountForPay
  */
  
  @Schema(name = "amountForPay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountForPay")
  public Double getAmountForPay() {
    return amountForPay;
  }

  public void setAmountForPay(Double amountForPay) {
    this.amountForPay = amountForPay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentPostRequest paymentPostRequest = (PaymentPostRequest) o;
    return Objects.equals(this.amountForPay, paymentPostRequest.amountForPay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountForPay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentPostRequest {\n");
    sb.append("    amountForPay: ").append(toIndentedString(amountForPay)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

