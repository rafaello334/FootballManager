package fm.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class LoginForm {
	@Getter
	@Setter
	@NotNull
	@Size(min=3, max=30)
	@Pattern(regexp="[a-zA-Z0-9]+")
	private String login;
	
	@Getter
	@Setter
	@NotNull
	@Size(max=30)
	@Pattern(regexp="[a-zA-Z0-9!@#$%^&*=+.]+")
	private String password;
}
