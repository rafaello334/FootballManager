package fm.forms;

import javax.validation.constraints.Min;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;

public class TransferForm 
{
	@Getter
	@Setter
	@NumberFormat
	@Min(1)
	private String cost;
	
	@Getter
	@Setter
	private long idTeam;
	
	@Getter
	@Setter
	private long idNPC;
}
