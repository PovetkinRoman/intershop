package ru.rpovetkin.intershop.payment.api;

import ru.rpovetkin.intershop.payment.ApiClient;

import ru.rpovetkin.intershop.payment.model.PaymentPostRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-08-13T14:58:26.908079+07:00[Asia/Novosibirsk]", comments = "Generator version: 7.4.0")
public class DefaultApi {
    private ApiClient apiClient;

    public DefaultApi() {
        this(new ApiClient());
    }

    @Autowired
    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Проверить, хватает ли средств для оплаты
     * 
     * <p><b>200</b> - Достаточно ли средств
     * @param amountForPay The amountForPay parameter
     * @return Boolean
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec paymentGetRequestCreation(Double amountForPay) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'amountForPay' is set
        if (amountForPay == null) {
            throw new WebClientResponseException("Missing the required parameter 'amountForPay' when calling paymentGet", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "amountForPay", amountForPay));
        
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Boolean> localVarReturnType = new ParameterizedTypeReference<Boolean>() {};
        return apiClient.invokeAPI("/payment", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Проверить, хватает ли средств для оплаты
     * 
     * <p><b>200</b> - Достаточно ли средств
     * @param amountForPay The amountForPay parameter
     * @return Boolean
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Boolean> paymentGet(Double amountForPay) throws WebClientResponseException {
        ParameterizedTypeReference<Boolean> localVarReturnType = new ParameterizedTypeReference<Boolean>() {};
        return paymentGetRequestCreation(amountForPay).bodyToMono(localVarReturnType);
    }

    /**
     * Проверить, хватает ли средств для оплаты
     * 
     * <p><b>200</b> - Достаточно ли средств
     * @param amountForPay The amountForPay parameter
     * @return ResponseEntity&lt;Boolean&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Boolean>> paymentGetWithHttpInfo(Double amountForPay) throws WebClientResponseException {
        ParameterizedTypeReference<Boolean> localVarReturnType = new ParameterizedTypeReference<Boolean>() {};
        return paymentGetRequestCreation(amountForPay).toEntity(localVarReturnType);
    }

    /**
     * Проверить, хватает ли средств для оплаты
     * 
     * <p><b>200</b> - Достаточно ли средств
     * @param amountForPay The amountForPay parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec paymentGetWithResponseSpec(Double amountForPay) throws WebClientResponseException {
        return paymentGetRequestCreation(amountForPay);
    }
    /**
     * Провести оплату
     * 
     * <p><b>200</b> - Успешно оплачено или нет
     * @param paymentPostRequest The paymentPostRequest parameter
     * @return Boolean
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec paymentPostRequestCreation(PaymentPostRequest paymentPostRequest) throws WebClientResponseException {
        Object postBody = paymentPostRequest;
        // verify the required parameter 'paymentPostRequest' is set
        if (paymentPostRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'paymentPostRequest' when calling paymentPost", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Boolean> localVarReturnType = new ParameterizedTypeReference<Boolean>() {};
        return apiClient.invokeAPI("/payment", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Провести оплату
     * 
     * <p><b>200</b> - Успешно оплачено или нет
     * @param paymentPostRequest The paymentPostRequest parameter
     * @return Boolean
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Boolean> paymentPost(PaymentPostRequest paymentPostRequest) throws WebClientResponseException {
        ParameterizedTypeReference<Boolean> localVarReturnType = new ParameterizedTypeReference<Boolean>() {};
        return paymentPostRequestCreation(paymentPostRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Провести оплату
     * 
     * <p><b>200</b> - Успешно оплачено или нет
     * @param paymentPostRequest The paymentPostRequest parameter
     * @return ResponseEntity&lt;Boolean&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Boolean>> paymentPostWithHttpInfo(PaymentPostRequest paymentPostRequest) throws WebClientResponseException {
        ParameterizedTypeReference<Boolean> localVarReturnType = new ParameterizedTypeReference<Boolean>() {};
        return paymentPostRequestCreation(paymentPostRequest).toEntity(localVarReturnType);
    }

    /**
     * Провести оплату
     * 
     * <p><b>200</b> - Успешно оплачено или нет
     * @param paymentPostRequest The paymentPostRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec paymentPostWithResponseSpec(PaymentPostRequest paymentPostRequest) throws WebClientResponseException {
        return paymentPostRequestCreation(paymentPostRequest);
    }
}
