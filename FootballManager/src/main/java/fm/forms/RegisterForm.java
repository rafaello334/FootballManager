package fm.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class RegisterForm {
	@Getter
	@Setter
	@NotNull
	@Size(min=3, max=30)
	@Pattern(regexp="[a-zA-Z0-9]+")
	private String login;

	@Getter
	@Setter
	@NotNull
	@Size(min=8, max=30)
	@Pattern(regexp="[a-zA-Z0-9!@#$%^&*=+.]+")
	private String password;
	
	@Getter
	@Setter
	@NotNull
	@Size(min=8, max=30)
	@Pattern(regexp="[a-zA-Z0-9!@#$%^&*=+.]+")
	private String confirmPassword;
	
	@Getter
	@Setter
	@NotNull
	@Size(min=8, max=30)
	@Pattern(regexp="[a-zA-Z0-9.-]+@[a-zA-Z-]+.[a-zA-Z.]+")
	private String email;
}
