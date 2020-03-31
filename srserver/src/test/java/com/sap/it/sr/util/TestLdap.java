package com.sap.it.sr.util;

import org.junit.Test;
import javax.naming.directory.SearchResult;

public class TestLdap {
	@Test
	public void printInfo() {
	    EmpInfo ei = LdapHelper.getEmployee("I012193", "i068056", "kiss@Hui16");
	    ei.printInfo();
	}

	@Test
	public void searchComputer() {
		try {
			LDAPService ls = new LDAPService("i068056", "kiss@Hui16");
			SearchResult rlt = ls.searchComputer("34318868");
			System.out.println(rlt);
		} catch (Exception e) {

		}

	}
}
