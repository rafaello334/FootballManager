package fm.tools;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import fm.data.Match;
import fm.data.NPCPlayer;
import fm.data.NPCTrainer;
import fm.db.MatchDB;
import fm.db.NPCPlayerDB;
import fm.db.NPCTrainerDB;
import fm.db.TeamDB;

@Component("Supplier")
public class Supplier {
	private Timer timer;
	@Autowired private DataManager dataManager;
	private NPCPlayerDB npcpdb = (NPCPlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCPlayerDB"));
	private NPCTrainerDB npctdb = (NPCTrainerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCTrainerDB"));
	private MatchDB mdb = (MatchDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("MatchDB"));
	private TeamDB tbd = (TeamDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("TeamDB"));
	
	private static final long repeatMilliseconds = TimeUnit.MINUTES.toMillis(1);
	private static final int npcPlayerDemand = 20;
	private static final int npcTrainerDemand = 30;
	
	@PostConstruct
	public void schedule() {
		timer = new Timer();
		Supplier sup = this;
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				sup.supply();
			}
		}, 0, repeatMilliseconds);
		
		List<Match> l = mdb.listAllIncoming();
		l.forEach(match -> {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					String score = Simulation.matchStimulation(Simulation.teamPower(npcpdb, match.getIdTeam1()), 
							Simulation.teamPower(npcpdb, match.getIdTeam2()), 
							Simulation.matchMorale(tbd, match.getIdTeam1(), match.getIdTeam2()));

					mdb.updatePlayNow(match.getIdGame(), score);
				}
			}, match.getDatePlayed().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() - System.currentTimeMillis());
		});
	}
	
	public void reschedule() {
		timer.cancel();
		schedule();
	}
	
	public void supply() {
		supplyNPCPlayers();
		supplyNPCTrainer();
	}
	
	public void supplyNPCPlayers() {
		for(int size = npcpdb.listTeamless().size(); size < npcPlayerDemand; size++) {
			List<Integer> stats = Random.intsWithSum(6, 60);
			List<String> types = NPCPlayer.Position.listShort();
			List<String> names = dataManager.getNamesData();
			String name = names.get(Random.rng.nextInt(names.size())) + " " + names.get(Random.rng.nextInt(names.size()));
			npcpdb.create(name, 2000, stats.get(0), stats.get(1), stats.get(2), stats.get(3), stats.get(4), stats.get(5), 0, types.get(Random.rng.nextInt(types.size())), true);
		}
	}
	
	public void supplyNPCTrainer()
	{		
		for(int size = npctdb.listTeamless().size(); size < npcTrainerDemand; size++)
		{
			int infl = Random.rng.nextInt(6);
			infl += 13;
			List<String> types = NPCTrainer.TrainerType.listShort();
			List<String> names = dataManager.getNamesData();
			String name = names.get(Random.rng.nextInt(names.size())) + " " + names.get(Random.rng.nextInt(names.size()));
			int cost = 100 * infl + 500; 
			npctdb.create(name, cost, infl, types.get(Random.rng.nextInt(types.size())));
		}
	}
	
	public int getTicketPrize(int teamOnePrestige, int teamTwoPrestige)
	{
		double multiplier = 0.0;
		double wageOne = 0.0;
		double wageTwo = 0.0;
		
		
		if(teamOnePrestige > 3*teamTwoPrestige || teamTwoPrestige > 3*teamOnePrestige)
			multiplier = 0.55;
		else if(teamOnePrestige > 2*teamTwoPrestige || teamTwoPrestige > 2*teamOnePrestige)
			multiplier = 0.8;
		else if(Math.abs(teamOnePrestige - teamTwoPrestige) < 10)
			multiplier = 1.1;
		else
			multiplier = 1.;
		
		if(teamOnePrestige > teamTwoPrestige)
		{
			wageOne = teamOnePrestige + (teamOnePrestige + teamTwoPrestige)/2.0;
			wageTwo = teamOnePrestige;
		}
		else
		{
			wageOne = teamTwoPrestige + (teamTwoPrestige + teamOnePrestige)/2.0;
			wageTwo = teamTwoPrestige;
		}
		
		double randomFactor = Random.rng.nextDouble()*1.5 - 0.5;
		
		return new Double(multiplier*(wageOne+wageTwo)*(randomFactor)).intValue();
	}
	
	public int getTicketPrize(long ligueRequiredPrestige, long teamOnePrestige, long teamTwoPrestige)
	{
		double multiplier = 0.0;
		double wageOne = 0.0;
		double wageTwo = 0.0;
		
		if (ligueRequiredPrestige == 0)
		{
			multiplier = 2.0;
		}
		else if(ligueRequiredPrestige < 10)
		{
			multiplier = 1.7;
		}
		else if(ligueRequiredPrestige < 50)
		{
			multiplier = 1.5;
		}
		else if(ligueRequiredPrestige < 100)
		{
			multiplier = 1.2;
		}
		else if(ligueRequiredPrestige < 200)
		{
			multiplier = 1;
		}
		else if(ligueRequiredPrestige < 500)
		{
			multiplier = 0.8;
		}
		else if(ligueRequiredPrestige < 900)
		{
			multiplier = 1;
		}
		else if(ligueRequiredPrestige < 1500)
		{
			multiplier = 1.2;
		}
		
		
		if(teamOnePrestige > 3*teamTwoPrestige || teamTwoPrestige > 3*teamOnePrestige)
			wageOne = 0.55;
		else if(teamOnePrestige > 2*teamTwoPrestige || teamTwoPrestige > 2*teamOnePrestige)
			wageOne = 0.7;
		else if(Math.abs(teamOnePrestige - teamTwoPrestige) < 100)
			wageOne = 1.1;
		else if(Math.abs(teamOnePrestige - teamTwoPrestige) < 50)
			wageOne = 1.2;
		else if(Math.abs(teamOnePrestige - teamTwoPrestige) < 10)
			wageOne = 1.4;
		else
			wageOne = 1.;
			
		if(teamOnePrestige > 3*ligueRequiredPrestige || teamTwoPrestige > 3*ligueRequiredPrestige)
			wageTwo = 0.4;
		else if(teamOnePrestige > 2*ligueRequiredPrestige || teamTwoPrestige > 2*ligueRequiredPrestige)
			wageTwo = 0.6;
		else if(Math.abs(teamOnePrestige - ligueRequiredPrestige) < 100 && Math.abs(teamTwoPrestige - ligueRequiredPrestige) < 100)
			wageTwo = 1.1;
		else if(Math.abs(teamOnePrestige - ligueRequiredPrestige) < 50 && Math.abs(teamTwoPrestige - ligueRequiredPrestige) < 50)
			wageTwo = 1.2;
		else if(Math.abs(teamOnePrestige - ligueRequiredPrestige) < 10 && Math.abs(teamTwoPrestige - ligueRequiredPrestige) < 10)
			wageTwo = 1.4;
		else
			wageTwo = 1.;
		
		
		double randomFactor = Random.rng.nextDouble()*1.5 + 0.5;
		return new Double(multiplier*randomFactor*((wageOne+wageTwo)/2.0)*((teamOnePrestige*teamTwoPrestige)/2.0+ligueRequiredPrestige)).intValue();
	}
}