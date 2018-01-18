package fm.tools;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import fm.data.NPCPlayer;
import fm.db.NPCPlayerDB;

public class Training {
	public static int whichStatsUp(NPCPlayerDB npcdb, int influence, int idTeam) {
		
		ArrayList<NPCPlayer> players = (ArrayList<NPCPlayer>) npcdb.getWholeTeam(idTeam);
		
		for(NPCPlayer p : players) {
			int randomMod = 0;
			int[] stats = {p.getStamina(), p.getStrength(), p.getAgility(), p.getSpeed(), p.getEndurance() ,p.getHealth()};
			for(int i = 0; i < 6; i++) {
				randomMod += stats[i];
			}
			
			for(int i = 0; i < 6; i++) {
				Random gen = new Random();
				int j = gen.nextInt(randomMod) + 1;
				if(j < influence){
					stats[i]++;
				}
			}
			npcdb.updateNpcPlayerStats(p.getIdNPC(), stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);
		}
		
		return 0;
	}
	
	public static boolean trainingDateCheck(LocalDateTime lastTraining, LocalDateTime now) {
		LocalDateTime toCheck = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 
													now.getHour(), now.getMinute());
		toCheck = toCheck.minusDays(1);
		if(toCheck.isAfter(lastTraining)) {
			return true;
		} else {
			return false;
		}
	}
}
