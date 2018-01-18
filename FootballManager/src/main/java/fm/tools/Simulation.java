package fm.tools;

import java.util.ArrayList;
import java.util.Random;

import fm.data.NPCPlayer;
import fm.data.NPCPlayer.Position;
import fm.db.NPCPlayerDB;
import fm.db.TeamDB;

public class Simulation {
	public static double matchMorale(int team1Prestige, int team2Prestige) {
		double morale = 0.0;
		int prestigeTeam1 = team1Prestige;
		int prestigeTeam2 = team2Prestige;
		
		if(prestigeTeam1 == prestigeTeam2) morale = 1.0;
		else {
			int stronger, weaker, difference;
			
			if(prestigeTeam1 > prestigeTeam2) {
				stronger = prestigeTeam1;
				weaker = prestigeTeam2;
			} else {
				stronger = prestigeTeam2;
				weaker = prestigeTeam1;
			}
			
			difference = stronger - weaker;
			
			if(difference < 50) morale = 1.0;
			else if(difference >= 50 && difference < 150) morale = 1.1;
			else if(difference >= 150 && difference < 250) morale = 1.2;
			else if(difference >= 250 && difference < 500) morale = 1.5;
			else if(difference >= 500 && difference < 1000) morale = 2.0;
			else if(difference >= 1000) morale = 3.0;	
		}	
		return morale;
	}
	
	public static double matchMorale(TeamDB tdb, long idTeam1, long idTeam2) {
		int prestigeTeam1 = (tdb.findById(idTeam1)).getPrestige();
		int prestigeTeam2 = (tdb.findById(idTeam2)).getPrestige();
		double morale = 0.0;
		
		if(prestigeTeam1 == prestigeTeam2) morale = 1.0;
		else {
			int stronger, weaker, difference;
			
			if(prestigeTeam1 > prestigeTeam2) {
				stronger = prestigeTeam1;
				weaker = prestigeTeam2;
			} else {
				stronger = prestigeTeam2;
				weaker = prestigeTeam1;
			}
			
			difference = stronger - weaker;
			
			if(difference < 50) morale = 1.0;
			else if(difference >= 50 && difference < 150) morale = 1.1;
			else if(difference >= 150 && difference < 250) morale = 1.2;
			else if(difference >= 250 && difference < 500) morale = 1.5;
			else if(difference >= 500 && difference < 1000) morale = 2.0;
			else if(difference >= 1000) morale = 3.0;	
		}
		return morale;
	}
		
	public static int teamPower(NPCPlayerDB npcdb, long idTeam) {
		ArrayList<NPCPlayer> players = (ArrayList<NPCPlayer>) npcdb.listSquad(idTeam);
		int power = 0;

		for(NPCPlayer p : players) {
			int sta, str, agi, spd, end, hp;
			Position pozycja = p.getPosition();
			
			switch(pozycja) {
				case ATTACKING: {
					sta = (int)(p.getStrength() * 1.5);
					str = (int)(p.getStrength() * 1.5);
					agi = (int)(p.getStrength() * 0.5);
					spd = (int)(p.getStrength() * 1.5);
					end = (int)(p.getStrength() * 0.5);
					hp = (int)(p.getStrength() * 0.5);
					power += sta + str + agi + spd + end + hp;
					break;
				}
				case DEFENCE: {
					sta = (int)(p.getStrength() * 0.5);
					str = (int)(p.getStrength() * 1.5);
					agi = (int)(p.getStrength() * 0.5);
					spd = (int)(p.getStrength() * 0.5);
					end = (int)(p.getStrength() * 1.5);
					hp = (int)(p.getStrength() * 1.5);
					power += sta + str + agi + spd + end + hp;
					break;
				}
				case MIDFIELD: {
					sta = (int)(p.getStrength() * 1.5);
					str = (int)(p.getStrength() * 0.5);
					agi = (int)(p.getStrength() * 1.5);
					spd = (int)(p.getStrength() * 0.5);
					end = (int)(p.getStrength() * 1.5);
					hp = (int)(p.getStrength() * 0.5);
					power += sta + str + agi + spd + end + hp;
					break;
				}
				case GOALKEEPER: {
					sta = (int)(p.getStrength() * 0.5);
					str = (int)(p.getStrength() * 0.5);
					agi = (int)(p.getStrength() * 1.5);
					spd = (int)(p.getStrength() * 1.5);
					end = (int)(p.getStrength() * 0.5);
					hp = (int)(p.getStrength() * 1.5);
					power += sta + str + agi + spd + end + hp;
					break;
				}
			}
		}
		
		return power;
	}
	
	public static String matchStimulation(int team1Power, int team2Power, double morale) {
		
		int stronger, weaker, differenceNumber, shotChance, shotStronger, shotWeaker, shotNon, numOfShots, 
			scoreStrongT = 0, scoreWeakT = 0;
		double diffMultiplier;
		String score;

		//Liczenie aktualnej sily druzyn
		if(team1Power == team2Power){
			stronger = team1Power;//not really stronger
			weaker = team2Power;//not really weaker
			differenceNumber = 0;
			diffMultiplier = 1.0;
		} else if(team1Power > team2Power) {
			stronger = (int)(team1Power * morale);
			weaker = team2Power;
			differenceNumber = stronger - weaker;
			stronger += differenceNumber;
			diffMultiplier = (double)stronger / (double)weaker;
		} else {
			stronger = (int)(team2Power * morale);
			weaker = team1Power;
			differenceNumber = stronger - weaker;
			stronger += differenceNumber;
			diffMultiplier = (double)stronger / (double)weaker;
		}
		
		//Liczenie szansy na strzal
		shotWeaker = weaker;
		shotStronger = stronger;
		shotNon = ((stronger + weaker) / 2) - differenceNumber;
		shotChance = shotWeaker + shotStronger + shotNon;
		
		//Radnom ile strzalow
		if(diffMultiplier >= 1.0 && diffMultiplier < 1.5) {//1-3 shots
			Random gen = new Random();
			numOfShots = gen.nextInt(3) + 1;
		} else if(diffMultiplier >= 1.5 && diffMultiplier < 2.0) {//2-5 shots
			Random gen = new Random();
			numOfShots = gen.nextInt(4) + 2;
		} else if(diffMultiplier >= 2.0 && diffMultiplier < 2.5) {//3-8 shots
			Random gen = new Random();
			numOfShots = gen.nextInt(7) + 3;
		} else if(diffMultiplier >= 2.5 && diffMultiplier < 3.0) {//4-11 shots
			Random gen = new Random();
			numOfShots = gen.nextInt(8) + 4;
		} else if(diffMultiplier >= 3.0) {//5-15 shots
			Random gen = new Random();
			numOfShots = gen.nextInt(11) + 5;
		} else {//zeby sie o inicjalizacje nie martwil kompilator
			numOfShots = 0;
		}
		
		//Strzelanie
		for(int i = 1; i <= numOfShots; i++) {
			Random gen = new Random();
			int whoScore = gen.nextInt(shotChance);
			if(whoScore >= 0 && whoScore < shotWeaker) {
				scoreWeakT += 1;
			} else if(whoScore >= shotNon + shotWeaker && whoScore < shotChance) {
				scoreStrongT += 1;
			}
		}
		
		//Zapisanie wyników
		if(team1Power == team2Power){
			score = scoreStrongT + "-" + scoreWeakT;
		} else if(team1Power > team2Power) {
			score = scoreStrongT + "-" + scoreWeakT;
		} else {
			score = scoreWeakT + "-" + scoreStrongT;
		}
		
		return score;
	}
}
