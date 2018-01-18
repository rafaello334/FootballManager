package fm.data;

import lombok.Getter;
import lombok.Setter;

public class League {
	
	@Setter
	@Getter
	private long idLeague;

	@Setter
	@Getter
	private String name;

	@Setter
	@Getter
	private long requiredPrestige;

	@Setter
	@Getter
	private long requiredNumberOfTeams;

	@Setter
	@Getter
	private long actualNumberOfTeams;

	
	public League(long idLeague, String name, long requiredPrestige, long requiredNumberOfTeams, long actualNumberOfTeams) {
		this.idLeague = idLeague;
		this.name = name;
		this.requiredPrestige = requiredPrestige;
		this.requiredNumberOfTeams = requiredNumberOfTeams;
		this.actualNumberOfTeams = actualNumberOfTeams;
	}

	public String toString() {
		return String.format("League[idLeague = %d, name = '%s', prestige = '%d', prestige = '%d']", idLeague, name, requiredPrestige, requiredNumberOfTeams);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		League other = (League) obj;
		if (idLeague != other.idLeague)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (requiredNumberOfTeams != other.requiredNumberOfTeams)
			return false;
		if (requiredPrestige != other.requiredPrestige)
			return false;
		return true;
	}
	


}