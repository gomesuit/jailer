package jailer.web.manager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jailer.web.project.JailerService;
import jailer.web.project.SideMenu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Controller
public class JailerManagerController {
	@Autowired
	private JailerService jailerService;
	
	@RequestMapping("/manager")
	public String top(Model model) {
		model.addAttribute("serviceList", jailerService.getServiceList());

		model.addAttribute("pageName", "manager/top");
		return "common_frame";
	}
	
	@Bean
	public MappedInterceptor interceptor() {
		return new MappedInterceptor(new String[]{"/manager"}, new PageNameInterceptor());
	}
	
	private class PageNameInterceptor implements HandlerInterceptor{

		@Override
		public void afterCompletion(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2, Exception arg3)
				throws Exception {
		}

		@Override
		public void postHandle(HttpServletRequest request,
				HttpServletResponse response, Object obj, ModelAndView mav)
				throws Exception {
			
			String pageName = (String)request.getAttribute("pageName");
			
			System.out.println(pageName);
			
			List<SideMenu> menuList = new ArrayList<>();
			
			menuList.add(new SideMenu("/", "ServiceList", pageName.equals("top")));
			
			request.setAttribute("menuList", menuList);
		}

		@Override
		public boolean preHandle(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2) throws Exception {
			return true;
		}
		
	}
}
