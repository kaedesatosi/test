package top.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.taotao.common.pojo.TaotaoResult;
import top.taotao.common.utils.CookieUtils;
import top.taotao.common.utils.JsonUtils;
import top.taotao.pojo.TbUser;
import top.taotao.sso.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;

	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public TaotaoResult checkUserData(@PathVariable String param, @PathVariable int type) {
		TaotaoResult taotaoResult = userService.checkData(param, type);
		return taotaoResult;
	}

	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult register(TbUser user) {
		TaotaoResult result = userService.register(user);
		return result;
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		TaotaoResult result = userService.login(username, password);
		if(result.getStatus() == 200) {
			CookieUtils.setCookie(request, response, TOKEN_KEY, result.getData().toString());			
		}
		return result;
	}

	/**
	 * jsonp
	 * @param token
	 * @param callBack
	 * @return
	 */
	@RequestMapping(value = "/user/token/{token}", method = RequestMethod.GET,
					//指定返回的content-type
					produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callBack) {
		 TaotaoResult result = userService.getUserByToken(token);
		 if(StringUtils.isNotBlank(callBack)) {
			 return callBack + "(" + JsonUtils.objectToJson(result) + ");" ;
		 }
		 return JsonUtils.objectToJson(result);
	}
	/**
	 * 
	 * @param token
	 * @return
	 */
//	@RequestMapping(value = "/user/token/{token}", method = RequestMethod.GET)
//	@ResponseBody
//	public Object getUserByToken(@PathVariable String token,String callBack) {
//		TaotaoResult result = userService.getUserByToken(token);
//		if(StringUtils.isNotBlank(callBack)) {
//			MappingJacksonValue jacksonValue = new MappingJacksonValue(result);
//			jacksonValue.setJsonpFunction(callBack);
//			return jacksonValue;
//		}
//		return result;
//	}
	
	@RequestMapping(value = "/user/logout/{token}", method = RequestMethod.GET)
	@ResponseBody
	public TaotaoResult logout(@PathVariable String token) {
		return userService.logout(token);
	}

}
