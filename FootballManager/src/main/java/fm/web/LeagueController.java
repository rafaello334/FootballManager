package fm.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import fm.data.League;
import fm.data.Match;
import fm.data.Player;
import fm.data.Team;
import fm.db.LeagueDB;
import fm.db.MatchDB;
import fm.db.PlayerDB;
import fm.db.TeamDB;
import fm.forms.CreateLeagueForm;
import fm.tools.ControllerHelper;
import fm.tools.MatchGenerator;
import fm.tools.Supplier;

@Controller
public class LeagueController {

	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	private TeamDB teamDB = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	private LeagueDB leagueDB = (LeagueDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("LeagueDB"));
	private MatchDB matchDB = (MatchDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("MatchDB"));
	@Autowired
	MatchGenerator matchGenerator;
	@Autowired
	Supplier supplier;

	@GetMapping("/league")
	public ModelAndView league(CreateLeagueForm createLeagueForm, @CookieValue(value = "fm-login") String login) {
		ArrayList<League> leagueList = (ArrayList<League>) leagueDB.list();

		leagueList.sort(new Comparator<League>() {

			@Override
			public int compare(League l1, League l2) {
				return l1.getName().compareTo(l2.getName());
			}
		});

		ModelAndView model = ControllerHelper.prepareModelAndView("league", login);
		model.addObject("leagueList", leagueList);
		model.addObject("firstLoad", true);
		return model;
	}

	@PostMapping("/league")
	public ModelAndView createLeague(@Valid CreateLeagueForm createLeagueForm, BindingResult bindingResult,
			@CookieValue(value = "fm-login") String login) {

		ArrayList<League> leagueList = (ArrayList<League>) leagueDB.list();

		leagueList.sort(new Comparator<League>() {

			@Override
			public int compare(League l1, League l2) {
				return l1.getName().compareTo(l2.getName());
			}
		});

		if (bindingResult.hasErrors()) {
			ModelAndView model = ControllerHelper.prepareModelAndView("league", login);
			model.addObject("leagueList", leagueList);
			return model;
		}

		if (createLeagueForm.getRequiredNumberOfTeams() % 2 != 0) {
			ModelAndView model = ControllerHelper.prepareModelAndView("league", login);
			model.addObject("leagueList", leagueList);
			model.addObject("errorEvenNumber", true);
			FootballManagerApplication.logger.error("Liga musi mieć parzystą liczbę drużyn:");
			return model;
		}

		if (!leagueDB.checkUniqueness(createLeagueForm.getName())) {
			ModelAndView model = ControllerHelper.prepareModelAndView("league", login);
			model.addObject("leagueList", leagueList);
			model.addObject("errorUnique", true);
			FootballManagerApplication.logger.error("Liga o podanej nazwie istnieje: " + createLeagueForm.getName());
			return model;

		}

		if (!leagueDB.checkUniqueness(createLeagueForm.getName())) {
			ModelAndView model = ControllerHelper.prepareModelAndView("league", login);
			leagueList.sort(new Comparator<League>() {

				@Override
				public int compare(League l1, League l2) {
					return l1.getName().compareTo(l2.getName());
				}
			});
			model.addObject("leagueList", leagueList);
			model.addObject("errorUnique", true);
			FootballManagerApplication.logger.error("Liga o podanej nazwie istnieje: " + createLeagueForm.getName());
			return model;

		}

		League league = leagueDB.create(createLeagueForm.getName(), createLeagueForm.getRequiredPrestige(),
				createLeagueForm.getRequiredNumberOfTeams());
		FootballManagerApplication.logger.info("Utworzono lige: " + league);
		leagueList = (ArrayList<League>) leagueDB.list();
		leagueList.sort(new Comparator<League>() {

			@Override
			public int compare(League l1, League l2) {
				return l1.getName().compareTo(l2.getName());
			}
		});
		ModelAndView model = ControllerHelper.prepareModelAndView("league", login);
		model.addObject("leagueList", leagueList);
		model.addObject("success", true);
		return model;
	}

