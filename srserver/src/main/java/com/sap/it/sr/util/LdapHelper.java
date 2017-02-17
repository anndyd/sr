package com.sap.it.sr.util;

import javax.naming.NamingException;

public class LdapHelper {
	public static String getEmployeeFullname(String id, String usr, String pwd) {
		String rlt = "";
		LDAPService ls = new LDAPService(usr, pwd);
		try {
			EmpInfo emp = ls.getIdentity(id);
			rlt = emp.getName();
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			try {
				ls.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return rlt;
	}
}
