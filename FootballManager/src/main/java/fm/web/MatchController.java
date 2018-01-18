package fm.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.data.League;
import fm.data.Match;
import fm.data.NPCPlayer;
import fm.data.Player;
import fm.data.Team;
import fm.db.LeagueDB;
import fm.db.MatchDB;
import fm.db.NPCPlayerDB;
import fm.db.PlayerDB;
import fm.db.TeamDB;
import fm.tools.ControllerHelper;
import fm.tools.Simulation;

@Controller
public class MatchController {

	private TeamDB teamDB = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	private LeagueDB leagueDB = (LeagueDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("LeagueDB"));
	private MatchDB matchDB = (MatchDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("MatchDB"));
	private NPCPlayerDB npcdb = (NPCPlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCPlayerDB"));
	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));

	@GetMapping("/matchDetails")
	public ModelAndView matchDetails(@CookieValue(value = "fm-login") String login, @RequestParam("idGame") long idGame,
			@RequestParam("idTeam") long idTeam, @RequestParam("idLeague") long idLeague, @RequestParam(name = "admin", defaultValue = "false") boolean adminPage) {

		League league = leagueDB.findById(idLeague);
		Match match = matchDB.findById(idGame);
		
		Team team1 = teamDB.findById(match.getIdTeam1());
		ArrayList<NPCPlayer> squadTeam1 = (ArrayList<NPCPlayer>) npcdb.listSquad(team1.getIdTeam());
		
		Team team2 = teamDB.findById(match.getIdTeam2());
		ArrayList<NPCPlayer> squadTeam2 = (ArrayList<NPCPlayer>) npcdb.listSquad(team2.getIdTeam());

		ModelAndView model = ControllerHelper.prepareModelAndView("matchDetails", login);
		model.addObject("idTeam", idTeam);
		model.addObject("league", league);
		model.addObject("match", match);
		model.addObject("team1", team1);
		model.addObject("squadTeam1", squadTeam1);
		model.addObject("team2", team2);
		model.addObject("squadTeam2", squadTeam2);
		model.addObject("adminPage", adminPage);

		return model;
	}
	
	@PostMapping("/playNow")
	public String playNow(@CookieValue(value = "fm-login") String login, @RequestParam("idGame") long idGame) {
		
		Match match = matchDB.findById(idGame);
		String score;
		
		score = Simulation.matchStimulation(Simulation.teamPower(npcdb, match.getIdTeam1()), 
									Simulation.teamPower(npcdb, match.getIdTeam2()), 
									Simulation.matchMorale(teamDB, match.getIdTeam1(), match.getIdTeam2()));
		
		matchDB.updatePlayNow(idGame, score);
		return "redirect:/main";
	}
	
	@GetMapping("/getPlayerMatches")
	public ModelAndView getPlayerMatches(@CookieValue(value = "fm-login") String login) {
		ModelAndView model = ControllerHelper.prepareModelAndView("getPlayerMatches", login);
		
		Player p = pdb.findByLogin(login);
		List<Match> list = matchDB.listIncoming(p.getIdPlayer());
		
		model.addObject("matchList", list);
		model.addObject("teamDB", teamDB);
		
		return model;
	}
	
	@GetMapping("/getTeamMatches")
	public ModelAndView getTeamMatches(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") long idTeam) {
		ModelAndView model = ControllerHelper.prepareModelAndView("getPlayerMatches", login);
		
		List<Match> list = matchDB.listIncomingForTeam(idTeam);
		
		model.addObject("matchList", list);
		model.addObject("teamDB", teamDB);
		
		return model;
	}
}
