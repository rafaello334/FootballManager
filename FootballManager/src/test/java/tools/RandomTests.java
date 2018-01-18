package tools;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import fm.tools.Random;

public class RandomTests {

	@Test
	public void testIntsWithSum() {
		List<Integer> test = Random.intsWithSum(10, 500);
		assertEquals(test.size(), 10);
		assertEquals(test.stream().mapToInt(Integer::intValue).sum(), 500);
	}

}
