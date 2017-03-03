package com.sap.it.sr.util;

import org.junit.Test;

public class TestLdap {
	@Test
	public void printInfo() {
	    EmpInfo ei = LdapHelper.getEmployee("I012193", "I063098", "asdfJkl1");
	    ei.printInfo();
	}
}
