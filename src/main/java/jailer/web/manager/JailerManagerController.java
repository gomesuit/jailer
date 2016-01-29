package jailer.web.manager;

import javax.servlet.http.HttpServletRequest;

import jailer.core.model.ServiceKey;
import jailer.web.JailerControllerBase;
import jailer.web.project.JailerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JailerManagerController extends JailerControllerBase{	
	@Autowired
	private JailerService jailerService;
	@Autowired
	private MessageSource msg;
	
	@RequestMapping("/manager")
	public String top(Model model,
			HttpServletRequest request) {
		model.addAttribute("serviceList", jailerService.getServiceList());
		model.addAttribute("serviceKey", new ServiceKey());
		
		request.setAttribute("pageName", "manager/top");
		return "common_frame";
	}

	@RequestMapping(value = "/manager/service/regist", method = RequestMethod.POST)
	public String registService(@ModelAttribute ServiceKey key,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		jailerService.registService(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/manager/service/delete", method = RequestMethod.POST)
	public String deleteService(@ModelAttribute ServiceKey key,
			HttpServletRequest request) throws Exception {
		jailerService.deleteService(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
