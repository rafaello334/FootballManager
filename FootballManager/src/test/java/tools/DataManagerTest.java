package tools;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import fm.FootballManagerApplication;
import fm.tools.DataManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=FootballManagerApplication.class)
public class DataManagerTest {

	@Autowired DataManager manager;
	
	@Test
	public void test() {
		assertNotNull(manager);
	}
	
	@Test
	public void testNames() {
		assertNotNull(manager.getNamesData());
		assertFalse(manager.getNamesData().isEmpty());
		assertEquals(manager.getNamesData().size(), 142106);
		assertEquals(manager.getNamesData().get(0), "John");
	}

}
