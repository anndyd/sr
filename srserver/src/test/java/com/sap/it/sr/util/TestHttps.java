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
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestHttps {
	private String HOST = "sapitctfdev.wdf.global.corp.sap";
	
	class CardMeta {
	
		public CardMeta() {
		}
		private String odatametadata;
		private List<CardInfo> value;
		public String getOdatametadata() {
			return odatametadata;
		}
		public void setOdatametadata(String odatametadata) {
			this.odatametadata = odatametadata;
		}
		public List<CardInfo> getValue() {
			return value;
		}
		public void setValue(List<CardInfo> value) {
			this.value = value;
		}
		
	}
	class CardInfo {
		
		public CardInfo() {
		}
		private String id;
		private String userLogon;
		private String fullName;
		private String EMail;
		private String CardNo;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getUserLogon() {
			return userLogon;
		}
		public void setUserLogon(String userLogon) {
			this.userLogon = userLogon;
		}
		public String getFullName() {
			return fullName;
		}
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		public String getEMail() {
			return EMail;
		}
		public void setEMail(String eMail) {
			EMail = eMail;
		}
		public String getCardNo() {
			return CardNo;
		}
		public void setCardNo(String cardNo) {
			CardNo = cardNo;
		}
		
	}
	
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
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

	}
	
    private SSLContext getSSLContext() throws KeyStoreException, 
    NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream instream = new FileInputStream(new File("C:\\Work\\IT\\cert\\.keystore"));
        try {
            trustStore.load(instream, "changeit".toCharArray());
        } finally {
            instream.close();
        }
        return SSLContexts.custom()
                .loadTrustMaterial(trustStore)
                .build();
    }
}
