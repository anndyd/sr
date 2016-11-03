package com.sap.it.sr.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.Employee;

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
        List<Employee> list = em.createQuery("select t from Employee t where t.empId=?1", Employee.class)
                .setParameter(1, empId).setMaxResults(1).getResultList();
        return list.isEmpty() ? emp : list.get(0);
    }
}
