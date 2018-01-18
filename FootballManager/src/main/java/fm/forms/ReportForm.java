package fm.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class ReportForm 
{
	@Getter
	@Setter
	@NotNull
	@Size(min=10, max=100)
	@Pattern(regexp="[a-zA-Z0-9!@#$%^&*=+.\\s]+")
	private String reason;
	
	@Getter
	@Setter
	@NotNull
	@Pattern(regexp="[a-zA-Z0-9]+")
	private String targetName;
	
}
