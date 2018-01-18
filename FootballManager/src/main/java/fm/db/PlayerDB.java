package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.Player;
import fm.data.Player.PlayerType;
import fm.db.dao.PlayerDAO;

public class PlayerDB implements PlayerDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private class PlayerMapper implements RowMapper<Player> {
		   public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
			      Player player = new Player(rs.getInt("idPlayer"), rs.getString("login"), rs.getString("email"), rs.getString("password"), rs.getString("type"), rs.getBoolean("active"), rs.getBoolean("banned"));
			      
			      return player;
		}
	}
	
	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Player findById(long id) {
		String sql = "select * from Player where idPlayer = ?";
		Player player = jdbcTemplate.queryForObject(sql, new Object[] { id }, new PlayerMapper());
		return player;
	}

	@Override
	public void delete(long id) {
		String sql = "delete from Player where idPlayer = ?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public List<Player> list() {
		String sql = "select * from Player";
		List<Player> players = jdbcTemplate.query(sql, new PlayerMapper());
		return players;
	}

	@Override
	public Player create(String login, String password, String email, String type) {
		String sql = "insert into Player (idPlayer, login, password, email, type, active, banned) values (?, ?, ?, ?, ?, ?, ?)";
		long id = getNewId();
		jdbcTemplate.update(sql, new Object[] { id, login, password, email, PlayerType.typeForName(type).toString(), 0, 0 });
		return new Player(id, login, email, password, type, false, false);
	}

	@Override
	public void update(long id, String login, String password, String email, String type) {
		String sql = "update Player set login = ?, password = ?, email = ?, type = ? where idPlayer = ?";
		jdbcTemplate.update(sql, login, password, email, type, id);
	}
	
	@Override
	public void update(Player player) {
		update(player.getIdPlayer(), player.getLogin(), player.getPassword(), player.getEmail(), player.getType().toString());
	}

	@Override
	public Player findByLogin(String login) {
		String sql = "select * from Player where login = ?";
		if(!jdbcTemplate.queryForRowSet(sql, login).next()) {
			return null;
		}
		Player player = jdbcTemplate.queryForObject(sql, new Object[] { login }, new PlayerMapper());
		return player;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idPlayer) from player";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long)x;
		
		return max + 1;
	}

	@Override
	public boolean checkUniqueness(String login, String email) {
		String sql = "select * from Player where login = ? or email = ?";
		return !jdbcTemplate.queryForRowSet(sql, login, email).next();
	}
	
	@Override
	public boolean checkUniqueness(String email)
	{
		String sql = "select * from Player where email = ?";
		return !jdbcTemplate.queryForRowSet(sql, email).next();
	}
	
	@Override
	public Player activate(long id) {
		String sql = "update Player set active = ? where idPlayer = ?";
		jdbcTemplate.update(sql, 1, id);
		
		return findById(id);
	}
	
	@Override
	public Player activate(Player player) {
		player = activate(player.getIdPlayer());
		return player;
	}

	@Override
	public List<Player> listInactive() {
		String sql = "select * from Player WHERE active = 0";
		List<Player> players = jdbcTemplate.query(sql, new PlayerMapper());
		return players;
	}
	
	@Override
	public Player banned(long id, int b) {
		String sql = "update Player set banned = ? where idPlayer = ?";
		jdbcTemplate.update(sql, b, id);
		
		return findById(id);
	}
	
	@Override
	public Player banned(Player player, int b) {
		player = banned(player.getIdPlayer(), b);
		return player;
	}
	
	@Override
	public List<Player> listActive() {
		String sql = "select * from Player WHERE active = 1 AND idPlayer > 1";
		List<Player> players = jdbcTemplate.query(sql, new PlayerMapper());
		return players;
	}
	
	@Override
	public boolean exists(String login) {
		return findByLogin(login) != null;
	}
	
	@Override
	public Player changePermission(String type, long idPlayer) {
		String sql = "update Player set type = ? where idPlayer = ?";
		jdbcTemplate.update(sql, type, idPlayer);
		
		return findById(idPlayer);
	}

	
}
