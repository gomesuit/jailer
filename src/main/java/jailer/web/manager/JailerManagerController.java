package jailer.web.manager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import jailer.core.model.ServiceKey;
import jailer.web.project.JailerService;

import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JailerManagerController {
	@Autowired
	private JailerService jailerService;
	
	@RequestMapping("/manager")
	public String top(Model model,
			HttpServletRequest request) {
		model.addAttribute("serviceList", jailerService.getServiceList());
		model.addAttribute("serviceKey", new ServiceKey());
		
		System.out.println("model : " + model);
		
		request.setAttribute("pageName", "manager/top");
		return "common_frame";
	}

	@RequestMapping(value = "/manager/service/regist", method = RequestMethod.POST)
	public String registService(@ModelAttribute ServiceKey key,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		try{
			jailerService.registService(key);
		}catch(NodeExistsException e){
			List<String> alertList = new ArrayList<>();
			alertList.add(key.getServiceId() + "は既に存在しています。");
			redirectAttrs.addFlashAttribute("alertList", alertList);
		}

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
