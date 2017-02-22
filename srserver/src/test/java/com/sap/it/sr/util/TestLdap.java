package com.sap.it.sr.util;

import org.junit.Test;

public class TestLdap {
	@Test
	public void printInfo() {
		System.out.println(
		LdapHelper.getEmployee("I063098", "I063098", "asdfJkl1"));
	}
}
