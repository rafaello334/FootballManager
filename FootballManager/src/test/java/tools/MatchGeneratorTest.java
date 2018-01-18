package tools;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fm.data.Match;
import fm.data.Team;
import fm.tools.MatchGenerator;

public class MatchGeneratorTest {

	MatchGenerator matchGenerator = new MatchGenerator();
	
	@Test
	public void test() {
		List<Team> teams = new ArrayList<>();
		teams.add(new Team(1, 2, "Team 1", 1, 1));
		teams.add(new Team(2, 2, "Team 2", 1, 1));
		teams.add(new Team(3, 2, "Team 3", 1, 1));
		teams.add(new Team(4, 2, "Team 4", 1, 1));
		teams.add(new Team(5, 2, "Team 5", 1, 1));
		teams.add(new Team(6, 2, "Team 6", 1, 1));
			
		Map<Integer, List<Match>> mapa = matchGenerator.generateQueues(teams);

		// Pierwsza kolejka
		assertEquals(((List<Match>) mapa.get(1)).get(0).getIdTeam1(), 1, 0);
		assertEquals(((List<Match>) mapa.get(1)).get(0).getIdTeam2(), 6, 0);
		
		assertEquals(((List<Match>) mapa.get(1)).get(1).getIdTeam1(), 2, 0);
		assertEquals(((List<Match>) mapa.get(1)).get(1).getIdTeam2(), 5, 0);
		
		assertEquals(((List<Match>) mapa.get(1)).get(2).getIdTeam1(), 3, 0);
		assertEquals(((List<Match>) mapa.get(1)).get(2).getIdTeam2(), 4, 0);
		
		// Druga kolejka
		assertEquals(((List<Match>) mapa.get(2)).get(0).getIdTeam1(), 6, 0);
		assertEquals(((List<Match>) mapa.get(2)).get(0).getIdTeam2(), 4, 0);
		
		assertEquals(((List<Match>) mapa.get(2)).get(1).getIdTeam1(), 5, 0);
		assertEquals(((List<Match>) mapa.get(2)).get(1).getIdTeam2(), 3, 0);
		
		assertEquals(((List<Match>) mapa.get(2)).get(2).getIdTeam1(), 1, 0);
		assertEquals(((List<Match>) mapa.get(2)).get(2).getIdTeam2(), 2, 0);
		
		// Trzecia kolejka
		assertEquals(((List<Match>) mapa.get(3)).get(0).getIdTeam1(), 2, 0);
		assertEquals(((List<Match>) mapa.get(3)).get(0).getIdTeam2(), 6, 0);
		
		assertEquals(((List<Match>) mapa.get(3)).get(1).getIdTeam1(), 3, 0);
		assertEquals(((List<Match>) mapa.get(3)).get(1).getIdTeam2(), 1, 0);
		
		assertEquals(((List<Match>) mapa.get(3)).get(2).getIdTeam1(), 4, 0);
		assertEquals(((List<Match>) mapa.get(3)).get(2).getIdTeam2(), 5, 0);
	
		
		// Czwarta kolejka
		assertEquals(((List<Match>) mapa.get(4)).get(0).getIdTeam1(), 6, 0);
		assertEquals(((List<Match>) mapa.get(4)).get(0).getIdTeam2(), 5, 0);
		
		assertEquals(((List<Match>) mapa.get(4)).get(1).getIdTeam1(), 1, 0);
		assertEquals(((List<Match>) mapa.get(4)).get(1).getIdTeam2(), 4, 0);
		
		assertEquals(((List<Match>) mapa.get(4)).get(2).getIdTeam1(), 2, 0);
		assertEquals(((List<Match>) mapa.get(4)).get(2).getIdTeam2(), 3, 0);
		
		
		// Piąta kolejka
		assertEquals(((List<Match>) mapa.get(5)).get(0).getIdTeam1(), 3, 0);
		assertEquals(((List<Match>) mapa.get(5)).get(0).getIdTeam2(), 6, 0);
	
		assertEquals(((List<Match>) mapa.get(5)).get(1).getIdTeam1(), 4, 0);
		assertEquals(((List<Match>) mapa.get(5)).get(1).getIdTeam2(), 2, 0);
		
		assertEquals(((List<Match>) mapa.get(5)).get(2).getIdTeam1(), 5, 0);
		assertEquals(((List<Match>) mapa.get(5)).get(2).getIdTeam2(), 1, 0);
		
		
	}

}
