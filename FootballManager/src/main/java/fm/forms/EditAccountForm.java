package fm.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class EditAccountForm {
	
	@Getter
	@Setter
	@Size(max=30)
	@Pattern(regexp="([a-zA-Z0-9!@#$%^&*=+.]{8,}|^$)")
	private String newPassword;
	
	@Getter
	@Setter
	@Size(max=30)
	@Pattern(regexp="([a-zA-Z0-9!@#$%^&*=+.]{8,}|^$)")
	private String confirmNewPassword;
	
	@Getter
	@Setter
	@Size(max=30)
	//@Pattern(regexp="([a-zA-Z0-9.-]+@[a-zA-Z-]+.[a-zA-Z.]+")
	private String email;
	
	@Getter
	@Setter
	@Size(min=8, max=30)
	@NotNull
	@Pattern(regexp="[a-zA-Z0-9!@#$%^&*=+.]+")
	private String password;

}
