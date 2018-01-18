package tools;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import fm.tools.Simulation;

public class SimulationTest {

	private int team1Power, team2Power;
	private double morale;
	private int team1Prestige, team2Prestige;
	
	@Before
	public void before() {
		team1Power = 1500;
		team2Power = 2000;
		morale = 1.1;
		team1Prestige = 50;
		team2Prestige = 150;
	}
	
	@Test
	public void testSimulation() {
		assertEquals(Simulation.matchMorale(team1Prestige, team2Prestige), morale, 0.001);
		System.out.println();
		System.out.println(Simulation.matchStimulation(team1Power, team2Power, Simulation.matchMorale(team1Prestige, team2Prestige)));
	}

}