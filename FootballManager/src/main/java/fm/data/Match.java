package fm.data;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class Match {

	@Getter
	@Setter
	private Long idGame;
	@Getter
	@Setter
	private Long idLeague;

	@Getter
	@Setter
	private Long queueNumber;

	@Getter
	@Setter
	private Long idTeam1;

	@Getter
	@Setter
	private Long idTeam2;

	@Getter
	@Setter
	private String score;

	@Getter
	@Setter
	private LocalDateTime datePlayed;

	@Getter
	@Setter
	private Long ticketPrice;

	public Match(Long idTeam1, Long idTeam2, Long queueNumber) {
		this.idTeam1 = idTeam1;
		this.idTeam2 = idTeam2;
		this.queueNumber = queueNumber;
	}

	public Match(Long idGame, Long idLeague, Long queueNumber, Long idTeam1, Long idTeam2, String score,
			LocalDateTime datePlayed, Long ticketPrice) {
		this.idGame = idGame;
		this.idLeague = idLeague;
		this.queueNumber = queueNumber;
		this.idTeam1 = idTeam1;
		this.idTeam2 = idTeam2;
		this.score = score;
		this.datePlayed = datePlayed;
		this.ticketPrice = ticketPrice;
	}

	@Override
	public String toString() {
		return "Match [idGame=" + idGame + ", idLeague=" + idLeague + ", queueNumber=" + queueNumber + ", idTeam1="
				+ idTeam1 + ", idTeam2=" + idTeam2 + ", score=" + score + ", datePlayed=" + datePlayed
				+ ", ticketPrice=" + ticketPrice + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Match other = (Match) obj;
		if (datePlayed == null) {
			if (other.datePlayed != null)
				return false;
		} else if (!datePlayed.equals(other.datePlayed))
			return false;
		if (idGame == null) {
			if (other.idGame != null)
				return false;
		} else if (!idGame.equals(other.idGame))
			return false;
		if (idLeague == null) {
			if (other.idLeague != null)
				return false;
		} else if (!idLeague.equals(other.idLeague))
			return false;
		if (idTeam1 == null) {
			if (other.idTeam1 != null)
				return false;
		} else if (!idTeam1.equals(other.idTeam1))
			return false;
		if (idTeam2 == null) {
			if (other.idTeam2 != null)
				return false;
		} else if (!idTeam2.equals(other.idTeam2))
			return false;
		if (queueNumber == null) {
			if (other.queueNumber != null)
				return false;
		} else if (!queueNumber.equals(other.queueNumber))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		if (ticketPrice == null) {
			if (other.ticketPrice != null)
				return false;
		} else if (!ticketPrice.equals(other.ticketPrice))
			return false;
		return true;
	}

}
