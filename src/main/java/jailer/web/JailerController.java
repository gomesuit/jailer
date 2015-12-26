package jailer.web;

import javax.servlet.http.HttpServletRequest;

import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JailerController {
	@Autowired
	private JailerService jailerService;
	

	@RequestMapping("/")
	public String top(Model model){
		model.addAttribute("serviceList", jailerService.getServiceList());

		model.addAttribute("pageName", "top");
		return "common_frame";
	}
	
	@RequestMapping("/service")
	public String service(
    		@RequestParam(value = "service", required = true) String service,
    		Model model){
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);

		model.addAttribute("service", service);
    	model.addAttribute("groupKey", new GroupKey());
		model.addAttribute("groupList", jailerService.getGroupList(key));
		
		model.addAttribute("pageName", "service");
		return "common_frame";
	}
	
    @RequestMapping("/group")
    public String group(
    		@RequestParam(value = "service", required = true) String service,
    		@RequestParam(value = "group", required = true) String group,
    		Model model) throws Exception {
    	
    	GroupKey key = new GroupKey();
    	key.setServiceId(service);
    	key.setGroupId(group);

    	model.addAttribute("groupKey", key);
    	model.addAttribute("dataSourceKey", new DataSourceKey());
    	model.addAttribute("dataSourceIdList", jailerService.getDataSourceIdList(key));
    	

		model.addAttribute("pageName", "group");
		return "common_frame";
    }

    @RequestMapping(value="/dataSource/regist", method=RequestMethod.POST)
	private String registDataSourceId(@ModelAttribute DataSourceKey key, HttpServletRequest request) throws Exception{
    	jailerService.registDataSourceId(key);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/group/regist", method=RequestMethod.POST)
	private String registGroup(@ModelAttribute GroupKey key, HttpServletRequest request) throws Exception{
    	jailerService.registGroup(key);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping("/dataSource")
	private String viewDataSource(
    		@RequestParam(value = "service", required = true) String service,
    		@RequestParam(value = "group", required = true) String group,
			@RequestParam(value = "dataSource", required = true) String dataSourceId,
			Model model) throws Exception{
    	
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

    @RequestMapping(value="/dataSource/update", method=RequestMethod.POST)
	private String registRole(@ModelAttribute DataSourceForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = jailerService.getJailerDataSource(form);
    	jailerDataSource.setUrl(form.getUrl());
    	
    	jailerService.registDataSource(form, jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/dataSourceParameter/regist", method=RequestMethod.POST)
	private String registdataSourceParameter(@ModelAttribute DataSourceParameterForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = jailerService.getJailerDataSource(form);
    	jailerDataSource.addProperty(form.getKey(), form.getValue());
    	
    	jailerService.registDataSource(form, jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/dataSourceParameter/remove", method=RequestMethod.POST)
	private String removedataSourceParameter(@ModelAttribute DataSourceParameterForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = jailerService.getJailerDataSource(form);
    	jailerDataSource.removeProperty(form.getKey());
    	
    	jailerService.registDataSource(form, jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
