package net.metrosystems.inventoryaggregator.oauth.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class OAuth2Interceptor implements ClientHttpRequestInterceptor{
 private String token;
 public OAuth2Interceptor(String token) {
  this.token = token;
 }
 
	   @Override
	    public ClientHttpResponse intercept( HttpRequest request, byte[] body, ClientHttpRequestExecution execution ) throws IOException {
		  
		   final String authHeader = "Bearer " + token;
	        //Add the auth-header
	        request.getHeaders().add( "Authorization", authHeader );
		   return execution.execute( request, body );
	   }
	   
	
}
