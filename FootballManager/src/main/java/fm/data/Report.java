package fm.data;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class Report {
	
	@Getter @Setter private long idReport;
	@Getter @Setter private long idPlayerSource;
	@Getter @Setter private long idPlayerTarget;
	@Getter @Setter private String namePlayerSource;
	@Getter @Setter private String namePlayerTarget;
	@Getter @Setter private String reason;
	@Getter @Setter private LocalDateTime dateReported;
	
	public Report(int idReport, int idPlayerSource, int idPlayerTarget, String reason, LocalDateTime dateReported)
	{
		this.idReport = idReport;
		this.idPlayerSource = idPlayerSource;
		this.idPlayerTarget = idPlayerTarget;
		this.reason = reason;
		this.dateReported = dateReported;
	}

	@Override
	public String toString() {
		return "Report [idReport=" + idReport + ", idPlayerSource=" + idPlayerSource + ", idPlayerTarget="
				+ idPlayerTarget + ", namePlayerSource=" + namePlayerSource + ", namePlayerTarget=" + namePlayerTarget
				+ ", reason=" + reason + ", dateReported=" + dateReported + "]";
	}

	
	

}
