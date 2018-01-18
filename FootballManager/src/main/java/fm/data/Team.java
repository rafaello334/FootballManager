package fm.data;

import lombok.Getter;
import lombok.Setter;

public class Team {

	@Getter @Setter private long idTeam;
	@Getter @Setter private long idPlayer;
	@Getter @Setter private String name;
	@Getter @Setter private int prestige;
	@Getter @Setter private int budget;
	
	

	public Team(long idTeam, long idPlayer, String name, int prestige, int budget) {
		this.idTeam = idTeam;
		this.idPlayer = idPlayer;
		this.name = name;
		this.prestige = prestige;
		this.budget = budget;
	}

	@Override
	public String toString() {
		return String.format("Team[idTeam=%d, idPlayer=%d, name='%s', prestige=%d, budget=%d]", idTeam, idPlayer, name, prestige, budget);
	}
	
	
}
