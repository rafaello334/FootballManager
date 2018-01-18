package fm.web;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.data.Player;
import fm.data.Report;
import fm.db.PlayerDB;
import fm.db.ReportDB;
import fm.db.TeamDB;
import fm.forms.PenaltyForm;
import fm.forms.ReportForm;
import fm.tools.ControllerHelper;

@Controller
public class ReportPlayerController {
	
	private PlayerDB pdb = (PlayerDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	private ReportDB rdb = (ReportDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("ReportDB"));
	private TeamDB tdb = (TeamDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	
	@GetMapping("/reportPlayer")
	public ModelAndView showForm(ReportForm reportForm, BindingResult bindingResult, @CookieValue(value = "fm-login") String login, @RequestParam(name = "target", defaultValue = "") String target) {

		ModelAndView model = ControllerHelper.prepareModelAndView("reportPlayer", login);
		model.addObject("target", target);

		return model;
	}

	@PostMapping("/addReport")
	public ModelAndView report(@CookieValue(value = "fm-login") String login, @Valid ReportForm reportForm, BindingResult bindingResult)
	{
		ModelAndView model = ControllerHelper.prepareModelAndView("reportPlayer", login);
		
		if(bindingResult.hasErrors())
		{
			model.addObject("errorAdd", true);
			return model;
		}
		
		Player playerSource = pdb.findByLogin(login);
		Player playerTarget = pdb.findByLogin(reportForm.getTargetName());
		
		if(playerTarget == null) {
			model.addObject("errorExist", true);
			return model;
		}
		
		LocalDateTime time = LocalDateTime.now();
		rdb.create(playerSource.getIdPlayer(), playerTarget.getIdPlayer(), reportForm.getReason(), time);
		
		
		model.addObject("successAdd", true);
		return model;
	}
	
	@GetMapping("/reportList")
	public ModelAndView listReport(@CookieValue(value = "fm-login") String login)
	{
		ModelAndView model = ControllerHelper.prepareModelAndView("reportList", login);
		List<Report> list = rdb.list();
		
		for(Report rep : list)
		{
			Player player = pdb.findById(rep.getIdPlayerSource());
			rep.setNamePlayerSource(player.getLogin());
			player = pdb.findById(rep.getIdPlayerTarget());
			rep.setNamePlayerTarget(player.getLogin());
		}
		
		model.addObject("reportList", list);
		return model;
	}
	
	@GetMapping("/penalty")
	public ModelAndView penalty(PenaltyForm penaltyForm, BindingResult bindingResult, @CookieValue(value = "fm-login") String login, @RequestParam(name = "target", defaultValue = "") String target) {

		ModelAndView model = ControllerHelper.prepareModelAndView("penalty", login);
		model.addObject("target", target);

		return model;
	}
	
	@PostMapping("/penalty")
	public ModelAndView addPenalty(@CookieValue(value = "fm-login") String login, @Valid PenaltyForm penaltyForm, BindingResult bindingResult) {
		ModelAndView model = ControllerHelper.prepareModelAndView("penalty", login);
		
		if(bindingResult.hasErrors())
		{
			return model;
		}
		
		Player player = pdb.findByLogin(penaltyForm.getTargetName());
		
		if(player == null) {
			model.addObject("errorExist", true);
			return model;
		}
		
		tdb.assignPenalty(player, Math.abs(penaltyForm.getAmount()));
		model.addObject("success", true);
		model.addObject("successLogin", penaltyForm.getTargetName());
		
		return model;
	}
}
