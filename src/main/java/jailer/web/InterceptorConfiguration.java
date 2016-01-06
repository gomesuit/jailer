package jailer.web;

import jailer.web.project.SideMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter{
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new PageNameInterceptor()).addPathPatterns("/project/**");
		registry.addInterceptor(new ManagerPageNameInterceptor()).addPathPatterns("/manager/**");
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
			List<String> urlSplit = Arrays.asList(request.getRequestURI().split("/"));
			String service = urlSplit.get(2);
			
			if(request.getMethod().equals("POST")){
				request.setAttribute("service", service);
				return;
			}
			
			String pageName = (String)request.getAttribute("pageName");
			
			List<SideMenu> menuList = new ArrayList<>();
			menuList.add(new SideMenu("/project/" + service + "/group", "Group", pageName.equals("service")));
			menuList.add(new SideMenu("/project/" + service + "/list", "Connection", pageName.equals("group")));
			
			request.setAttribute("menuList", menuList);
		}

		@Override
		public boolean preHandle(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2) throws Exception {
			return true;
		}
		
	}
	
	private class ManagerPageNameInterceptor implements HandlerInterceptor{

		@Override
		public void afterCompletion(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2, Exception arg3)
				throws Exception {
		}

		@Override
		public void postHandle(HttpServletRequest request,
				HttpServletResponse response, Object obj, ModelAndView mav)
				throws Exception {

			if(request.getMethod().equals("POST")){
				return;
			}
			
			String pageName = (String)request.getAttribute("pageName");
			
			List<SideMenu> menuList = new ArrayList<>();
			
			menuList.add(new SideMenu("/manager", "Service", pageName.equals("manager/top")));
			
			request.setAttribute("menuList", menuList);
		}

		@Override
		public boolean preHandle(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2) throws Exception {
			return true;
		}
		
	}
}
