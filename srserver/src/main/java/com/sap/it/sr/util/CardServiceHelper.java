package com.sap.it.sr.util;

import java.io.IOException;
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
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardServiceHelper {
    private static final Logger LOGGER = Logger.getLogger(CardServiceHelper.class);
	private final String HOST = "sapitctfdev.wdf.global.corp.sap";
	private final String SERVICE_PATH = "/CardId/odata/CardIdUsers";
    private final String TRUSTSTORE = "trust.keystore";
    private final String STORE_PASSWORD = "changeit";
    private final String NTLM_USER = "UsrCI_OD_RPO01";
    private final String NTLM_PASSWORD = "IF_POpo_01_FRES";
    private final String NTLM_DOMAIN = "global";
	
	public JsonNode getEmpInfoByCardNo(String cardNo) {
		JsonNode rlt = null;
		try {
			String hexCardNo = toHexThenReverse(cardNo);
			// Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					getSSLContext(),
			        new String[] { "TLSv1" },
			        null,
			        SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			NTCredentials creds = new NTCredentials(NTLM_USER, NTLM_PASSWORD, HOST, NTLM_DOMAIN);
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
			    .setPath(SERVICE_PATH)
			    .setParameter("$filter", "CardNo eq '" + hexCardNo + "'");
			    HttpGet httpget = new HttpGet(uri.build());
			    CloseableHttpResponse response = httpclient.execute(httpget);
			    try {
			        HttpEntity entity = response.getEntity();
			        String res = EntityUtils.toString(entity);
			        ObjectMapper objectMapper = new ObjectMapper();
			        try {
                        rlt = objectMapper.readTree(res).path("value").get(0);
                    } catch (Exception e) {
                        LOGGER.info("----- read card no from card service failed. -----");
                    }
			    } finally {
			        response.close();
			    }
			} finally {
			    httpclient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return rlt;
	}
    
    private String toHexThenReverse(String dec) {
    	String hex = Long.toHexString(Long.parseLong(dec));
        if (hex.length()<8) {
            hex = "0" + hex;
        }
        List<String> hexStr = Arrays.asList(hex.split("(?<=\\G.{2})"));
        Collections.reverse(hexStr);
        return String.join("", hexStr);
    }

	private SSLContext getSSLContext() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, KeyManagementException {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(getClass().getResourceAsStream(TRUSTSTORE), STORE_PASSWORD.toCharArray());
		return new SSLContextBuilder().loadTrustMaterial(null, (X509Certificate[] arg0, String arg1) -> {
			return true;
		}).build();
	}

}
