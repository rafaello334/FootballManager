package fm.data;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class LastTraining {
	
	@Getter @Setter private long idTeam;
	@Getter @Setter private LocalDateTime trainingDate;
	
	public LastTraining(long idTeam, LocalDateTime trainingDate) {
		this.idTeam = idTeam;
		this.trainingDate = trainingDate;
	}
}
