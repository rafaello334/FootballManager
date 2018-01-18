package fm.web;

import java.util.ArrayList;
import java.util.Collections;

import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.FootballManagerApplication;
import fm.data.Player;
import fm.data.Player.PlayerType;
import fm.db.PlayerDB;
import fm.forms.AccountManagementForm;
import fm.forms.EditAccountForm;
import fm.forms.RegisterForm;
import fm.tools.ControllerHelper;

@Controller
public class AccountController {

	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));

	@RequestMapping("/register")
	public String showForm(RegisterForm registerForm) {
		return "register";
	}

	@PostMapping("/confirmRegister")
	public ModelAndView checkRegister(@Valid RegisterForm registerForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("register");
		}

		if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
			ModelAndView model = new ModelAndView("register");
			model.addObject("errorPassword", true);
			return model;
		}

		if (pdb.checkUniqueness(registerForm.getLogin(), registerForm.getEmail())) {
			Player player = pdb.create(registerForm.getLogin(), DigestUtils.sha256Hex(registerForm.getPassword()),
					registerForm.getEmail(), PlayerType.PLAYER.toString());

			ModelAndView model = new ModelAndView("register");
			model.addObject("success", true);
			model.addObject("login", player.getLogin());
			return model;
		} else {
			ModelAndView model = new ModelAndView("register");
			model.addObject("errorUnique", true);
			return model;
		}
	}

	@GetMapping("/accountManagement")
	public ModelAndView accountManagement(AccountManagementForm accountManagementForm,
			@CookieValue(value = "fm-login") String login) {
		ArrayList<Player> playersList = (ArrayList<Player>) pdb.listActive();

		ModelAndView model = ControllerHelper.prepareModelAndView("accountManagement", login);
		model.addObject("playersList", playersList);
		return model;
	}

	@PostMapping("/accountManagement")
	public String banAccountPost(@RequestParam("accountId") int accountId) {
		Player player = pdb.findById(accountId);
		if (!player.isBanned()) {
			pdb.banned(accountId, 1);
			FootballManagerApplication.logger.info("Zbanowano użytkownika: " + player);
		}

		if (player.isBanned()) {
			pdb.banned(accountId, 0);
			FootballManagerApplication.logger.info("Odbanowano użytkownika: " + player);
		}

		return "redirect:/accountManagement";
	}

	@GetMapping("/activateAccount")
	public ModelAndView activateAccountGet(@CookieValue(value = "fm-login") String login) {
		ArrayList<Player> inactivePlayerList = (ArrayList<Player>) pdb.listInactive();

		ModelAndView model = ControllerHelper.prepareModelAndView("activateAccount", login);
		model.addObject("inactivePlayerList", inactivePlayerList);
		return model;
	}

	@PostMapping("/activateAccount")
	public String activateAccountPost(@RequestParam("accountId") int accountId) {
		pdb.activate(accountId);

		return "redirect:/activateAccount";

	}

	@GetMapping("/editAccount")
	public ModelAndView index(EditAccountForm editAccountForm, @CookieValue(value = "fm-login") String login) {
		ModelAndView model = ControllerHelper.prepareModelAndView("editAccount", login);
		return model;
	}

	@PostMapping("/editAccount")
	public ModelAndView edit(@Valid EditAccountForm editAccountForm, BindingResult bindingResult,
			@CookieValue(value = "fm-login") String login) {
		Player player = pdb.findByLogin(login);

		if (bindingResult.hasErrors()) {
			ModelAndView model = ControllerHelper.prepareModelAndView("editAccount", login);
			return model;
		}

		if (!(editAccountForm.getEmail().length() == 0 || (editAccountForm.getEmail().length() >= 8
				&& editAccountForm.getEmail().matches("[a-zA-Z0-9.-]+@[a-zA-Z-]+.[a-zA-Z.]+")))) {
			ModelAndView model = ControllerHelper.prepareModelAndView("editAccount", login);
			model.addObject("errorEmail", true);
			return model;
		}

		if (!player.getPassword().equals(DigestUtils.sha256Hex(editAccountForm.getPassword()))) {
			ModelAndView model = ControllerHelper.prepareModelAndView("editAccount", login);
			model.addObject("errorPassword", true);
			return model;
		}

		if (!editAccountForm.getNewPassword().equals(editAccountForm.getConfirmNewPassword())) {
			ModelAndView model = ControllerHelper.prepareModelAndView("editAccount", login);
			model.addObject("errorPassword", true);
			return model;
		} else {
			if (!editAccountForm.getNewPassword().equals("") && !editAccountForm.getEmail().equals("")
					&& (pdb.checkUniqueness(editAccountForm.getEmail())))
				pdb.update(player.getIdPlayer(), player.getLogin(),
						DigestUtils.sha256Hex(editAccountForm.getConfirmNewPassword()), editAccountForm.getEmail(),
						player.getType().toString());
			else if (!editAccountForm.getNewPassword().equals(""))
				pdb.update(player.getIdPlayer(), player.getLogin(),
						DigestUtils.sha256Hex(editAccountForm.getNewPassword()), player.getEmail(),
						player.getType().toString());
			else if (!editAccountForm.getEmail().equals("") && (pdb.checkUniqueness(editAccountForm.getEmail())))
				pdb.update(player.getIdPlayer(), player.getLogin(), player.getPassword(), editAccountForm.getEmail(),
						player.getType().toString());

			ModelAndView model = ControllerHelper.prepareModelAndView("editAccount", login);
			model.addObject("success", true);
			FootballManagerApplication.logger.info(model);
			return model;

		}
	}

	@GetMapping("/changePermission")
	public ModelAndView changePermissionGet(@CookieValue(value = "fm-login") String login) {
		ArrayList<Player> playersList = (ArrayList<Player>) pdb.listActive();
		Collections.sort(playersList);

		ModelAndView model = ControllerHelper.prepareModelAndView("accountManagement", login);
		model.addObject("playersList", playersList);
		return model;
	}
	
	@PostMapping("/changePermissionToAdmin")
	public ModelAndView changePermissionToAdminPost(@CookieValue(value = "fm-login") String login, long idPlayer) {
		
		pdb.changePermission("a", idPlayer);
		
		ArrayList<Player> playersList = (ArrayList<Player>) pdb.listActive();
		Collections.sort(playersList);

		ModelAndView model = ControllerHelper.prepareModelAndView("accountManagement", login);
		model.addObject("playersList", playersList);
		return model;
	}
	
	@PostMapping("/changePermissionToMod")
	public ModelAndView changePermissionToModPost(@CookieValue(value = "fm-login") String login, long idPlayer) {
		
		pdb.changePermission("m", idPlayer);
		
		ArrayList<Player> playersList = (ArrayList<Player>) pdb.listActive();
		Collections.sort(playersList);

		ModelAndView model = ControllerHelper.prepareModelAndView("accountManagement", login);
		model.addObject("playersList", playersList);
		return model;
	}
	
	@PostMapping("/changePermissionToPlayer")
	public ModelAndView changePermissionToPlayerPost(@CookieValue(value = "fm-login") String login, long idPlayer) {
		
		pdb.changePermission("p", idPlayer);
		
		ArrayList<Player> playersList = (ArrayList<Player>) pdb.listActive();
		Collections.sort(playersList);

		ModelAndView model = ControllerHelper.prepareModelAndView("accountManagement", login);
		model.addObject("playersList", playersList);
		return model;
	}

}
