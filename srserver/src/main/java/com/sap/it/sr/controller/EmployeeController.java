package com.sap.it.sr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.dao.EmployeeDao;
import com.sap.it.sr.entity.Employee;

@Controller
@RequestMapping("employee")
@Scope("request")
public class EmployeeController {

    @Autowired
    private EmployeeDao dao;

	@RequestMapping(value="/all", method = RequestMethod.GET)
	@ResponseBody
	public List<Employee> getEmployees(){
		return dao.findAll();
	}

	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	public Employee getEmployee(@RequestParam(required = true) String badgeId, @RequestParam(required = true) String empId){
		if (!badgeId.equals("")) {
			return dao.findByBadgeId(badgeId);
		} else {
			return dao.findByEmpId(empId);
		}
	}
	
	@RequestMapping(value="/upsert", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void upsertEmployee(@RequestBody Employee emp){
		Employee emp1 = dao.findByEmpId(emp.getEmpId());
		if (emp.getEmpId() != null) {
			if (emp1.getId() != null) {
				emp.setId(emp1.getId());
			}
			emp.setEmpId(emp.getEmpId().toUpperCase());
			dao.merge(emp);
		}
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deleteEmployee(@RequestBody Long id){
		dao.remove(id);
	}

}
