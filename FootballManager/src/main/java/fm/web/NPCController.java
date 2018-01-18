package fm.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.validation.Valid;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.FootballManagerApplication;
import fm.data.NPCPlayer;
import fm.data.NPCTrainer;
import fm.data.Player;
import fm.data.Team;
import fm.db.NPCPlayerDB;
import fm.db.NPCTrainerDB;
import fm.db.PlayerDB;
import fm.db.TeamDB;
import fm.forms.ComparePlayersForm;
import fm.forms.SearchPlayerForm;
import fm.tools.ControllerHelper;

@Controller
public class NPCController {

	private TeamDB tdb = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	private NPCPlayerDB npcPlayerDB = (NPCPlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCPlayerDB"));
	private NPCTrainerDB npcTrainerDB = (NPCTrainerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCTrainerDB"));
	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	
	@GetMapping("/buyNPCPlayer")
	public ModelAndView buyNPCPlayer(SearchPlayerForm searchPlayerForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login, @RequestParam("idNPC") long idNPC,
			@RequestParam("name") String name, @RequestParam("minCost") String minCost,
			@RequestParam("maxCost") String maxCost) {

		Player player = pdb.findByLogin(login);
		NPCPlayer npcPlayer = npcPlayerDB.findById(idNPC);
		Team team = tdb.findById(idTeam);

		ArrayList<NPCPlayer> playersList = (ArrayList<NPCPlayer>) npcPlayerDB.get(name, minCost, maxCost);
		ArrayList<NPCPlayer> playersListToBuy = new ArrayList<NPCPlayer>(playersList);
		Iterator<NPCPlayer> iterator = playersList.iterator();
		Iterator<NPCPlayer> iteratorToBuy = playersListToBuy.iterator();

		while (iterator.hasNext())
		{
			NPCPlayer npc = iterator.next();

			if (npc.getIdTeam() == 0) {
				iterator.remove();
			}

		}

		while (iteratorToBuy.hasNext())
		{
			NPCPlayer npc = iteratorToBuy.next();

			if (npc.getIdTeam() != 0 && !npc.isTransfer()) {
				iteratorToBuy.remove();
			}

		}

		Collections.sort(playersListToBuy);
		Collections.sort(playersList);

		ModelAndView model = ControllerHelper.prepareModelAndView("npcPlayerList", login);

		model.addObject("idTeam", idTeam);
		model.addObject("name", name);
		model.addObject("minCost", minCost);
		model.addObject("maxCost", maxCost);
		
		if (team.getBudget() < npcPlayer.getCost()) {
			model.addObject("playersList", playersList);
			model.addObject("playersListToBuy", playersListToBuy);
			model.addObject("errorBudget", true);
			model.addObject("name", npcPlayer.getName());
			return model;
		}

		if (npcPlayer.getIdTeam() != 0 && !npcPlayer.isTransfer()) {
			model.addObject("playersList", playersList);
			model.addObject("playersListToBuy", playersListToBuy);
			model.addObject("errorAccessible", true);
			model.addObject("name", npcPlayer.getName());
			return model;
		}
		
		if(npcPlayer.getIdTeam() != 0)
		{
			Team tempTeam = tdb.findById(npcPlayer.getIdTeam());
			tdb.update(npcPlayer.getIdTeam(), tempTeam.getBudget() + npcPlayer.getCost());
			if(tempTeam.getIdTeam() == team.getIdTeam()) {
				team.setBudget(tempTeam.getBudget() + npcPlayer.getCost());
			}
		}
		
		tdb.update(team.getIdTeam(), player.getIdPlayer(), team.getName(), team.getPrestige(),
				team.getBudget() - npcPlayer.getCost());
		npcPlayerDB.updateNpcIdTeam(idTeam, npcPlayer.getIdNPC());
		
		playersList = (ArrayList<NPCPlayer>) npcPlayerDB.get(name, minCost, maxCost);
		playersListToBuy = new ArrayList<NPCPlayer>(playersList);
		iterator = playersList.iterator();
		iteratorToBuy = playersListToBuy.iterator();

		while (iterator.hasNext()) {
			NPCPlayer npc = iterator.next();

			if (npc.getIdTeam() == 0 || npc.isTransfer()) {
				iterator.remove();
			}

		}

		while (iteratorToBuy.hasNext()) {
			NPCPlayer npc = iteratorToBuy.next();

			if (npc.getIdTeam() != 0 && !npc.isTransfer()) {
				iteratorToBuy.remove();
			}

		}

		Collections.sort(playersListToBuy);
		Collections.sort(playersList);

		model.addObject("playersList", playersList);
		model.addObject("playersListToBuy", playersListToBuy);
		model.addObject("success", true);


		return model;
	}

	
	
	@GetMapping("/buyNPCTrainer")
	public ModelAndView buyNPCTrainer(SearchPlayerForm searchPlayerForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login, @RequestParam("idNPC") long idNPC,
			@RequestParam("name") String name, @RequestParam("minCost") String minCost,
			@RequestParam("maxCost") String maxCost) {

		Player player = pdb.findByLogin(login);
		NPCTrainer npcTrainer = npcTrainerDB.findById(idNPC);
		Team team = tdb.findById(idTeam);

		ArrayList<NPCTrainer> trainerList = (ArrayList<NPCTrainer>) npcTrainerDB.get(name, minCost, maxCost);
		ArrayList<NPCTrainer> trainerListToBuy = new ArrayList<NPCTrainer>(trainerList);
		Iterator<NPCTrainer> iterator = trainerList.iterator();
		Iterator<NPCTrainer> iteratorToBuy = trainerListToBuy.iterator();

		while (iterator.hasNext()) {
			NPCTrainer npc = iterator.next();

			if (npc.getIdTeam() == 0) {
				iterator.remove();
			}

		}

		while (iteratorToBuy.hasNext()) {
			NPCTrainer npc = iteratorToBuy.next();

			if (npc.getIdTeam() != 0) {
				iteratorToBuy.remove();
			}

		}

		Collections.sort(trainerListToBuy);
		Collections.sort(trainerList);

		ModelAndView model = ControllerHelper.prepareModelAndView("npcTrainerList", login);

		model.addObject("idTeam", idTeam);
		model.addObject("name", name);
		model.addObject("minCost", minCost);
		model.addObject("maxCost", maxCost);
		if (team.getBudget() < npcTrainer.getCost()) {
			model.addObject("npcTrainerList", trainerList);
			model.addObject("trainerToBuyList", trainerListToBuy);
			model.addObject("errorBudget", true);
			model.addObject("name", npcTrainer.getName());
			return model;
		}

		if (npcTrainer.getIdTeam() != 0) {
			model.addObject("npcTrainerList", trainerList);
			model.addObject("trainerToBuyList", trainerListToBuy);
			model.addObject("errorAccessible", true);
			model.addObject("name", npcTrainer.getName());
			return model;
		}

		tdb.update(team.getIdTeam(), player.getIdPlayer(), team.getName(), team.getPrestige(),
				team.getBudget() - npcTrainer.getCost());
		npcTrainerDB.updateNpcIdTeam(idTeam, npcTrainer.getIdNPC());
		
		trainerList = (ArrayList<NPCTrainer>) npcTrainerDB.get(name, minCost, maxCost);
		trainerListToBuy = new ArrayList<NPCTrainer>(trainerList);
		iterator = trainerList.iterator();
		iteratorToBuy = trainerListToBuy.iterator();

		while (iterator.hasNext()) {
			NPCTrainer npc = iterator.next();

			if (npc.getIdTeam() == 0) {
				iterator.remove();
			}

		}

		while (iteratorToBuy.hasNext()) {
			NPCTrainer npc = iteratorToBuy.next();

			if (npc.getIdTeam() != 0) {
				iteratorToBuy.remove();
			}

		}

		Collections.sort(trainerListToBuy);
		Collections.sort(trainerList);

		model.addObject("npcTrainerList", trainerList);
		model.addObject("trainerToBuyList", trainerListToBuy);
		model.addObject("success", true);


		return model;
	}
	
	@GetMapping("/comparePlayers")
	public ModelAndView showForm(ComparePlayersForm comparePlayersForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login) {
		ModelAndView model = ControllerHelper.prepareModelAndView("comparePlayers", login);
		model.addObject("idTeam", idTeam);

		return model;
	}

	@GetMapping("/comparePlayersStats")
	public ModelAndView checkSearchPlayer(@Valid ComparePlayersForm comparePlayersForm,BindingResult bindingResult,
			@RequestParam("idTeam") int idTeam, @CookieValue(value = "fm-login") String login) {
		
		if (bindingResult.hasErrors()) {
			return ControllerHelper.prepareModelAndView("comparePlayers", login);
		}

		NPCPlayer firstPlayer = npcPlayerDB.findByName(comparePlayersForm.getFirstPlayerName());
		NPCPlayer secondPlayer = npcPlayerDB.findByName(comparePlayersForm.getSecondPlayerName());

		if (comparePlayersForm.getFirstPlayerName().equals("")) {
			ModelAndView model = ControllerHelper.prepareModelAndView("comparePlayers", login);
			model.addObject("errorEmptyFirstPlayerName", true);
			model.addObject("idTeam", idTeam);
			return model;
		}
		
		if (comparePlayersForm.getSecondPlayerName().equals("")) {
			ModelAndView model = ControllerHelper.prepareModelAndView("comparePlayers", login);
			model.addObject("errorEmptySecondPlayerName", true);
			model.addObject("idTeam", idTeam);
			return model;
		}
		
		if (firstPlayer == null) {
			ModelAndView model = ControllerHelper.prepareModelAndView("comparePlayers", login);
			model.addObject("errorFirstPlayer", true);
			model.addObject("idTeam", idTeam);
			return model;
		}
		
		if (secondPlayer == null) {
			ModelAndView model = ControllerHelper.prepareModelAndView("comparePlayers", login);
			model.addObject("errorSecondPlayer", true);
			model.addObject("idTeam", idTeam);
			return model;
		}
		
		
		ModelAndView model = ControllerHelper.prepareModelAndView("comparePlayersStats", login);
		model.addObject("firstPlayer", firstPlayer);
		model.addObject("secondPlayer", secondPlayer);
		model.addObject("idTeam", idTeam);

		FootballManagerApplication.logger.info("Pierwszy zawodnik do porównania: " + firstPlayer);
		FootballManagerApplication.logger.info("Pierwszy zawodnik do porównania: " + secondPlayer);

		return model;
	}
	
	@GetMapping(path = "/npcPlayerStats", params = {"idNPC", "idTeam", "name", "minCost", "maxCost"})
	public ModelAndView npcPlayerStatsGet(SearchPlayerForm searchPlayerForm, @CookieValue(value = "fm-login") String login, @RequestParam("idNPC") long idNPC, @RequestParam("idTeam") long idTeam, @RequestParam("name") String name, @RequestParam("minCost") String minCost, @RequestParam("maxCost") String maxCost) 
	{
		NPCPlayer npcPlayer = npcPlayerDB.findById(idNPC);
		
		
		ModelAndView model = ControllerHelper.prepareModelAndView("npcPlayerStats", login);	

		model.addObject("player", npcPlayer);
		model.addObject("idTeam", idTeam);
		model.addObject("name", name);
		model.addObject("minCost", minCost);
		model.addObject("maxCost", maxCost);
		
		
		return model;
	}
	
	@GetMapping(path = "/npcPlayerStats", params={"idNPC", "idTeam"})
	public ModelAndView npcPlayerStatsGet(@CookieValue(value = "fm-login") String login, @RequestParam("idNPC") long idNPC, @RequestParam("idTeam") long idTeam)
	{
		NPCPlayer npcPlayer = npcPlayerDB.findById(idNPC);
		
		ModelAndView model = ControllerHelper.prepareModelAndView("npcPlayerStatsNoForm", login);	

		model.addObject("player", npcPlayer);
		model.addObject("idTeam", idTeam);
		
		return model;
	}
	
	@GetMapping(path = "/npcTrainerStats", params = {"idNPC", "idTeam", "name", "minCost", "maxCost"})
	public ModelAndView npcTrainerStatsGet(SearchPlayerForm searchPlayerForm, @CookieValue(value = "fm-login") String login, @RequestParam("idNPC") long idNPC, @RequestParam("idTeam") long idTeam, @RequestParam("name") String name, @RequestParam("minCost") String minCost, @RequestParam("maxCost") String maxCost) 
	{
		NPCTrainer npcTrainer = npcTrainerDB.findById(idNPC);
		
		ModelAndView model = ControllerHelper.prepareModelAndView("npcTrainerStats", login);
		model.addObject("trainer", npcTrainer);
		model.addObject("idTeam", idTeam);
		model.addObject("name", name);
		model.addObject("minCost", minCost);
		model.addObject("maxCost", maxCost);
		
		
		return model;
	}
	
	@GetMapping(path = "/npcTrainerStats", params={"idNPC", "idTeam"})
	public ModelAndView npcTrainerStatsGet(@CookieValue(value = "fm-login") String login, @RequestParam("idNPC") long idNPC, @RequestParam("idTeam") long idTeam)
	{
		NPCTrainer npcTrainer = npcTrainerDB.findById(idNPC);
		
		ModelAndView model = ControllerHelper.prepareModelAndView("npcTrainerStatsNoForm", login);
		model.addObject("trainer", npcTrainer);
		model.addObject("idTeam", idTeam);
		
		return model;
	}
	
	@GetMapping("/searchPlayer")
	public ModelAndView searchPlayer(SearchPlayerForm searchPlayerForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login) {

		ModelAndView model = ControllerHelper.prepareModelAndView("searchPlayer", login);
		model.addObject("idTeam", idTeam);

		return model;
	}

	@GetMapping("/npcPlayerList")
	public ModelAndView checkSearchPlayer(@Valid SearchPlayerForm searchPlayerForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login) {

		ArrayList<NPCPlayer> playersList = (ArrayList<NPCPlayer>) npcPlayerDB.get(searchPlayerForm.getName(),
				searchPlayerForm.getMinCost(), searchPlayerForm.getMaxCost());
		ArrayList<NPCPlayer> playersListToBuy = new ArrayList<NPCPlayer>(playersList);
		Iterator<NPCPlayer> iterator = playersList.iterator();
		Iterator<NPCPlayer> iteratorToBuy = playersListToBuy.iterator();

		while (iterator.hasNext()) {
			NPCPlayer npcPlayer = iterator.next();

			if (npcPlayer.isTransfer()) {
				iterator.remove();
			}

		}
		
		while (iteratorToBuy.hasNext()) {
			NPCPlayer npcPlayer = iteratorToBuy.next();

			if (!npcPlayer.isTransfer()) {
				iteratorToBuy.remove();
			}

		}

		Collections.sort(playersListToBuy);
		Collections.sort(playersList);

		ModelAndView model = ControllerHelper.prepareModelAndView("npcPlayerList", login);
		model.addObject("playersList", playersList);
		model.addObject("playersListToBuy", playersListToBuy);
		model.addObject("idTeam", idTeam);

		FootballManagerApplication.logger.info("Lista znalezionych zawodników: " + playersList);

		return model;
	}

	
	@GetMapping("/searchTrainer")
	public ModelAndView searchTrainer(SearchPlayerForm searchPlayerForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login)
	{
		ModelAndView model = ControllerHelper.prepareModelAndView("searchTrainer", login);
		model.addObject("idTeam", idTeam);
		
		return model;
	}
	
	@GetMapping("/npcTrainerList")
	public ModelAndView npcTrainerList(@Valid SearchPlayerForm searchPlayerForm, @RequestParam("idTeam") int idTeam,
			@CookieValue(value = "fm-login") String login)
	{
		ArrayList<NPCTrainer> trainerList = (ArrayList<NPCTrainer>) npcTrainerDB.get(searchPlayerForm.getName(), searchPlayerForm.getMinCost(), searchPlayerForm.getMaxCost());
		ArrayList<NPCTrainer> trainerToBuyList = new ArrayList<NPCTrainer>(trainerList);
		Iterator<NPCTrainer> iterator = trainerList.iterator();
		Iterator<NPCTrainer> iteratorToBuy = trainerToBuyList.iterator();

		
		while (iterator.hasNext()) {
			NPCTrainer npcTrainer = iterator.next();
			
			if(npcTrainer.getIdTeam() == 0)
			{
				iterator.remove();
			}
		}
		
		while (iteratorToBuy.hasNext()) {
			NPCTrainer npcTrainer = iteratorToBuy.next();
			
			if(npcTrainer.getIdTeam() != 0)
				iteratorToBuy.remove();
			
		}
		
		Collections.sort(trainerToBuyList);
		Collections.sort(trainerList);
		
		ModelAndView model = ControllerHelper.prepareModelAndView("npcTrainerList", login);
		model.addObject("trainerList", trainerList);
		model.addObject("trainerToBuyList", trainerToBuyList);
		model.addObject("idTeam", idTeam);

		FootballManagerApplication.logger.info("Lista znalezionych trenerów: " + trainerList);
		
		return model;
	}
	
}
