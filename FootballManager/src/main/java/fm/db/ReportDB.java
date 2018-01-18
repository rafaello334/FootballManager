package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.Report;
import fm.db.dao.ReportDAO;

public class ReportDB implements ReportDAO
{
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private class ReportMapper implements RowMapper<Report> {
		public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
			String dateReported = rs.getDate("dateReported").toString() + "T" + rs.getTime("dateReported").toString();
			Report report = new Report(rs.getInt("idReport"), rs.getInt("idPlayerSource"), rs.getInt("idPlayerTarget"), rs.getString("reason"), LocalDateTime.parse(dateReported));
			return report;
		}
	}
	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);	
		
	}

	@Override
	public Report findById(long id) {
		String sql = "Select * from REPORT where idReport = ?";
		Report rep = jdbcTemplate.queryForObject(sql, new Object[]{id}, new ReportMapper());
		
		return rep;
	}

	@Override
	public void delete(long id) {
		String sql="DELETE from REPORT where idReport = ?";
		jdbcTemplate.update(sql, id);		
		
	}

	@Override
	public void deleteForPlayer(long idPlayerTarget)
	{
		String sql = "DELETE from REPORT where idPlayerTarget = ?";
		jdbcTemplate.update(sql, idPlayerTarget);
	}
	
	@Override
	public List<Report> list() {
		String sql = "Select * from Report";
		List<Report> list = jdbcTemplate.query(sql, new ReportMapper());
		return list;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idReport) from Report";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long)x;
		
		return max + 1;
	}

	@Override
	public void create(long idPlayerSource, long idPlayerTarget, String reason, LocalDateTime dateReported) {
		String sql = "insert into report(idReport, idPlayerSource, idPlayerTarget, reason, dateReported) values(?,?,?,?,to_date(?, 'YYYY-MM-DD HH24:MI'))";
		jdbcTemplate.update(sql, new Object[]{getNewId(), idPlayerSource, idPlayerTarget, reason, dateReported.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))});
		
	}
	
}
