package com.sap.it.sr.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sap.it.sr.dao.UserDao;
import com.sap.it.sr.entity.User;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    @Autowired
    private UserDao userDAO;

    public void openSession() {
        User user = userDAO.findAll("id").get(0);
//        SessionHolder.setContext(user.getId(), getTenantLocale());
    }

}
