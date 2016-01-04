package jailer.web.manager;

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
	public String top(Model model) {
		model.addAttribute("serviceList", jailerService.getServiceList());

		model.addAttribute("pageName", "top");
		return "common_frame";
	}
}
