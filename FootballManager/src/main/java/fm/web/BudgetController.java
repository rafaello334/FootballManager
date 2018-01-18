package fm.web;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.data.Team;
import fm.db.TeamDB;
import fm.tools.ControllerHelper;

@Controller
public class BudgetController {
	
	private TeamDB tdb = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	
	@GetMapping("/getBudget")
	public ModelAndView getBudget(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") int idTeam) {
		Team team = (Team) tdb.findById(idTeam);
		
		int budget = team.getBudget();
		ModelAndView model = ControllerHelper.prepareModelAndView("getBudget", login);
		model.addObject("budget", budget);
		
		return model;
	}
	
	@GetMapping("/getPrestige")
	public ModelAndView getPrestige(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") int idTeam) {
		Team team = (Team) tdb.findById(idTeam);

		int prestige = team.getPrestige();
		ModelAndView model = ControllerHelper.prepareModelAndView("getPrestige", login);
		model.addObject("prestige", prestige);
		
		return model;
	}
}
