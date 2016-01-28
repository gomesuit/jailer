package jailer.web.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.PropertyContents;
import jailer.core.model.ServiceKey;
import jailer.web.util.JDBCURLUtils;

@Controller
public class JailerController {
	@Autowired
	private JailerService jailerService;

	@RequestMapping("/")
	public String top(
			Model model,
			HttpServletRequest request) {
		model.addAttribute("serviceList", jailerService.getServiceList());
		
		request.setAttribute("pageName", "top");
		return "common_frame";
	}

	@RequestMapping("/project/{service}/group")
	public String service(
			@PathVariable String service,
			Model model,
			HttpServletRequest request) {
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);
		
		// 登録・削除
		model.addAttribute("groupKey", new GroupKey());
		
		// 一覧
		model.addAttribute("groupList", jailerService.getGroupList(key));

		request.setAttribute("pageName", "service");
		return "common_frame";
	}

	@RequestMapping("/project/{service}/list")
	public String group(
			@PathVariable String service,
			Model model,
			HttpServletRequest request) throws Exception {
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);
		
		// ページ共通キー
		model.addAttribute("serviceKey", key);
		
		// 登録時グループリスト
		model.addAttribute("groupList", jailerService.getGroupList(key));
		
		// 登録フォーム
		model.addAttribute("dataSourceInputForm", new DataSourceKey());
		
		// 一覧
		model.addAttribute("connectionList", jailerService.getConnectionInfoList(key));
		
		request.setAttribute("pageName", "group");
		return "common_frame";
	}

	@RequestMapping("/project/{service}/dataSource")
	public String viewDataSource(
			@PathVariable String service,
			@RequestParam(value = "group", required = true) String group,
			@RequestParam(value = "dataSource", required = true) String dataSourceId,
			Model model,
			HttpServletRequest request) throws Exception {
	
		DataSourceKey key = new DataSourceKey();
		key.setServiceId(service);
		key.setGroupId(group);
		key.setDataSourceId(dataSourceId);
	
		model.addAttribute("dataSourceKey", key);
		
		model.addAttribute("connectString", jailerService.getConnectString());
	
		JailerDataSource jailerDataSourceCorrent = jailerService.getJailerDataSource(key);
		model.addAttribute("jailerDataSourceCorrent", jailerDataSourceCorrent);

		model.addAttribute("jailerDataSourcePlan", jailerService.getJailerDataSourcePlan(key));

		DataSourceForm dataSourceForm = new DataSourceForm(key);
		dataSourceForm.setUrl(jailerDataSourceCorrent.getUrl());
		model.addAttribute("dataSourceForm", dataSourceForm);
	
		DriverForm driverForm = new DriverForm(key);
		driverForm.setDriverName(jailerDataSourceCorrent.getDriverName());
		model.addAttribute("driverForm", driverForm);
	
		DataSourceParameterForm dataSourceParameterForm = new DataSourceParameterForm(key);
		model.addAttribute("dataSourceParameterForm", dataSourceParameterForm);
	
		model.addAttribute("connectionList", jailerService.getConnectionList(key));
		
		String url = jailerDataSourceCorrent.getUrl();
		if(jailerService.isExistCheck(key, url)){
			List<String> messageList = new ArrayList<>();
			String host = JDBCURLUtils.getHost(url);
			String databaseName = JDBCURLUtils.getDatabaseName(url);
			messageList.add("host名『" + host + "』、database名『" + databaseName + "』は既に登録されています。");
			model.addAttribute("messageList", messageList);
		}
	
		request.setAttribute("pageName", "datasource");
		return "common_frame";
	}

	@RequestMapping(value = "/project/{service}/dataSource/regist", method = RequestMethod.POST)
	public String registDataSourceId(@ModelAttribute DataSourceKey key,
			HttpServletRequest request) throws Exception {
		jailerService.registDataSourceId(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/dataSource/delete", method = RequestMethod.POST)
	public String deleteDataSourceId(@ModelAttribute DataSourceKey key,
			HttpServletRequest request) throws Exception {
		jailerService.deleteDataSourceId(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/group/regist", method = RequestMethod.POST)
	public String registGroup(@ModelAttribute GroupKey key,
			HttpServletRequest request) throws Exception {
		jailerService.registGroup(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/group/delete", method = RequestMethod.POST)
	public String deleteGroup(@ModelAttribute GroupKey key,
			HttpServletRequest request) throws Exception {
		jailerService.deleteGroup(key);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/dataSource/update", method = RequestMethod.POST)
	public String registDataSource(@ModelAttribute DataSourceForm form,
			HttpServletRequest request) throws Exception {
		
		JailerDataSource jailerDataSource = jailerService.getJailerDataSourcePlan(form);
		jailerDataSource.setUrl(form.getUrl());
		jailerDataSource.setHide(form.isHide());

		jailerService.registDataSourcePlan(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/driver/update", method = RequestMethod.POST)
	public String registDriver(@ModelAttribute DriverForm form,
			HttpServletRequest request) throws Exception {
		
		JailerDataSource jailerDataSource = jailerService.getJailerDataSourcePlan(form);
		jailerDataSource.setDriverName(form.getDriverName());

		jailerService.registDataSourcePlan(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/dataSourceParameter/regist", method = RequestMethod.POST)
	public String registdataSourceParameter(
			@ModelAttribute DataSourceParameterForm form,
			HttpServletRequest request) throws Exception {
		
		JailerDataSource jailerDataSource = jailerService.getJailerDataSourcePlan(form);
		jailerDataSource.addProperty(form.getKey(), new PropertyContents(form.getValue(), form.isHide()));

		jailerService.registDataSourcePlan(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/dataSourceParameter/remove", method = RequestMethod.POST)
	public String removedataSourceParameter(
			@ModelAttribute DataSourceParameterForm form,
			HttpServletRequest request) throws Exception {
		JailerDataSource jailerDataSource = jailerService.getJailerDataSourcePlan(form);
		jailerDataSource.removeProperty(form.getKey());

		jailerService.registDataSourcePlan(form, jailerDataSource);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/project/{service}/dataSource/reflection", method = RequestMethod.POST)
	public String reflectionDataSource(@ModelAttribute DataSourceKey key, HttpServletRequest request) throws Exception{
		JailerDataSource jailerDataSourcePlan = jailerService.getJailerDataSourcePlan(key);
		jailerService.registDataSourceCorrent(key, jailerDataSourcePlan);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
	
	@RequestMapping("/project/{service}/jdbcSearch")
	public String jdbcSearch(
			@PathVariable String service,
			Model model,
			HttpServletRequest request) throws Exception {
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);
		
		// ページ共通キー
		model.addAttribute("serviceKey", key);
		
		// 一覧
		model.addAttribute("jDBCSearchInfoRowList", jailerService.getJDBCSearchInfoRowList(key));
		
		// ページ設定
		request.setAttribute("pageName", "jdbc_search");
		
		return "common_frame";
	}
	
	@RequestMapping("/project/{service}/connectionSearch")
	public String connectionSearch(
			@PathVariable String service,
			Model model,
			HttpServletRequest request) throws Exception {
		
		ServiceKey key = new ServiceKey();
		key.setServiceId(service);
		
		// ページ共通キー
		model.addAttribute("serviceKey", key);
		
		// 一覧
		model.addAttribute("connectionSearchInfoRowList", jailerService.getConnectionSearchInfoRowList(key));
		
		// ページ設定
		request.setAttribute("pageName", "connection_search");
		
		return "common_frame";
	}
}
