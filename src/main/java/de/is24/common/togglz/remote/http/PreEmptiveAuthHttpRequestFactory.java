package de.is24.common.togglz.remote.http;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import java.net.URI;


public class PreEmptiveAuthHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
  public PreEmptiveAuthHttpRequestFactory(HttpClient httpClient) {
    super(httpClient);
  }

  @Override
  protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
    AuthCache authCache = new BasicAuthCache();
    BasicScheme basicAuth = new BasicScheme();
    HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort());
    authCache.put(targetHost, basicAuth);

    BasicHttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);

    return localContext;
  }
}
