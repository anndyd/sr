/**
 * 
 */
package com.sap.it.sr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import junit.framework.Assert;

/**
 * @author I063098
 *
 */
public class TestRestTemplate {
    private final String urlOverHttps = "https://sapitctfdev.wdf.global.corp.sap/CardId/odata/CardIdUsers?$filter=UserLogon eq 'i063098'";
    private final String CLIENT_KEYSTORE = "C:/Users/i063098/.keystore";
    private final String storePassword = "changeit";
    
    @Test
    public void givenAcceptingAllCertificatesUsing4_4_whenUsingRestTemplate_thenCorrect()
            throws ClientProtocolException, IOException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, URISyntaxException {
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
//                .build();
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setHttpClient(httpClient);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("sapitctfdev.wdf.global.corp.sap").setPath("/CardId/odata/CardIdUsers")
            .setParameter("filter", "UserLogon eq 'i063098'");
        URI uri = builder.build();
        
        SSLContext sslContext =
                new SSLContextBuilder().loadTrustMaterial(
                        null,
                        (X509Certificate[] arg0, String arg1) -> {
                            return true;
                        }) // trust all
                        .build();
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
         = new UsernamePasswordCredentials("UsrCI_OD_RPO01", "IF_POpo_01");
        provider.setCredentials(AuthScope.ANY, credentials);
          
        HttpClient client = HttpClients.custom().setSslcontext(sslContext)
          .setDefaultCredentialsProvider(provider)
          .build();
        HttpResponse response = client.execute(
                new HttpGet(uri));
//        httpclient = HttpClients.custom().setSSLContext(trustedSSLContext).build();
//
//        
//        // create strategy that accepts all certificates
//        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
//            @Override
//            public boolean isTrusted(X509Certificate[] certificate,
//                                     String type) {
//                return true;
//            }
//        };
//
//        
//        SSLContext sslContext = SSLContexts.custom()
//                .loadKeyMaterial(getStore(CLIENT_KEYSTORE, storePassword.toCharArray()), storePassword.toCharArray())
//                .loadTrustMaterial(getStore(CLIENT_KEYSTORE, storePassword.toCharArray()),
//                        acceptingTrustStrategy)
//                //.useProtocol("TLS")
//                .build();

//        RestTemplate restTemplate = getRestTemplateForHTTPS(sslContext);
//        
//        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("UsrCI_OD_RPO01", "IF_POpo_01"));
//        ResponseEntity<String> response = restTemplate.exchange(urlOverHttps, HttpMethod.GET, 
//                null, 
//                String.class, "filter", "UserLogon eq 'I063098'");
        Assert.assertEquals(response.getEntity().getContentLength(), 200);
    }
    
    private RestTemplate getRestTemplateForHTTPS(SSLContext sslContext) {
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        CloseableHttpClient httpClient =
                HttpClientBuilder.create().setSSLSocketFactory(connectionFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

    protected KeyStore getStore(final String storeFileName, final char[] password)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        final KeyStore store = KeyStore.getInstance("jks");
//        URL url = getClass().getClassLoader().getResource(storeFileName);
        InputStream inputStream = new FileInputStream(new File(storeFileName));
        try {
            store.load(inputStream, password);
        } finally {
            inputStream.close();
        }

        return store;
    }
    
    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
              String auth = username + ":" + password;
              byte[] encodedAuth = Base64.encodeBase64( 
                 auth.getBytes(Charset.forName("US-ASCII")) );
              String authHeader = "Basic " + new String( encodedAuth );
              set( "Authorization", authHeader );
           }};
     }
}
