package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.NPC.NPCType;
import fm.data.NPCTrainer;
import fm.data.NPCTrainer.TrainerType;
import fm.db.dao.NPCTrainerDAO;

public class NPCTrainerDB implements NPCTrainerDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private class TrainerMapper implements RowMapper<NPCTrainer>{
		public NPCTrainer mapRow(ResultSet rs, int rowNum) throws SQLException {
			NPCTrainer npc = new NPCTrainer(rs.getInt("idNPC"), rs.getString("name"), rs.getString("type"),
					rs.getInt("cost"), rs.getInt("idTeam"), rs.getInt("influence"), rs.getString("trainerType"));
			return npc;
		}
	}
	
	private class InfluenceMapper implements RowMapper<Integer>{
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			int influence = rs.getInt("influence");
			return influence;
		}
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public NPCTrainer findById(long id) {
		String sql = "select * from NPCTrainer natural join NPC where idNPC = ?";
		NPCTrainer trainer = jdbcTemplate.queryForObject(sql, new Object[] { id }, new TrainerMapper());
		return trainer;
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<NPCTrainer> list() {
		String sql = "select * from NPCTrainer natural join NPC";
		List<NPCTrainer> trainers = jdbcTemplate.query(sql, new TrainerMapper());
		return trainers;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idNPC) from NPC";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long) x;

		return max + 1;
	}

	@Override
	public NPCTrainer create(String name, int cost, int influence, String trainerType) {
		String sql = "insert into NPC (idNPC, name, type, cost) values (?, ?, ?, ?)";
		long id = getNewId();
		jdbcTemplate.update(sql, new Object[] { id, name, NPCType.TRAINER.toString(), cost } );
		sql = "insert into NPCTrainer (idNPC, influence, trainerType) values (?,?,?)";
		jdbcTemplate.update(sql, new Object[] {id, influence, TrainerType.typeForName(trainerType).toString() } );
		
		return null;
	}

	@Override
	public List<NPCTrainer> get(String name, String minCost, String maxCost) {
		List<NPCTrainer> npcTrainerList;
		String sql = "select * from NPC natural join NPCTrainer";
		ArrayList<Object> obs = new ArrayList<>();

		if(!name.concat(minCost).concat(maxCost).isEmpty()) {
			sql += " where";
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
		
		npcTrainerList = jdbcTemplate.query(sql, obs.toArray(), new TrainerMapper());
		
		return npcTrainerList;
	}

	@Override
	public List<NPCTrainer> getForTeam(long idTeam) {
		String sql = "select * from NPCTrainer natural join NPC where idTeam = ?";
		List<NPCTrainer> trainers = jdbcTemplate.query(sql, new Object[] {idTeam}, new TrainerMapper());
		return trainers;
	}

	@Override
	public List<NPCTrainer> listTeamless() {
		String sql = "select * from NPCTrainer natural join NPC where idTeam is null";
		List<NPCTrainer> trainers = jdbcTemplate.query(sql, new TrainerMapper());
		return trainers;
	}

	@Override
	public void updateNpcIdTeam(int idTeam, int idNPC) {
		
		String sql = "update NPC set idTeam = ? where idNPC = ?";
		jdbcTemplate.update(sql, idTeam, idNPC);

	}
	
	@Override
	public int highestInfluence(long idTeam, String trainerType) {
		String sql = "select MAX(influence) AS influence from NPCTrainer natural join NPC where idTeam = ? and trainertype = ?";
		int trainer = jdbcTemplate.queryForObject(sql, new Object[] { idTeam, trainerType }, new InfluenceMapper());
		return trainer;
	}

}
