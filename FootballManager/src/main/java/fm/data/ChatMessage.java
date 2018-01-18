package fm.data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ChatMessage {
	@Getter @Setter private long id;
	@Getter @Setter private long chatId;
	@Getter @Setter private Player sender;
	@Getter @Setter private String text;
	@Getter @Setter LocalDateTime date;
	@Getter @Setter private boolean removed;
}
