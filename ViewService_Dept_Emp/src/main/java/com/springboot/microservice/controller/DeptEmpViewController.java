package com.springboot.microservice.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.microservice.model.Department;
import com.springboot.microservice.model.Employee;
import com.springboot.microservice.model.ListOfDepartments;
import com.springboot.microservice.model.ListOfEmployees;

@RestController
public class DeptEmpViewController {
	
	@Autowired
	private RestTemplate restTemplate;
    
	@RequestMapping(value = "/DeptList")
	 public ModelAndView  getAllDepartments(HttpServletRequest request,HttpServletResponse response) {
		 System.out.println("In Controller");
		 ListOfDepartments deptlist  =  restTemplate.getForObject("http://gateway-service/Department/GetAll", ListOfDepartments.class);
		 System.out.println(deptlist.getDeptList().get(0));
		 List<Department> lstdept = new ArrayList<>();
		 
		 for(int i = 0; i < deptlist.getDeptList().size(); i++) {
			 lstdept.add(deptlist.getDeptList().get(i));
		 }
		 for (Department department : lstdept) {
			System.out.println(department.getDeptid()+department.getDepthead());
		}
		 HttpSession session = request.getSession();
		 session.setAttribute("DeptList", lstdept);
		 ModelAndView modelAndView = new ModelAndView("home");
		 modelAndView.addObject("DeptList", lstdept);
		 modelAndView.addObject("homepage", "main");
		 return modelAndView;
	 }
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/NewDepartment" ,method =RequestMethod.GET )
	 public ModelAndView newDepartment(HttpServletRequest request) {
		 String Register = "newform";
		 HttpSession session1 = request.getSession();
		 List<Department> lst = (List<Department>) session1.getAttribute("DeptList");
		 session1.setAttribute("DeptList", lst);
		 ModelAndView modelAndView = new ModelAndView();
		 modelAndView.addObject("Register", Register);
		 modelAndView.addObject("createdept", "newdept");
		 modelAndView.setViewName("home");
		 Department department = new Department();
		 modelAndView.addObject("department", department);
		 return modelAndView;
	 }
	 
	 @RequestMapping(value = "/CreateDepartment", method = RequestMethod.POST)
	 public ModelAndView insertDepartment(@ModelAttribute Department department) {
	     restTemplate.postForObject("http://gateway-service/Department/AddDepartment",department,Department.class);
		 return new ModelAndView("redirect:/DeptList");
	 }
	 
	 @RequestMapping(value = "/UpdateDepartment", method = RequestMethod.POST)
	 public ModelAndView updateDepartment(@ModelAttribute Department department, HttpServletRequest request) {
		 int deptid =Integer.parseInt(request.getParameter("deptid"));
	     restTemplate.put("http://gateway-service/Department/updateDepartment/"+deptid,department);
		 return new ModelAndView("redirect:/DeptList");
	 }	 
	 
	 @RequestMapping(value = "/DeleteDepartment",method = RequestMethod.GET)
	 public ModelAndView deleteDepartment(HttpServletRequest request) {
		 int deptid =Integer.parseInt(request.getParameter("deptid"));
		 restTemplate.delete("http://gateway-service/Department/DeleteDepartment/"+deptid);
		 return new ModelAndView("redirect:/DeptList");
	 }
	 
	 @SuppressWarnings("unchecked")
	@RequestMapping(value = "/GetDepartment",method = RequestMethod.GET)
	 public ModelAndView getDepartmentId(HttpServletRequest request) {
		int deptid =  Integer.parseInt(request.getParameter("deptid"));
		HttpSession session2 = request.getSession();
		List<Department> lst = (List<Department>) session2.getAttribute("DeptList");
		ModelAndView modelAndView = new ModelAndView("home");
		modelAndView.addObject("DeptList", lst);
		modelAndView.addObject("departmentid", deptid);
		return  modelAndView;		
	 }
	 
	 @SuppressWarnings("unchecked")
	@RequestMapping(value = "/showdepartments",method = RequestMethod.GET)
	 public ModelAndView showDepartments(HttpServletRequest request) {
		 HttpSession session3 = request.getSession();
		 List<Department> lstdept1 = (List<Department>) session3.getAttribute("DeptList");
		 session3.setAttribute("DeptListemp", lstdept1);
		 ModelAndView modelAndView = new ModelAndView("home");
   	 modelAndView.addObject("DeptListemp", lstdept1);
		 int deptid =  lstdept1.get(0).getDeptid();
		 modelAndView.addObject("name", "names");
		 return new ModelAndView("redirect:/emplist?deptid="+deptid);
	 }
	 
