package com.sap.it.sr.util;

import javax.naming.NamingException;

public class LdapHelper {
	public static EmpInfo getEmployee(String id, String usr, String pwd) {
		EmpInfo rlt = new EmpInfo();
		LDAPService ls = new LDAPService(usr, pwd);
		try {
			EmpInfo emp = ls.getIdentity(id);
			rlt = emp;
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
