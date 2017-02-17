package com.sap.it.sr.util;

import org.junit.Test;

public class TestEnp {
	@Test
	public void encrypt() {
		final String secretKey = "i8VDeGLpwt2UhfUhekoNyxN4pARDmCnnPin8x7778Ig=";
	     
	    String originalString = "mynameisAnndy_123$2&";
	    String encryptedString = EncryptHelper.encrypt(originalString, secretKey) ;
	    String decryptedString = EncryptHelper.decrypt(encryptedString, secretKey) ;
	     
	    System.out.println(originalString);
	    System.out.println(encryptedString);
	    System.out.println(decryptedString);
	}
}
