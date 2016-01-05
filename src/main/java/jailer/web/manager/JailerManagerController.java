package jailer.web.manager;

import javax.servlet.http.HttpServletRequest;

import jailer.web.project.JailerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JailerManagerController {
	@Autowired
	private JailerService jailerService;
	
	@RequestMapping("/manager")
	public String top(Model model,
			HttpServletRequest request) {
		model.addAttribute("serviceList", jailerService.getServiceList());
		
		request.setAttribute("pageName", "manager/top");
		return "common_frame";
	}
}
