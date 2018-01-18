package fm.data;

import lombok.Getter;
import lombok.Setter;

public class StadiumTraining {
	@Setter @Getter private long idStadium;
	@Setter @Getter private long idTeam;
	@Setter @Getter private int xpMultiplier; //Mnoznik doswiadczenia treningu
	@Setter @Getter private int cost;
	
	public StadiumTraining(long idStadium, long idTeam, int xpMultiplier, int cost) {
		this.idStadium = idStadium;
		this.idTeam = idTeam;
		this.xpMultiplier = xpMultiplier;
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return String.format("StadiumTraining[idStadium = %d, idTeam = %d, xpMultiplier =%d, cost = %d]", 
				idStadium, idTeam, xpMultiplier, cost);
	}

}
