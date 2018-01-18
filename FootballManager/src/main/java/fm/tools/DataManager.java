package fm.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import fm.FootballManagerApplication;
import lombok.Getter;

@Component("DataManager")
public class DataManager {
	
	private static final String dataPath = System.getProperty("user.dir") + "\\src\\main\\resources\\data\\";
	
	@Getter private List<String> namesData;
	
	@PostConstruct
	public void init() {
		initNames();
	}
	
	private void initNames() {
		try(Stream<String> stream = Files.lines(Paths.get(dataPath + "names.csv"))) {
			namesData = stream.collect(Collectors.toList());
		} catch(IOException e) {
			FootballManagerApplication.logger.error("Błąd przy otwieraniu pliku danych names.csv", e);
		}
	}
}
