package com.sap.it.sr.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.Employee;
import com.sap.it.sr.entity.User;
import com.sap.it.sr.util.EmpInfo;
import com.sap.it.sr.util.EncryptHelper;
import com.sap.it.sr.util.LdapHelper;

@Repository
public class EmployeeDao extends BaseDao<Employee> {
    public Employee findByBadgeId(String badgeId) {
    	Employee emp = new Employee();
        List<Employee> list = em.createQuery("select t from Employee t where t.badgeId=?1", Employee.class)
                .setParameter(1, badgeId).setMaxResults(1).getResultList();
        return list.isEmpty() ? emp : list.get(0);
    }

    public Employee findByEmpId(String empId) {
    	Employee emp = new Employee();
    	if (empId != null) {
    	    empId = empId.toUpperCase();
    	}
        List<Employee> list = em.createQuery("select t from Employee t where t.empId=?1", Employee.class)
                .setParameter(1, empId).setMaxResults(1).getResultList();
        emp = list.isEmpty() ? emp : list.get(0);
        if (list.isEmpty() || (!list.isEmpty() && (null == emp.getEmpName() || null == emp.getCostCenter()))) {
        	EmpInfo ei = getEmpInfo(empId);
        	if (null != ei) {
	        	emp.setEmpName(ei.getName());
	        	emp.setCostCenter(ei.getCostCenter());
        	}
        }
        
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
}
