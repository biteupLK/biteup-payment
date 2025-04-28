package com.biteup.biteup_payment.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payments")
@Data
public class PaymentDetails {

  @Id
  private String id;

  private String eventId;
  private String type;
  private DataWrapper data;

  @Data
  public static class DataWrapper {

    private ObjectWrapper object;
  }

  @Data
  public static class ObjectWrapper {

    private Members members;
  }

  @Data
  public static class Members {

    private ObjectWrapperValue id;
    private ValueWrapper object;
    private ValueWrapper billing_address_collection;
    private ValueWrapper cancel_url;
    private ValueWrapper currency;
    private ValueWrapper receipt_url;
    private ValueWrapper customer;
    private ValueWrapper payment_status;
    private ValueWrapper success_url;
    private ValueWrapper mode;
    private NestedValueWrapper amount_subtotal;
    private NestedValueWrapper amount;
    private ValueWrapper payment_intent;
    private Object currencyObject;
    private Metadata metadata;
    private CustomerDetails billing_details;
    private TotalDetails total_details;
  }

  @Data
  public static class ObjectWrapperValue {

    private ValueWrapper value;
  }

  @Data
  public static class ValueWrapper {

    private String value;
  }

  @Data
  public static class NestedValueWrapper {

    private ValueWrapper value;
  }

  @Data
  public static class Metadata {

    private MetadataMembers members;
  }

  @Data
  public static class MetadataMembers {

    private ValueWrapper foodName;
    private ValueWrapper receipt_email;
    private ValueWrapper restaurantEmail;
    private ValueWrapper phone;
  }

  @Data
  public static class CustomerDetails {

    private CustomerMembers members;
  }

  @Data
  public static class CustomerMembers {

    private CustomerAddress address;
    private ValueWrapper email;
    private ValueWrapper name;
    private ValueWrapper tax_exempt;
  }

  @Data
  public static class CustomerAddress {

    private AddressMembers members;
  }

  @Data
  public static class AddressMembers {

    private ValueWrapper city;
    private ValueWrapper country;
    private ValueWrapper line1;
    private ValueWrapper line2;
    private ValueWrapper postal_code;
  }

  @Data
  public static class TotalDetails {

    private Object amount_discount;
    private Object amount_shipping;
    private Object amount_tax;
  }
}
