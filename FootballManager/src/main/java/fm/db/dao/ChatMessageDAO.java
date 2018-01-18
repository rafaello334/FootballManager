package fm.db.dao;

import java.util.List;

import fm.data.ChatMessage;
import fm.data.Player;

public interface ChatMessageDAO extends GenericDAO<ChatMessage> {
	public ChatMessage create(long chatId, Player sender, String text);
	public void setRemoved(long id, boolean isRemoved);
	public List<ChatMessage> listMessagesForChat(long chatId);
}
