package org.darcy.SimpleProject.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.darcy.SimpleProject.model.Device;
import org.darcy.SimpleProject.service.TransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页
 */
@Controller
public class HomeController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private TransactionalService TransactionalService;

	@RequestMapping("/")
	public String index(HttpSession session, ModelMap m) {
		log.debug("进入首页");
		return "index";
	}

	/**
	 * service、dao测试
	 */
	@ResponseBody
	@RequestMapping("get/{id}")
	public Device get(@PathVariable("id") Integer id) {
		Device device = TransactionalService.get(id);
		return device;
	}

}
