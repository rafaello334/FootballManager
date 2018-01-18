package fm.forms;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;

public class CreateLeagueForm {
	
	@Getter
	@Setter
	@NotNull
	@Size(min = 5, max = 50, message="Nazwa ligi musi mieć conamniej 5 znaków, conajwyżej 50.")
	@Pattern(regexp = "[a-zA-Z0-9 ]+", message = "Nazwa może zawierać: małe i duże litery, cyfry i spacje.")
	private String name;
	
	@Getter
	@Setter
	@NumberFormat
	@Min(value = 0, message = "Minimalna wartość prestiżu wynosi 0")
	@Max(value = 1000000, message = "Maksymalna wartość prestiżu wynosi 1 000 000")
	private long requiredPrestige;
	
	@Getter
	@Setter
	@NumberFormat
	@Min(value = 4, message = "Minimalna liczba drużyn musi być większa od 4")
	@Max(value = 64, message = "Maksymalna liczba drużyn musi być mniejsza od 64")
	private long requiredNumberOfTeams;
}
