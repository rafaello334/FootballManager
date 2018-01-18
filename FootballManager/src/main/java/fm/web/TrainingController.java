package fm.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.FootballManagerApplication;
import fm.data.LastTraining;
import fm.data.StadiumTraining;
import fm.data.Team;
import fm.db.NPCPlayerDB;
import fm.db.NPCTrainerDB;
import fm.db.StadiumTrainingDB;
import fm.db.TeamDB;
import fm.tools.ControllerHelper;
import fm.tools.Training;

@Controller
public class TrainingController {

	private TeamDB tdb = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	private StadiumTrainingDB sdb = (StadiumTrainingDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("StadiumTrainingDB"));
	private NPCTrainerDB npctdb = (NPCTrainerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCTrainerDB"));
	private NPCPlayerDB npcdb = (NPCPlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCPlayerDB"));
	
	@GetMapping("/training")
	public ModelAndView trainingGet(@RequestParam("idTeam") int idTeam, @CookieValue(value = "fm-login") String login) {
		
		int cInf, mInf, pInf, nInf, rInf,stadiumInf;
		StadiumTraining stadium = sdb.findById(idTeam);
		LastTraining lt = tdb.trainingDate(idTeam);
		
		cInf = npctdb.highestInfluence(idTeam, "c");
		mInf = npctdb.highestInfluence(idTeam, "m");
		pInf = npctdb.highestInfluence(idTeam, "p");
		nInf = npctdb.highestInfluence(idTeam, "n");
		rInf = npctdb.highestInfluence(idTeam, "r");
		
		stadiumInf = stadium.getXpMultiplier();
		LocalDateTime dateLastTraining = lt.getTrainingDate();
		int wholeInfluence = cInf + mInf + pInf + nInf + rInf + stadiumInf;
		
		ModelAndView model = ControllerHelper.prepareModelAndView("training", login);
		model.addObject("cInf", cInf);
		model.addObject("mInf", mInf);
		model.addObject("pInf", pInf);
		model.addObject("nInf", nInf);
		model.addObject("rInf", rInf);
		model.addObject("stadiumInf", stadiumInf);
		if(dateLastTraining.isEqual(LocalDateTime.of(1995, 1, 1, 19, 2))){
			model.addObject("dateLastTraining", "Jeszcze się nie odbył");
		} else {
			model.addObject("dateLastTraining",dateLastTraining.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		}
		model.addObject("wholeInfluence", wholeInfluence);
		model.addObject("idTeam", idTeam);
		return model;
	}
	
	@GetMapping("/startTraining")
	public ModelAndView trainingPost(@RequestParam("idTeam") int idTeam, @RequestParam("wholeInfluence") int wholeInfluence, 
										@CookieValue(value = "fm-login") String login) {
		
		int cInf, mInf, pInf, nInf, rInf,stadiumInf;
		StadiumTraining stadium = sdb.findById(idTeam);
		LastTraining lt = tdb.trainingDate(idTeam);
		Team team = tdb.findById(idTeam);
		LocalDateTime now = LocalDateTime.now();
		
		cInf = npctdb.highestInfluence(idTeam, "c");
		mInf = npctdb.highestInfluence(idTeam, "m");
		pInf = npctdb.highestInfluence(idTeam, "p");
		nInf = npctdb.highestInfluence(idTeam, "n");
		rInf = npctdb.highestInfluence(idTeam, "r");
		
		stadiumInf = stadium.getXpMultiplier();
		LocalDateTime dateLastTraining = lt.getTrainingDate();
		ModelAndView model = ControllerHelper.prepareModelAndView("training", login);
		
		if(Training.trainingDateCheck(dateLastTraining, now)) {
			Training.whichStatsUp(npcdb, wholeInfluence, idTeam);
			model.addObject("success", true);
			tdb.updateTraining(idTeam, now);
			model.addObject("dateLastTraining", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		} else {
			model.addObject("errorTooEarly", true);
			FootballManagerApplication.logger.error("Drużyna o nazwie " + team.getName() + " już dzisiaj trenowała!");
			model.addObject("dateLastTraining", dateLastTraining.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		}
		
		model.addObject("cInf", cInf);
		model.addObject("mInf", mInf);
		model.addObject("pInf", pInf);
		model.addObject("nInf", nInf);
		model.addObject("rInf", rInf);
		model.addObject("stadiumInf", stadiumInf);
		model.addObject("wholeInfluence", wholeInfluence);
		model.addObject("idTeam", idTeam);
		return model;
	}
	
}
