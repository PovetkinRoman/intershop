# DefaultApi

All URIs are relative to *http://payment-service-app:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**paymentGet**](DefaultApi.md#paymentGet) | **GET** /payment | Проверить, хватает ли средств для оплаты |
| [**paymentPost**](DefaultApi.md#paymentPost) | **POST** /payment | Провести оплату |



## paymentGet

> Boolean paymentGet(amountForPay)

Проверить, хватает ли средств для оплаты

### Example

```java
// Import classes:
import ru.rpovetkin.intershop.payment.ApiClient;
import ru.rpovetkin.intershop.payment.ApiException;
import ru.rpovetkin.intershop.payment.Configuration;
import ru.rpovetkin.intershop.payment.models.*;
import ru.rpovetkin.intershop.payment.api.DefaultApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://payment-service-app:8080");

        DefaultApi apiInstance = new DefaultApi(defaultClient);
        Double amountForPay = 3.4D; // Double | 
        try {
            Boolean result = apiInstance.paymentGet(amountForPay);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#paymentGet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amountForPay** | **Double**|  | |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Достаточно ли средств |  -  |


## paymentPost

> Boolean paymentPost(paymentPostRequest)

Провести оплату

### Example

```java
// Import classes:
import ru.rpovetkin.intershop.payment.ApiClient;
import ru.rpovetkin.intershop.payment.ApiException;
import ru.rpovetkin.intershop.payment.Configuration;
import ru.rpovetkin.intershop.payment.models.*;
import ru.rpovetkin.intershop.payment.api.DefaultApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://payment-service-app:8080");

        DefaultApi apiInstance = new DefaultApi(defaultClient);
        PaymentPostRequest paymentPostRequest = new PaymentPostRequest(); // PaymentPostRequest | 
        try {
            Boolean result = apiInstance.paymentPost(paymentPostRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#paymentPost");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **paymentPostRequest** | [**PaymentPostRequest**](PaymentPostRequest.md)|  | |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Успешно оплачено или нет |  -  |

