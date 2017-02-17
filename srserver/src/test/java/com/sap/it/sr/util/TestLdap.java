package com.sap.it.sr.util;

import org.junit.Test;

public class TestLdap {
	@Test
	public void printInfo() {
		System.out.println(
		LdapHelper.getEmployeeFullname("I062672", "I063098", ""));
	}
}
