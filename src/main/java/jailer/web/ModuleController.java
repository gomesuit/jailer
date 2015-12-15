package jailer.web;

import javax.servlet.http.HttpServletRequest;

import jailer.core.JailerDataSource;
import jailer.web.zookeeper.ZookeeperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ModuleController {
	@Autowired
	private ZookeeperService zookeeperService;
	
    @RequestMapping("/")
    String sample(Model model) throws Exception {
    	model.addAttribute("dataSourceIdForm", new DataSourceIdForm());
    	model.addAttribute("dataSourceIdList", zookeeperService.getDataSourceIdList());
    	
        return "sample";
    }

    @RequestMapping(value="/dataSourceId/regist", method=RequestMethod.POST)
	private String registDataSourceId(@ModelAttribute DataSourceIdForm form, HttpServletRequest request) throws Exception{
    	zookeeperService.registDataSourceId(form);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping("/dataSource/view")
	private String viewDataSource(
			@RequestParam(value = "dataSourceId", required = true) String dataSourceId,
			Model model) throws Exception{
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(dataSourceId);
    	model.addAttribute("jailerDataSource", jailerDataSource);
    	
    	DataSourceForm dataSourceForm = new DataSourceForm();
    	dataSourceForm.setDataSourceId(jailerDataSource.getDataSourceId());
    	dataSourceForm.setUrl(jailerDataSource.getUrl());
    	model.addAttribute("dataSourceForm", dataSourceForm);
    	
    	DataSourceParameterForm dataSourceParameterForm = new DataSourceParameterForm();
    	dataSourceParameterForm.setDataSourceId(jailerDataSource.getDataSourceId());
    	model.addAttribute("dataSourceParameterForm", dataSourceParameterForm);
    	
    	return "sample2";
    }

    @RequestMapping(value="/dataSource/regist", method=RequestMethod.POST)
	private String registRole(@ModelAttribute DataSourceForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(form.getDataSourceId());
    	jailerDataSource.setUrl(form.getUrl());
    	
    	zookeeperService.registDataSource(jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

    @RequestMapping(value="/dataSourceParameter/regist", method=RequestMethod.POST)
	private String registdataSourceParameter(@ModelAttribute DataSourceParameterForm form, HttpServletRequest request) throws Exception{
    	JailerDataSource jailerDataSource = zookeeperService.getJailerDataSource(form.getDataSourceId());
    	jailerDataSource.addProperty(form.getKey(), form.getValue());
    	
    	zookeeperService.registDataSource(jailerDataSource);
		
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