	@GetMapping("/joinLeague")
	public ModelAndView joinLeague(@CookieValue(value = "fm-login") String login, @RequestParam("idTeam") int idTeam) {
		Player player = pdb.findByLogin(login);
		Team team = teamDB.findById(idTeam);

		ArrayList<League> leagueList = (ArrayList<League>) leagueDB.listAvailable(team.getPrestige());
		ArrayList<League> listLeaguesForTeam = (ArrayList<League>) leagueDB.listLeaguesForTeam(idTeam);
		leagueList.sort(new Comparator<League>() {

			@Override
			public int compare(League l1, League l2) {
				return l1.getName().compareTo(l2.getName());
			}
		});
		ModelAndView model = ControllerHelper.prepareModelAndView("joinLeague", login);
		model.addObject("login", player.getLogin());
		model.addObject("idTeam", idTeam);
		model.addObject("leagueList",
				leagueList.stream().filter(x -> !listLeaguesForTeam.contains(x)).collect(Collectors.toList()));
		return model;
	}

	@PostMapping("/joinLeague")
	public ModelAndView joinLeaguePost(@CookieValue(value = "fm-login") String login,
			@RequestParam("idTeam") int idTeam, @RequestParam("idLeague") int idLeague) {

		League league = leagueDB.findById(idLeague);
		Map<Integer, List<Match>> queueMap;
		List<Team> teamList = teamDB.getListOfTeamsFromLeague(idLeague);
		Player p = pdb.findByLogin(login);
		
		ModelAndView model = ControllerHelper.prepareModelAndView("joinLeague", login);
		model.addObject("idTeam", idTeam);
		
		boolean containsPlayer = false;
		for(Team team : teamList) {
			if(team.getIdPlayer() == p.getIdPlayer()) {
				containsPlayer = true;
				break;
			}
		}
		
		if(containsPlayer) {
			model.addObject("errorContainsPlayer", true);
			return model;
		} else if (league.getActualNumberOfTeams() < league.getRequiredNumberOfTeams()) {
			leagueDB.joinTeamToLeague(idTeam, idLeague);
			leagueDB.updateActualNumberOfTeams(idLeague);
			model.addObject("join", true);

			if (league.getActualNumberOfTeams() + 1 == league.getRequiredNumberOfTeams()) {
				LocalDate dateMatch = LocalDate.now();
				LocalTime timeMatch = LocalTime.parse("20:00", DateTimeFormatter.ofPattern("HH:mm"));

				queueMap = matchGenerator.generateQueues(teamList);
				for (Integer key : queueMap.keySet()) {
					List<Match> matchList = queueMap.get(key);

					for (Match match : matchList) {
						matchDB.create(league.getIdLeague(), match.getQueueNumber(), match.getIdTeam1(),
								match.getIdTeam2(),
								supplier.getTicketPrize(league.getRequiredPrestige(),
										teamDB.findById(match.getIdTeam1()).getPrestige(),
										teamDB.findById(match.getIdTeam2()).getPrestige()),
								LocalDateTime.of(dateMatch.plusDays(key), timeMatch));
					}
				}
				
				supplier.reschedule();
			}
			return model;
		} else {
			model.addObject("errorFullLeague", true);
			return model;
		}
	}

	@RequestMapping("/leagueDetails")
	public ModelAndView leagueDetails(@CookieValue(value = "fm-login") String login,
			@RequestParam(name = "idTeam", defaultValue = "0") long idTeam, @RequestParam("idLeague") long idLeague,
			@RequestParam(name = "admin", defaultValue = "false") boolean adminPage) {

		List<Team> teamList = (ArrayList<Team>) teamDB.getListOfTeamsFromLeague(idLeague);
		Map<Integer, List<Match>> matchesList = new HashMap<>();
		List<Integer> queueList = (ArrayList<Integer>) matchDB.getListOfQueuesFromLeague(idLeague);
		League league = leagueDB.findById(idLeague);

		for (Integer i : queueList) {
			matchesList.put(i, (ArrayList<Match>) matchDB.getListOfQueueMatchesFromLeague(i, idLeague));
		}

		ModelAndView model = ControllerHelper.prepareModelAndView("leagueDetails", login);
		model.addObject("idTeam", idTeam);
		model.addObject("league", league);
		model.addObject("teamList", teamList);
		model.addObject("matchesList", matchesList);
		model.addObject("adminPage", adminPage);
		model.addObject("teamDB", teamDB);
		return model;
	}
}
