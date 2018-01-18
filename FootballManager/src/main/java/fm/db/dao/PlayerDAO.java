package fm.db.dao;

import java.util.List;

import fm.data.Player;

public interface PlayerDAO extends GenericDAO<Player> {
	public Player create(String login, String password, String email, String type);
	public void update(long id, String login, String password, String email, String type);
	public void update(Player player);
	public Player findByLogin(String login);
	public boolean checkUniqueness(String login, String email);
	public boolean checkUniqueness(String email);
	public Player activate(long id);
	public Player activate(Player player);
	public List<Player> listInactive();
	public Player banned(long id, int b);
	public Player banned(Player player, int b);
	public List<Player> listActive();
	public boolean exists(String login);
	public Player changePermission(String type, long idPlayer);
	
}