	 @SuppressWarnings("unchecked")
	@RequestMapping(value = "/emplist")
		public ModelAndView getAllEmployees(HttpServletRequest request) {
			int deptid =Integer.parseInt(request.getParameter("deptid"));
			List<Employee> lstemp = new ArrayList<>();
			ListOfEmployees  lst = restTemplate.getForObject("http://gateway-service/Department/"+deptid+"/employees", ListOfEmployees.class);
			for (int i = 0; i < lst.getListOfEmployee().size(); i++) {
				lstemp.add(lst.getListOfEmployee().get(i));
			}
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute("EmpList", lstemp);
			List<Department> lstdept1 = (List<Department>) httpSession.getAttribute("DeptList");
			ModelAndView modelAndView = new ModelAndView("home");
			modelAndView.addObject("DeptListemp", lstdept1);
			modelAndView.addObject("EmpList", lstemp);
			modelAndView.addObject("homepage", "emppage");
			modelAndView.addObject("name", "names");		
			return modelAndView;
			}
		
		@SuppressWarnings("unchecked")
		@RequestMapping(value = "/newEmployee", method = RequestMethod.GET)
		public ModelAndView newContact(HttpServletRequest request) {
			String Register  = "NewForm";
			HttpSession session1 = request.getSession();
			List<Employee> lst =(List<Employee>)session1.getAttribute("EmpList");
			ModelAndView model = new ModelAndView("home");
			model.addObject("EmpList", lst);
			model.addObject("Register", Register);
			model.addObject("insertEmployee", "newemployee");
			model.addObject("homepage", "emppage");		
			return model;	
		}

		@RequestMapping(value = "/saveEmployee", method = RequestMethod.POST)
		public ModelAndView saveEmployee(@ModelAttribute Employee employee,HttpServletRequest request) {
			//int employeeId = Integer.parseInt(request.getParameter("empid"));
			int edid =  Integer.parseInt(request.getParameter("edid"));
			//String empname = request.getParameter("empname");
			//String emploc = request.getParameter("emploc");
			restTemplate.postForObject("http://gateway-service/Department/employees/"+edid+"/addEmployee", employee, Employee.class);	
			return new ModelAndView("redirect:/emplist?deptid="+edid);		
		}

		@RequestMapping(value = "/deleteEmployee", method = RequestMethod.GET)
		public ModelAndView deleteEmployee(HttpServletRequest request) {
			int employeeId = Integer.parseInt(request.getParameter("id"));
			int edid =  Integer.parseInt(request.getParameter("did"));
			restTemplate.delete("http://gateway-service/Department/employees/"+edid+"/deleteEmployee/"+employeeId);
			return new ModelAndView("redirect:/emplist?deptid="+edid);	
		}

		@SuppressWarnings("unchecked")
		@RequestMapping(value = "/getEmployee", method = RequestMethod.GET)
		public ModelAndView editContact(HttpServletRequest request) {
			int employeeId = Integer.parseInt(request.getParameter("id"));
			int did =  Integer.parseInt(request.getParameter("did"));
			HttpSession session2 = request.getSession();
			List<Employee> lst =(List<Employee>) session2.getAttribute("EmpList");
			session2.setAttribute("EmpList", lst);
			ModelAndView model = new ModelAndView("home");
			model.addObject("homepage", "emppage");
			model.addObject("EmpList", lst);
			model.addObject("employeeid", employeeId);
			model.addObject("Did", did);
			return model;
		}
		@RequestMapping(value = "/updateEmployee", method = RequestMethod.POST)
		public ModelAndView updateEmployee(@ModelAttribute Employee employee,HttpServletRequest request) {
			int employeeId = Integer.parseInt(request.getParameter("empid"));
			int did =  Integer.parseInt(request.getParameter("edid"));
			//String empname = request.getParameter("empname");
			//String emploc = request.getParameter("emploc");
			restTemplate.put("http://gateway-service/Department/employees/"+did+"/updateEmployee/"+employeeId, employee);
			return new ModelAndView("redirect:/emplist?deptid="+did);
		}
	
	
}
