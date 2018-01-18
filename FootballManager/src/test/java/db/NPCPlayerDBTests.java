package db;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fm.FootballManagerApplication;
import fm.data.NPCPlayer;
import fm.db.NPCPlayerDB;
public class NPCPlayerDBTests {

	private NPCPlayerDB pdb;
	
	@Before
	public void before()
	{
		pdb = (NPCPlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("NPCPlayerDB"));
	}
	
	@Test
	public void test() 
	{
		NPCPlayer npc = pdb.findById(0);
		Assert.assertEquals("Zbyszek", npc.getName());
		FootballManagerApplication.logger.info("Test name was successful");
		Assert.assertEquals(3000, npc.getCost());
		FootballManagerApplication.logger.info("Test cost was successful");
		Assert.assertEquals(1, npc.getIdTeam());
		FootballManagerApplication.logger.info("Test idTead was successful");
		Assert.assertEquals(12, npc.getStamina());
		FootballManagerApplication.logger.info("Test stamina was successful");
		Assert.assertEquals(15, npc.getStrength());
		FootballManagerApplication.logger.info("Test strength was successful");
		Assert.assertEquals(12, npc.getAgility());
		FootballManagerApplication.logger.info("Test agility was successful");
		Assert.assertEquals(16, npc.getSpeed());
		FootballManagerApplication.logger.info("Test speed was successful");
		Assert.assertEquals(12, npc.getEndurance());
		FootballManagerApplication.logger.info("Test endurance was successful");
		Assert.assertEquals(33, npc.getHealth());
		FootballManagerApplication.logger.info("Test health was successful");
		Assert.assertEquals(14, npc.getFatigue());
		FootballManagerApplication.logger.info("Test fatigue was successful");
		Assert.assertEquals("g", npc.getPosition().toString());
		FootballManagerApplication.logger.info("Whole test was successful");
	}

}
