package jailer.web;

import javax.servlet.http.HttpServletRequest;

import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;
import jailer.web.zookeeper.ZookeeperService;

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
	private ZookeeperService zookeeperService;
	

	@RequestMapping("/")
	public String top(Model model){
		model.addAttribute("serviceList", zookeeperService.getServiceList());
		
		return "top";
	}
	
	@RequestMapping("/service")
	public String service(
    		@RequestParam(value = "service", required = true) String service,
    		Model model){
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);

		model.addAttribute("service", service);
    	model.addAttribute("groupKey", new GroupKey());
		model.addAttribute("groupList", zookeeperService.getGroupList(key));
		
		return "service";
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
    	model.addAttribute("dataSourceIdList", zookeeperService.getDataSourceIdList(key));
    	
        return "group";
    }

    @RequestMapping(value="/dataSource/regist", method=RequestMethod.POST)
	private String registDataSourceId(@ModelAttribute DataSourceKey key, HttpServletRequest request) throws Exception{
    	zookeeperService.registDataSourceId(key);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/group/regist", method=RequestMethod.POST)
	private String registGroup(@ModelAttribute GroupKey key, HttpServletRequest request) throws Exception{
    	zookeeperService.registGroup(key);
		
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
    	
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(key);
    	model.addAttribute("jailerDataSource", jailerDataSource);
    	
    	DataSourceForm dataSourceForm = new DataSourceForm(key);
    	dataSourceForm.setUrl(jailerDataSource.getUrl());
    	model.addAttribute("dataSourceForm", dataSourceForm);
    	
    	DataSourceParameterForm dataSourceParameterForm = new DataSourceParameterForm(key);
    	model.addAttribute("dataSourceParameterForm", dataSourceParameterForm);
    	
    	model.addAttribute("connectionList", zookeeperService.getConnectionList(key));
    	
    	return "datasource";
    }

    @RequestMapping(value="/dataSource/update", method=RequestMethod.POST)
	private String registRole(@ModelAttribute DataSourceForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(form);
    	jailerDataSource.setUrl(form.getUrl());
    	
    	zookeeperService.registDataSource(form, jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/dataSourceParameter/regist", method=RequestMethod.POST)
	private String registdataSourceParameter(@ModelAttribute DataSourceParameterForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(form);
    	jailerDataSource.addProperty(form.getKey(), form.getValue());
    	
    	zookeeperService.registDataSource(form, jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/dataSourceParameter/remove", method=RequestMethod.POST)
	private String removedataSourceParameter(@ModelAttribute DataSourceParameterForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(form);
    	jailerDataSource.removeProperty(form.getKey());
    	
    	zookeeperService.registDataSource(form, jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
