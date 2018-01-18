package fm.db.dao;

import java.time.LocalDateTime;

import fm.data.Report;

public interface ReportDAO extends GenericDAO<Report>{
	
	public void create(long idPlayerSource, long idPlayerTarget, String reason, LocalDateTime dateReported);
	public void deleteForPlayer(long idPlayerTarget);
}
