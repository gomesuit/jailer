package jailer.web.manager;

import javax.servlet.http.HttpServletRequest;

import jailer.core.model.ServiceKey;
import jailer.web.project.JailerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class JailerManagerController {
	@Autowired
	private JailerService jailerService;
	
	@RequestMapping("/manager")
	public String top(Model model,
			HttpServletRequest request) {
		model.addAttribute("serviceList", jailerService.getServiceList());
		model.addAttribute("serviceKey", new ServiceKey());
		
		request.setAttribute("pageName", "manager/top");
		return "common_frame";
	}

	@RequestMapping(value = "/service/regist", method = RequestMethod.POST)
	public String registService(@ModelAttribute ServiceKey key,
			HttpServletRequest request) throws Exception {
		jailerService.registService(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
