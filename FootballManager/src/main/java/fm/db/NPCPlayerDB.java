package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.NPC.NPCType;
import fm.data.NPCPlayer;
import fm.db.dao.NPCPlayerDAO;

public class NPCPlayerDB implements NPCPlayerDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private class PlayerMapper implements RowMapper<NPCPlayer> {
		public NPCPlayer mapRow(ResultSet rs, int rowNum) throws SQLException {
			NPCPlayer npc = new NPCPlayer(rs.getInt("idNPC"), rs.getString("name"), rs.getString("type"),
					rs.getInt("cost"), rs.getInt("idTeam"), rs.getInt("stamina"), rs.getInt("strength"),
					rs.getInt("agility"), rs.getInt("speed"), rs.getInt("endurance"), rs.getInt("health"),
					rs.getInt("fatigue"), rs.getString("position"), rs.getInt("inSquad"), rs.getBoolean("transfer"));
			return npc;
		}
	}
	
	private class SquadFullMapper implements RowMapper<Integer> {
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			int ammount = rs.getInt("count");
			return ammount;
		}
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public NPCPlayer findById(long id) {
		String sql = "select * from NPCPlayer natural join NPC where idNPC = ?";
		NPCPlayer player = jdbcTemplate.queryForObject(sql, new Object[] { id }, new PlayerMapper());
		return player;
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<NPCPlayer> list() {
		String sql = "select * from NPCPlayer natural join NPC";
		List<NPCPlayer> players = jdbcTemplate.query(sql, new PlayerMapper());
		return players;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idNPC) from NPC";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long) x;

		return max + 1;
	}

	@Override
	public NPCPlayer create(String name, int cost, int stamina, int strength, int agility,
			int speed, int endurance, int health, int fatigue, String position, boolean transfer) {
		int value;
		if(transfer)
			value = 1;
		else
			value = 0;
		String sql = "insert into NPC (idNPC, name, type, cost) values (?, ?, ?, ?)";
		long id = getNewId();
		jdbcTemplate.update(sql, new Object[] { id, name, NPCType.PLAYER.toString(), cost });

		sql = "insert into NPCPlayer (idNPC, stamina, strength, agility, speed, endurance, health, fatigue, position, inSquad, transfer) values (?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { id, stamina, strength, agility, speed, endurance, health, fatigue,
				NPCPlayer.Position.typeForName(position).toString(), 0, value });

		return null;
	}
	
	@Override
	public void sellNPC(long idNPC, int cost)
	{
		String sql = "update NPCPlayer set transfer = 1 where idNPC = ?";
		jdbcTemplate.update(sql, idNPC);
		sql = "update NPC set cost = ? where idNPC = ?";
		jdbcTemplate.update(sql, cost, idNPC);
	}

	@Override
	public List<NPCPlayer> get(String name, String minCost, String maxCost) {
		List<NPCPlayer> npcPlayerList;
		String sql;
		sql = "select * from NPC natural join NPCPlayer";
		ArrayList<Object> obs = new ArrayList<>();

		if(!name.concat(minCost).concat(maxCost).isEmpty()) {
			sql += " where NPCPlayer.transfer = 1 AND";
			if (!name.isEmpty()) {
				sql += " NPC.name = ? AND";
				obs.add(name);
			}
			if (!minCost.isEmpty()) {
				sql += " NPC.cost >= ? AND";
				obs.add(minCost);
			}
			if (!maxCost.isEmpty()) {
				sql += " NPC.cost <= ? AND";
				obs.add(maxCost);
			}
			sql = sql.substring(0, sql.length() - 4);
		}

		npcPlayerList = jdbcTemplate.query(sql, obs.toArray(), new PlayerMapper());

		return npcPlayerList;
	}

	@Override
	public List<NPCPlayer> listTeamless() {
		String sql = "select * from NPCPlayer natural join NPC where idTeam is null";
		List<NPCPlayer> players = jdbcTemplate.query(sql, new PlayerMapper());
		return players;
	}

	@Override
	public void updateNpcIdTeam(int idTeam, int idNPC) 
	{
		
		String sql = "update NPC set idTeam = ? where idNPC = ?";
		jdbcTemplate.update(sql, idTeam, idNPC);
		sql = "update NPCPlayer set transfer = 0 where idNPC = ?";
		jdbcTemplate.update(sql, idNPC);		
	}
	

	public List<NPCPlayer> getForTeam(long idTeam) {
		String sql = "select * from NPCPlayer natural join NPC where idTeam = ? AND transfer = 0 AND inSquad = 0";
		List<NPCPlayer> players = jdbcTemplate.query(sql, new Object[] {idTeam}, new PlayerMapper());
		return players;
	}

	@Override
	public NPCPlayer findByName(String name) {
		String sql = "select * from NPCPlayer natural join NPC where name = ?";
		if(!jdbcTemplate.queryForRowSet(sql, name).next()) {
			return null;
		}
		NPCPlayer player = jdbcTemplate.queryForObject(sql, new Object[] {name}, new PlayerMapper());
		return player;
	}
	
	@Override
	public void updateNpcPlayerStats(int idNPC, int stamina, int strength, int agility, int speed, int endurance, int health) {
		
		String sql = "update NPCPlayer set stamina = ?, strength = ?, agility = ?, speed = ?, endurance = ?, health = ? where idNPC = ?";
		jdbcTemplate.update(sql, stamina, strength, agility, speed, endurance, health, idNPC);
	}

	@Override
	public void squadInOut(int idNPC, int squadInOut) {
		String sql = "update NPCPlayer set inSquad = ? where idNPC = ?";
		jdbcTemplate.update(sql, squadInOut, idNPC);
	}
	
	@Override
	public List<NPCPlayer> listSquad(long idTeam) {
		String sql = "select * from NPCPlayer natural join NPC where idTeam = ? and inSquad = 1 and transfer = 0";
		List<NPCPlayer> players = jdbcTemplate.query(sql, new Object[] {idTeam}, new PlayerMapper());
		return players;
	}

	public List<NPCPlayer> getWholeTeam(long idTeam) {
		String sql = "select * from NPCPlayer natural join NPC where idTeam = ?";
		List<NPCPlayer> players = jdbcTemplate.query(sql, new Object[] {idTeam}, new PlayerMapper());
		return players;
	}
	
	public boolean squadFull(long idTeam) {
		boolean isFull;
		String sql = "SELECT COUNT(idNPC) as count FROM npcplayer natural join npc where npc.idTeam = ? and npcplayer.transfer = 0 and npcplayer.inSquad = 1";
		int ammount = jdbcTemplate.queryForObject(sql, new Object[] { idTeam }, new SquadFullMapper());
		if(ammount >= 11) isFull = true;
		else isFull = false;
		return isFull;
	}

}
