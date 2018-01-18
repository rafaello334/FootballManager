package fm.forms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;

public class PenaltyForm {
	@Getter
	@Setter
	@NotNull
	@Min(1)
	@NumberFormat
	private int amount;
	
	@Getter
	@Setter
	@NotNull
	@Pattern(regexp="[a-zA-Z0-9]+")
	private String targetName;
}
