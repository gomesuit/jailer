package jailer.web;

import jailer.web.project.JailerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.KeeperException.NotEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public abstract class JailerControllerBase {
	private Logger log = LoggerFactory.getLogger(JailerControllerBase.class);
	
	@Autowired
	private JailerService jailerService;
	@Autowired
	private MessageSource msg;
	
	@ExceptionHandler(NodeExistsException.class)
    public String NodeExistsExceptionHandler(HttpServletRequest request, Exception ex) {
		log.error("occur NodeExistsException !!", ex);

		FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);

		List<String> alertList = new ArrayList<>();
		alertList.add(msg.getMessage("errors.regist.exists", null, Locale.JAPAN));
		outputFlashMap.put("alertList", alertList);
        
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
    }

	@ExceptionHandler(NotEmptyException.class)
    public String NotEmptyExceptionHandler(HttpServletRequest request, Exception ex) {
		log.error("occur NotEmptyException !!", ex);

		FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);

		List<String> alertList = new ArrayList<>();
		alertList.add(msg.getMessage("errors.delete.NotEmpty", null, Locale.JAPAN));
		outputFlashMap.put("alertList", alertList);
        
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
    }
}
