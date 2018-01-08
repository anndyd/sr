package com.sap.it.sr.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sap.it.sr.entity.Employee;
import com.sap.it.sr.entity.User;
import com.sap.it.sr.util.CardServiceHelper;
import com.sap.it.sr.util.EmpInfo;
import com.sap.it.sr.util.EncryptHelper;
import com.sap.it.sr.util.LdapHelper;

@Repository
public class EmployeeDao extends BaseDao<Employee> {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDao.class);
    
    public Employee findByBadgeId(String badgeId) {
        LOGGER.info("---- EmployeeDao.findByBadgeId start ----");
    	Employee emp = new Employee();
        List<Employee> list = em.createQuery("select t from Employee t where t.badgeId=?1", Employee.class)
                .setParameter(1, badgeId).setMaxResults(1).getResultList();
        if (list.isEmpty()) {
            LOGGER.info("---- EmployeeDao.findByBadgeId -> findFromCardService ----");
        	emp = findFromCardService(badgeId);
        } else {
        	emp = list.get(0);
        }
        LOGGER.info("---- EmployeeDao.findByBadgeId end ----");
        return emp;
    }

    public Employee findByEmpId(String empId) {
    	Employee emp = new Employee();
    	if (empId != null) {
    	    empId = empId.toUpperCase();
    	}
        List<Employee> list = em.createQuery("select t from Employee t where t.empId=?1", Employee.class)
                .setParameter(1, empId).setMaxResults(1).getResultList();
        emp = list.isEmpty() ? emp : list.get(0);
        return emp;
    }
    
    public EmpInfo getEmpInfo(String id) {
    	EmpInfo rlt = new EmpInfo();
        List<User> userList = em.createQuery("select t from User t where length(t.password)>0 and t.role=?1", User.class)
                .setParameter(1, "1").setMaxResults(1).getResultList();
        if (userList.size() > 0){
        	String pwd = userList.get(0).getPassword();
        	rlt = LdapHelper.getEmployee(id, userList.get(0).getUserName(), EncryptHelper.decrypt(pwd));
        }
        
    	return rlt;
    }
    
    private Employee findFromCardService(String badgeId) {
    	Employee emp = new Employee();
    	CardServiceHelper csp = new CardServiceHelper();
    	JsonNode empNode = csp.getEmpInfoByCardNo(badgeId);
    	if (null != empNode) {
    	    String empId = empNode.path("UserLogon").asText();
    	    emp = findByEmpId(empId);
    		emp.setBadgeId(badgeId);
    		emp.setEmpId(empId);
    		emp.setEmpName(empNode.path("FullName").asText());
    		// save employee information
    		merge(emp);
    	}
    	return emp;
    }
}
