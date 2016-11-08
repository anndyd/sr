package com.sap.it.sr.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.User;

@Repository
public class UserDao extends BaseDao<User> {
    public User findByName(String userName) {
    	User usr = new User();
        List<User> userList = em.createQuery("select t from User t where t.userName=?1", User.class)
                .setParameter(1, userName).setMaxResults(1).getResultList();
        return userList.isEmpty() ? usr : userList.get(0);
    }
}
