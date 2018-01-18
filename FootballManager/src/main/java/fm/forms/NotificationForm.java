package fm.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import fm.data.Notification.NotificationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class NotificationForm {
	@Getter
	@Setter
	@NotNull
	@Size(max=500)
	private String text;
	
	@Getter
	@Setter
	@NotNull
	@Pattern(regexp="[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]")
	private String startDate;
	
	@Getter
	@Setter
	@NotNull
	@Pattern(regexp="[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]")
	private String finishDate;
	
	@Getter
	@Setter
	@NotNull
	private NotificationType type;
	
	@Getter
	@Setter
	private String player;
}
