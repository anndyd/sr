package com.sap.it.sr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestHttps {
	private final String HOST = "sapitctfdev.wdf.global.corp.sap";
    private final String CLIENT_KEYSTORE = "C:/Users/i063098/.keystore";
    private final String storePassword = "changeit";
	
	@Test
	public void accessCardService() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, URISyntaxException{
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
        		getSSLContext(),
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        
        NTCredentials creds = new NTCredentials("UsrCI_OD_RPO01","IF_POpo_01", HOST, "global");
        		
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, creds);
		CloseableHttpClient httpclient = HttpClients.custom()
				
        		.setDefaultCredentialsProvider(credentialsProvider)
                .setSSLSocketFactory(sslsf)
                .build();
        try {

        	URIBuilder uri = new URIBuilder();
            uri.setScheme("https")
            .setHost(HOST)
            .setPath("/CardId/odata/CardIdUsers")
            .setParameter("$filter", "UserLogon eq 'I063098'");
            
            HttpGet httpget = new HttpGet(uri.build());

            System.out.println("Executing request " + httpget.getRequestLine());
            

            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);
                System.out.println("----------------------------------------");
                System.out.println(res);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(res);
                JsonNode idNode = rootNode.path("value").get(0).path("CardNo");
                System.out.println(idNode.asText());
                System.out.println(toDec(idNode.asText()));
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

	}
    private long toDec(String hex) {
        List<String> hexStr = Arrays.asList(hex.split("(?<=\\G.{2})"));
        Collections.reverse(hexStr);
        String dec = String.join("", hexStr);
        return Long.parseLong(dec, 16);
    }

    private SSLContext getSSLContext() throws KeyStoreException, 
    NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream instream = new FileInputStream(new File(CLIENT_KEYSTORE));
        try {
            trustStore.load(instream, storePassword.toCharArray());
        } finally {
            instream.close();
        }
        return new SSLContextBuilder().loadTrustMaterial(
                        null,
                        (X509Certificate[] arg0, String arg1) -> {
                            return true;
                        }) // trust all
                        .build();

    }
}
