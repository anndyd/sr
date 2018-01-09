package com.sap.it.sr.dao;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

public class SrSessionCustomizer implements SessionCustomizer {

	@Override
	public void customize(Session session) throws Exception {
		DatabaseLogin login = (DatabaseLogin)session.getDatasourceLogin();
        login.setConnectionHealthValidatedOnError(false);
	}

}
