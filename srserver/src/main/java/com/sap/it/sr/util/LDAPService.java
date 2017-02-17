package com.sap.it.sr.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class LDAPService {
	private String ldapServer = "ldap://global.corp.sap:389";
	private String ldapSearchPhrase = "OU=Identities,DC=global,DC=corp,DC=sap";

	private LdapContext ldapContext;

	public LDAPService(String usr, String pwd) {
		initContext(usr, pwd);
	}

	private void initContext(String usr, String pwd) {
		try {
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, usr + "@global.corp.sap");
			env.put(Context.SECURITY_CREDENTIALS, pwd);
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, this.ldapServer);

			env.put("java.naming.ldap.attributes.binary", "objectSID");
			this.ldapContext = new InitialLdapContext(env, null);
		} catch (Exception e) {
			throw new RuntimeException("Initialize LDAP Context Error " + e);
		}

	}

	public List<String> getDirectReports(String sAMAccount) throws NamingException {

		ArrayList<String> subordinates = new ArrayList<String>();
		SearchResult result = this.searchAccount(sAMAccount);
		if (result != null) {
			Attributes attributes = result.getAttributes();
			if (attributes.get("directReports") != null) {
				NamingEnumeration all = attributes.get("directReports").getAll();
				while (all.hasMoreElements()) {
					LdapName dn = new LdapName(all.nextElement().toString());
					for (Rdn rdn : dn.getRdns()) {
						if (rdn.getType().equals("CN")) {
							subordinates.add(rdn.getValue().toString());
						}
					}
				}
			}
		}
		return subordinates;

	}

	public EmpInfo getIdentity(String sAMAccount) throws NamingException {
		SearchResult result = this.searchAccount(sAMAccount);

		if (result != null) {
			EmpInfo employee = new EmpInfo();

			Attributes attributes = result.getAttributes();

			if (attributes.get("sAMAccountName") != null) {
				employee.setId(attributes.get("sAMAccountName").get().toString());
			}

			if (attributes.get("mail") != null) {
				employee.setMail(attributes.get("mail").get().toString());
			}

			if (attributes.get("displayNamePrintable") != null) {
				employee.setName(attributes.get("displayNamePrintable").get().toString());
			}

			if (attributes.get("co") != null) {
				employee.setCountry(attributes.get("co").get().toString());
			}

			if (attributes.get("l") != null) {
				employee.setLocation(attributes.get("l").get().toString());
			}

			if (attributes.get("department") != null) {
				employee.setDepartment(attributes.get("department").get().toString());
			}

			if (attributes.get("manager") != null) {
				LdapName dn = new LdapName(attributes.get("manager").get().toString());
				List<Rdn> rdns = dn.getRdns();
				for (Rdn rdn : rdns) {
					if (rdn.getType().equals("CN")) {
						employee.setManager(rdn.getValue().toString());
					}
				}
			}
			return employee;
		}

		return null;

	}

	private SearchResult searchAccount(String sAMAccountName) throws NamingException {

		String searchFilter = "(&(objectClass=user)(sAMAccountName=" + sAMAccountName + "))";
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		NamingEnumeration<SearchResult> results = ldapContext.search(this.ldapSearchPhrase, searchFilter,
				searchControls);

		if (results.hasMoreElements()) {
			SearchResult searchResult = results.nextElement();
			if (results.hasMoreElements()) {
				// log.error("Multiple matched sAMAccountName while searching: "
				// + sAMAccountName);
			}
			return searchResult;
		} else {
			return null;
		}

	}

	public void close() throws NamingException {

		this.ldapContext.close();
	}
}
