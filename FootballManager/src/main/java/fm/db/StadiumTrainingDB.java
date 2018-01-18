package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.StadiumTraining;
import fm.db.dao.StadiumTrainingDAO;

public class StadiumTrainingDB implements StadiumTrainingDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private class StadiumMapper implements RowMapper<StadiumTraining> {
		
		public StadiumTraining mapRow(ResultSet rs, int rowNum) throws SQLException {
			StadiumTraining stadium = new StadiumTraining(rs.getInt("idStadium"), rs.getInt("idTeam"), rs.getInt("xpMultiplier"), rs.getInt("cost"));
			return stadium;
		}
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);	
	}

	@Override
	public StadiumTraining findById(long id) {
		String sql = "select * from StadiumTraining where idTeam = ?";
		StadiumTraining stadium = jdbcTemplate.queryForObject(sql, new Object[] { id }, new StadiumMapper());
		return stadium;
	}

	@Override
	public void delete(long id) {
		String sql = "delete from StadiumTraining where idStadium = ?";
		jdbcTemplate.update(sql, id);
		
	}

	@Override
	public List<StadiumTraining> list() {
		String sql = "select * from StadiumTraining";
		List<StadiumTraining> stadium = jdbcTemplate.query(sql, new StadiumMapper());
		return stadium;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idStadium) from StadiumTraining";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long)x;
		
		return max + 1;
	}

	@Override
	public StadiumTraining create(long idTeam, int xpMultiplier, int cost) {
		String sql = "insert into StadiumTraining (idStadium, idTeam, xpMultiplier, cost) values (?, ?, ?, ?)";
		long idStadium = getNewId();
		jdbcTemplate.update(sql, new Object[] { idStadium, idTeam, xpMultiplier, cost });
		return new StadiumTraining(idStadium, idTeam, xpMultiplier, cost);
	}

	@Override
	public void update(long idStadium, int xpMultiplier, int cost) {
		String sql = "update StadiumTraining set xpMultiplier = ?, cost = ? where idStadium = ?";
		jdbcTemplate.update(sql, xpMultiplier, cost, idStadium);	
	}

	@Override
	public void update(StadiumTraining stadium) {
		update(stadium.getIdStadium(), stadium.getXpMultiplier(), stadium.getCost());
		
	}
}
