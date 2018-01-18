package fm.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import fm.FootballManagerApplication;
import fm.data.League;
import fm.data.Player;
import fm.db.PlayerDB;
import fm.forms.LoginForm;
import fm.tools.ControllerHelper;

@Controller
public class IndexController {
	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	
	@RequestMapping("/")
	public String index(@CookieValue(value="fm-login", defaultValue="") String login) {
		if(!login.isEmpty()) {
			return "redirect:/logged";
		}
		return "index";
	}

	@RequestMapping("/index")
	public String index2(@CookieValue(value="fm-login", defaultValue="") String login) {
		return index(login);
	}
	
	@GetMapping("/login")
	public String login(LoginForm loginForm) {
		return "login";
	}
	
	@GetMapping("/howToPlay")
	public String howToPlay() {
		return "howToPlay";
	}

	@PostMapping("/login")
	public ModelAndView login(@Valid LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("login");
		}
		
		Player player = pdb.findByLogin(loginForm.getLogin());

		if (player == null) {
			ModelAndView model = new ModelAndView("login");
			model.addObject("errorExist", true);
			return model;
		}
		if (!player.getPassword().equals(DigestUtils.sha256Hex(loginForm.getPassword()))) {
			ModelAndView model = new ModelAndView("login");
			model.addObject("errorPassword", true);
			return model;
		}
		if (player.isBanned()) {
			ModelAndView model = new ModelAndView("login");
			model.addObject("errorBanned", true);
			return model;
		}
		if (!player.isActive()) {
			ModelAndView model = new ModelAndView("login");
			model.addObject("errorInActive", true);
			return model;
		}

		Cookie cookie = new Cookie("fm-login", player.getLogin());
		cookie.setMaxAge(60 * 60 * 24 * 365);
		response.addCookie(cookie);
		FootballManagerApplication.logger.info("Utworzono ciastko: " + cookie);
		return new ModelAndView("redirect:/main");
	}

	@RequestMapping("/main")
	public ModelAndView main(@CookieValue(value = "fm-login") String login) {
		ModelAndView model = ControllerHelper.prepareModelAndView("logged", login);
		model.addObject("league", new League(0, "", 0, 0, 0));
		return model;
	}
	
	@RequestMapping("/logged")
	public String logged() {
		return "redirect:/main";
	}

	@RequestMapping("/logout")
	public String logout(@CookieValue(value = "fm-login") String login, HttpServletResponse response) {
		Cookie cookie = new Cookie("fm-login", login);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "redirect:/index";
	}
}
