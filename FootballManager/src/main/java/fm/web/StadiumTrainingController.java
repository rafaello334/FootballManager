package fm.web;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.data.StadiumTraining;
import fm.data.Team;
import fm.db.StadiumTrainingDB;
import fm.db.TeamDB;
import fm.tools.ControllerHelper;

@Controller
public class StadiumTrainingController {
	
	private StadiumTrainingDB sdb = (StadiumTrainingDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("StadiumTrainingDB"));
	private TeamDB tdb = (TeamDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	
	@GetMapping("/stadiumTraining")
	public ModelAndView stadiumTrainingGet(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") long idTeam) {
		StadiumTraining stadium = sdb.findById(idTeam);
		ModelAndView model = ControllerHelper.prepareModelAndView("stadiumTraining", login);
		model.addObject("idTeam", idTeam);
		model.addObject("cost", stadium.getCost());
		model.addObject("xpMultiplier", stadium.getXpMultiplier());
		return model;
	}
	
	@GetMapping("/getXpMultiplier")
	public ModelAndView getMultiplier(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") int idTeam) {
		StadiumTraining stadium = sdb.findById(idTeam);
		
		int xpMultiplier = stadium.getXpMultiplier();
		ModelAndView model = ControllerHelper.prepareModelAndView("getXpMultiplier", login);
		model.addObject("xpMultiplier", xpMultiplier);
		
		return model;
	}
	
	@GetMapping("/getCost")
	public ModelAndView getBudget(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") int idTeam) {
		StadiumTraining stadium = sdb.findById(idTeam);
		
		int cost = stadium.getCost();
		ModelAndView model = ControllerHelper.prepareModelAndView("getCost", login);
		model.addObject("cost", cost);
		
		return model;
	}
	
	@GetMapping("/upgradeStadiumTraining")
	public ModelAndView stadiumTrainingPost(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") long idTeam){
		StadiumTraining stadium = sdb.findById(idTeam);
		Team team = tdb.findById(idTeam);
		ModelAndView model = ControllerHelper.prepareModelAndView("stadiumTraining", login);
		if(team.getBudget() < stadium.getCost()) {
			model.addObject("errorBudget", true);
		}
		else {
			tdb.update(team.getIdTeam(), team.getIdPlayer(), team.getName(), team.getPrestige(), team.getBudget() - stadium.getCost());
			sdb.update(stadium.getIdStadium(), stadium.getXpMultiplier() + 2, stadium.getCost() * 2);
		}
		model.addObject("idTeam", idTeam);
		model.addObject("cost", stadium.getCost());
		model.addObject("xpMultiplier", stadium.getXpMultiplier());
		return model;
	}
}
