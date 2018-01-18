package fm.forms;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;

public class SearchPlayerForm {

	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	@NumberFormat
	private String minCost;

	@Getter
	@Setter
	@NumberFormat
	private String maxCost;

	@Getter
	@Setter
	private String idTeam;

}
