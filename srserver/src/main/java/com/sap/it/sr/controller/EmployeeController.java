package com.sap.it.sr.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import com.sap.it.sr.util.EmpInfo;
import com.sap.it.sr.util.SessionHolder;

@Controller
@RequestMapping("employee")
@Scope("request")
public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class);
    
	@Autowired(required=true)
	private HttpServletRequest request;	

    @Autowired
    private EmployeeDao dao;

	@RequestMapping(value="/all", method = RequestMethod.GET)
	@ResponseBody
	@Transactional(readOnly = true)
	public List<Employee> getEmployees(){
		return dao.findAll();
	}

	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	@Transactional(readOnly = true)
	public Employee getEmployee(@RequestParam(required = true) String badgeId, 
	        @RequestParam(required = true) String empId, 
	        @RequestParam(required = false) boolean... needADInfo){
	    LOGGER.info("---- Employee.get start ----");
		Employee emp;
		if (!badgeId.equals("")) {
			emp = dao.findByBadgeId(badgeId);
		} else {
			emp = dao.findByEmpId(empId);
		}
		if (null != needADInfo && needADInfo.length > 0 && needADInfo[0]) {
		    emp.setEmpId(empId.toUpperCase());
		    LOGGER.info("---- Employee.get -> get AD information ----");
		    emp = getADInfo(emp);
		}
		LOGGER.info("---- Employee.get end ----");
		return emp;
	}
	
	@RequestMapping(value="/upsert", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void upsertEmployee(@RequestBody Employee emp){
	    LOGGER.info("---- Employee.upsert ----");
		HttpSession session = request.getSession();
	    String role = session.getAttribute(SessionHolder.USER_ROLE).toString();

	    if (null != role && (role.equals("1") || role.equals("2"))) {
			Employee emp1 = dao.findByEmpId(emp.getEmpId());
			if (emp.getEmpId() != null) {
				if (emp1.getId() != null) {
					emp.setId(emp1.getId());
				}
				emp.setEmpId(emp.getEmpId().toUpperCase());
				LOGGER.info("---- Employee.upsert -> merge data ----");
				dao.merge(emp);
			}
		}
	    LOGGER.info("---- Employee.upsert end ----");
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deleteEmployee(@RequestBody Long id){
		dao.remove(id);
	}
    
	@Transactional(readOnly = true)
    private Employee getADInfo(Employee emp) {
        Employee rlt = new Employee();
        if (emp.getEmpId() != null) {
            EmpInfo ei = dao.getEmpInfo(emp.getEmpId());
            if (null != ei) {
                emp.setEmpName(ei.getName());
                emp.setCostCenter(ei.getCostCenter());
                rlt = emp;
            } else {
                dao.remove(emp);
            }
        }
        return rlt;
    }
}
