package fm.web;

import java.util.ArrayList;
import java.util.Comparator;

import javax.validation.Valid;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.FootballManagerApplication;
import fm.data.League;
import fm.data.NPCPlayer;
import fm.data.NPCTrainer;
import fm.data.Player;
import fm.data.StadiumTraining;
import fm.data.Team;
import fm.db.LeagueDB;
import fm.db.NPCPlayerDB;
import fm.db.NPCTrainerDB;
import fm.db.PlayerDB;
import fm.db.StadiumTrainingDB;
import fm.db.TeamDB;
import fm.forms.CreateTeamForm;
import fm.forms.TransferForm;
import fm.tools.ControllerHelper;

@Controller
public class TeamController {

	private TeamDB tdb = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	private StadiumTrainingDB sdb = (StadiumTrainingDB) (new ClassPathXmlApplicationContext("beans.xml")
			.getBean("StadiumTrainingDB"));
	private NPCPlayerDB npcdb = (NPCPlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCPlayerDB"));
	private NPCTrainerDB npctdp = (NPCTrainerDB) (new ClassPathXmlApplicationContext("beans.xml")
			.getBean("NPCTrainerDB"));
	private LeagueDB leagueDB = (LeagueDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("LeagueDB"));

	@GetMapping("/createTeam")
	public ModelAndView index(CreateTeamForm createTeamForm, @CookieValue(value = "fm-login") String login) {
		Player player = pdb.findByLogin(login);

		ModelAndView model = ControllerHelper.prepareModelAndView("createTeam", login);
		model.addObject("login", player.getLogin());
		return model;
	}

	@PostMapping("/createTeam")
	public ModelAndView createTeam(@Valid CreateTeamForm createTeamForm, BindingResult bindingResult,
			@CookieValue(value = "fm-login") String login) {
		Player player = pdb.findByLogin(login);
		ModelAndView model = ControllerHelper.prepareModelAndView("createTeam", login);

		if (bindingResult.hasErrors()) {
			return model;
		}

		if (tdb.checkUniqueness(createTeamForm.getName())) {
			Team team = tdb.create(player.getIdPlayer(), createTeamForm.getName(), 0, 100000);
			StadiumTraining stadium = sdb.create(team.getIdTeam(), 2, 5000);
			tdb.createTraining(team.getIdTeam());

			FootballManagerApplication.logger.info("Utworzono drużynę: " + team);
			FootballManagerApplication.logger.info("Utworzono stadion treningowy: " + stadium);
			model.addObject("name", team.getName());
			model.addObject("success", true);
			return model;
		} else {
			model.addObject("errorUnique", true);
			FootballManagerApplication.logger.error("Drużyna o nazwie " + createTeamForm.getName() + " już istnieje!");
			return model;
		}
	}

	@GetMapping("/manageTeam")
	public ModelAndView manageTeamGet(@CookieValue(value = "fm-login") String login) {
		Player player = pdb.findByLogin(login);
		ArrayList<Team> playerTeams = (ArrayList<Team>) tdb.findTeamsByPlayerId(player.getIdPlayer());

		ModelAndView model = ControllerHelper.prepareModelAndView("manageTeam", login);
		model.addObject("playerTeams", playerTeams);
		FootballManagerApplication.logger.info("Lista drużyn gracza o loginie: " + login + "  Lista: " + playerTeams);
		return model;
	}

	@GetMapping("/teamDetails")
	public ModelAndView teamDetailsGet(@RequestParam("idTeam") int idTeam, @Valid TransferForm transferForm,
			@CookieValue(value = "fm-login") String login) {
		ArrayList<NPCPlayer> playersInSquad = (ArrayList<NPCPlayer>) npcdb.listSquad(idTeam);
		ArrayList<NPCPlayer> players = (ArrayList<NPCPlayer>) npcdb.getForTeam(idTeam);
		ArrayList<NPCTrainer> trainers = (ArrayList<NPCTrainer>) npctdp.getForTeam(idTeam);
		ArrayList<League> leagueList = (ArrayList<League>) leagueDB.listLeaguesForTeam(idTeam);
		Team team = (Team) tdb.findById(idTeam);

		players.sort(new Comparator<NPCPlayer>() {

			@Override
			public int compare(NPCPlayer o1, NPCPlayer o2) {
				int i = o1.getPosition().compareTo(o2.getPosition());
				if (i != 0) {
					return i;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

		playersInSquad.sort(new Comparator<NPCPlayer>() {

			@Override
			public int compare(NPCPlayer o1, NPCPlayer o2) {
				int i = o1.getPosition().compareTo(o2.getPosition());
				if (i != 0) {
					return i;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

		ModelAndView model = ControllerHelper.prepareModelAndView("teamDetails", login);
		model.addObject("team", team);
		model.addObject("playersInSquad", playersInSquad);
		model.addObject("leagueList", leagueList);
		model.addObject("trainers", trainers);
		model.addObject("players", players);
		model.addObject("idTeam", idTeam);

		return model;
	}

	@GetMapping("/addToSquad")
	public ModelAndView addToSquad(@RequestParam("idNPC") int idNPC, @RequestParam("idTeam") int idTeam, @Valid TransferForm transferForm,
			@CookieValue(value = "fm-login") String login) {

		NPCPlayer player = npcdb.findById(idNPC);
		Team team = tdb.findById(idTeam);
		ModelAndView model = ControllerHelper.prepareModelAndView("teamDetails", login);
		if (!npcdb.squadFull(idTeam)) {
			npcdb.squadInOut(idNPC, 1);
			FootballManagerApplication.logger.info("Zawodnik " + player + " dołączył do składu drużyny " + team);
			model.addObject("successAdd", true);
		} else {
			model.addObject("errorSquadFull", true);
		}

		ArrayList<NPCPlayer> playersInSquad = (ArrayList<NPCPlayer>) npcdb.listSquad(idTeam);
		ArrayList<NPCPlayer> players = (ArrayList<NPCPlayer>) npcdb.getForTeam(idTeam);
		ArrayList<NPCTrainer> trainers = (ArrayList<NPCTrainer>) npctdp.getForTeam(idTeam);
		ArrayList<League> leagueList = (ArrayList<League>) leagueDB.listLeaguesForTeam(idTeam);

		players.sort(new Comparator<NPCPlayer>() {

			@Override
			public int compare(NPCPlayer o1, NPCPlayer o2) {
				int i = o1.getPosition().compareTo(o2.getPosition());
				if (i != 0) {
					return i;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

		playersInSquad.sort(new Comparator<NPCPlayer>() {

			@Override
			public int compare(NPCPlayer o1, NPCPlayer o2) {
				int i = o1.getPosition().compareTo(o2.getPosition());
				if (i != 0) {
					return i;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

		model.addObject("team", team);
		model.addObject("playersInSquad", playersInSquad);
		model.addObject("leagueList", leagueList);
		model.addObject("trainers", trainers);
		model.addObject("players", players);
		model.addObject("idTeam", idTeam);
		return model;
	}

	@GetMapping("/deleteFromSquad")
	public ModelAndView deleteFromSquad(@RequestParam("idNPC") int idNPC, @RequestParam("idTeam") int idTeam, @Valid TransferForm transferForm,
			@CookieValue(value = "fm-login") String login) {

		npcdb.squadInOut(idNPC, 0);
		NPCPlayer player = npcdb.findById(idNPC);
		Team team = tdb.findById(idTeam);
		FootballManagerApplication.logger.info("Zawodnik " + player + " opuścił skład drużyny " + team);
		ArrayList<NPCPlayer> playersInSquad = (ArrayList<NPCPlayer>) npcdb.listSquad(idTeam);
		ArrayList<NPCPlayer> players = (ArrayList<NPCPlayer>) npcdb.getForTeam(idTeam);
		ArrayList<NPCTrainer> trainers = (ArrayList<NPCTrainer>) npctdp.getForTeam(idTeam);
		ArrayList<League> leagueList = (ArrayList<League>) leagueDB.listLeaguesForTeam(idTeam);

		players.sort(new Comparator<NPCPlayer>() {

			@Override
			public int compare(NPCPlayer o1, NPCPlayer o2) {
				int i = o1.getPosition().compareTo(o2.getPosition());
				if (i != 0) {
					return i;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

		playersInSquad.sort(new Comparator<NPCPlayer>() {

			@Override
			public int compare(NPCPlayer o1, NPCPlayer o2) {
				int i = o1.getPosition().compareTo(o2.getPosition());
				if (i != 0) {
					return i;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});

		ModelAndView model = ControllerHelper.prepareModelAndView("teamDetails", login);
		model.addObject("successDelete", true);
		model.addObject("team", team);
		model.addObject("playersInSquad", playersInSquad);
		model.addObject("leagueList", leagueList);
		model.addObject("trainers", trainers);
		model.addObject("players", players);
		model.addObject("idTeam", idTeam);
		return model;

	}
	
	@PostMapping("/npcSell")
	public String npcSell(@CookieValue(value = "fm-login") String login, @Valid TransferForm transferForm, BindingResult bindingResult)
	{
		if(bindingResult.hasErrors()) 
		{
			return "redirect:/teamDetails?idTeam=" + transferForm.getIdTeam();
		}	
		npcdb.sellNPC(transferForm.getIdNPC(), Integer.parseInt(transferForm.getCost()));
						
		return "redirect:/teamDetails?idTeam=" + transferForm.getIdTeam();
			
	}



}
