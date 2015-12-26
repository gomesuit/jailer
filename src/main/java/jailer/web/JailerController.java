package jailer.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Controller
public class JailerController {
	@Autowired
	private JailerService jailerService;

	@RequestMapping("/")
	public String top(Model model) {
		model.addAttribute("serviceList", jailerService.getServiceList());

		model.addAttribute("pageName", "top");
		return "common_frame";
	}
	
	@Bean
    public MappedInterceptor interceptor() {
		return new MappedInterceptor(new String[]{"/*"}, new SideMenuInterceptor());
	}
	
	private class SideMenuInterceptor implements HandlerInterceptor{

		@Override
		public void afterCompletion(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2, Exception arg3)
				throws Exception {
			
		}

		@Override
		public void postHandle(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2, ModelAndView arg3)
				throws Exception {
			
		}

		@Override
		public boolean preHandle(HttpServletRequest arg0,
				HttpServletResponse arg1, Object arg2) throws Exception {
			
			System.out.println("aaaa");
			
			return true;
		}
		
	}

	@RequestMapping("/{service}/group")
	public String service(
			@PathVariable String service,
			Model model) {

		model.addAttribute("service", service);
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);
		
		model.addAttribute("groupKey", new GroupKey());
		model.addAttribute("groupList", jailerService.getGroupList(key));

		model.addAttribute("pageName", "service");
		return "common_frame";
	}

	@RequestMapping("/{service}/list")
	public String group(
			@PathVariable String service,
			Model model) throws Exception {

		model.addAttribute("service", service);

		ServiceKey key = new ServiceKey();
		key.setServiceId(service);

		model.addAttribute("serviceKey", key);
		model.addAttribute("groupList", jailerService.getGroupList(key));
		model.addAttribute("dataSourceKey", new DataSourceKey());
		model.addAttribute("dataSourceList", jailerService.getDataSourceKeyList(key));


		model.addAttribute("pageName", "group");
		return "common_frame";
	}

	@RequestMapping("/{service}/dataSource")
	public String viewDataSource(
			@PathVariable String service,
			@RequestParam(value = "group", required = true) String group,
			@RequestParam(value = "dataSource", required = true) String dataSourceId,
			Model model) throws Exception {

		model.addAttribute("service", service);
	
		DataSourceKey key = new DataSourceKey();
		key.setServiceId(service);
		key.setGroupId(group);
		key.setDataSourceId(dataSourceId);
	
		model.addAttribute("dataSourceKey", key);
	
		JailerDataSource jailerDataSource = jailerService.getJailerDataSource(key);
		model.addAttribute("jailerDataSource", jailerDataSource);
	
		DataSourceForm dataSourceForm = new DataSourceForm(key);
		dataSourceForm.setUrl(jailerDataSource.getUrl());
		model.addAttribute("dataSourceForm", dataSourceForm);
	
		DataSourceParameterForm dataSourceParameterForm = new DataSourceParameterForm(key);
		model.addAttribute("dataSourceParameterForm", dataSourceParameterForm);
	
		model.addAttribute("connectionList", jailerService.getConnectionList(key));
	
		model.addAttribute("pageName", "datasource");
		return "common_frame";
	}

	@RequestMapping(value = "/dataSource/regist", method = RequestMethod.POST)
	public String registDataSourceId(@ModelAttribute DataSourceKey key,
			HttpServletRequest request) throws Exception {
		jailerService.registDataSourceId(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/group/regist", method = RequestMethod.POST)
	public String registGroup(@ModelAttribute GroupKey key,
			HttpServletRequest request) throws Exception {
		jailerService.registGroup(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/dataSource/update", method = RequestMethod.POST)
	public String registRole(@ModelAttribute DataSourceForm form,
			HttpServletRequest request) throws Exception {
		
		JailerDataSource jailerDataSource = jailerService.getJailerDataSource(form);
		jailerDataSource.setUrl(form.getUrl());

		jailerService.registDataSource(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/dataSourceParameter/regist", method = RequestMethod.POST)
	public String registdataSourceParameter(
			@ModelAttribute DataSourceParameterForm form,
			HttpServletRequest request) throws Exception {
		
		JailerDataSource jailerDataSource = jailerService.getJailerDataSource(form);
		jailerDataSource.addProperty(form.getKey(), form.getValue());

		jailerService.registDataSource(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/dataSourceParameter/remove", method = RequestMethod.POST)
	public String removedataSourceParameter(
			@ModelAttribute DataSourceParameterForm form,
			HttpServletRequest request) throws Exception {
		JailerDataSource jailerDataSource = jailerService
				.getJailerDataSource(form);
		jailerDataSource.removeProperty(form.getKey());

		jailerService.registDataSource(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
