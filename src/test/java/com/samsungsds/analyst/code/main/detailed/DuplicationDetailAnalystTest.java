package com.samsungsds.analyst.code.main.detailed;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.samsungsds.analyst.code.sonar.DuplicationResult;

public class DuplicationDetailAnalystTest {
	private DuplicationDetailAnalyst detailAnalyst;

	@Before
	public void setUp() {
		detailAnalyst = new DuplicationDetailAnalyst();
	}
	
	@Test
	public void testForListLessThanTop10WithSameSource() {
		// arrange
		DuplicationResult[] list = new DuplicationResult[] {
			new DuplicationResult("Source A", 1, 10, "Source B", 11, 20),
			new DuplicationResult("Source A", 1, 10, "Source C", 1, 10)
		};
		
		// act
		for (DuplicationResult duplication : list) {
			detailAnalyst.add(duplication);
		}
		
		List<Duplication> result = detailAnalyst.getTopList();
		
		// assert
		assertEquals(1, result.size());
		assertEquals(20, result.get(0).getTotalDuplicatedLines());
	}
	
	@Test
	public void testForListLessThanTop10WithoutSameSource() {
		// arrange
		DuplicationResult[] list = new DuplicationResult[] {
				new DuplicationResult("Source A", 1, 10, "Source B", 11, 20),
				new DuplicationResult("Source B", 1, 10, "Source C", 1, 10)
		};
		
		// act
		for (DuplicationResult duplication : list) {
			detailAnalyst.add(duplication);
		}
		
		List<Duplication> result = detailAnalyst.getTopList();
		
		// assert
		assertEquals(2, result.size());
		assertEquals(10, result.get(0).getTotalDuplicatedLines());
		assertEquals(10, result.get(1).getTotalDuplicatedLines());
	}
	
	@Test
	public void testForListMoreThanTop10() {
		// arrange
		DuplicationResult[] list = new DuplicationResult[] {
				new DuplicationResult("Source A", 1, 10, "Source B", 1, 10),
				new DuplicationResult("Source B", 1, 10, "Source C", 1, 20),
				new DuplicationResult("Source C", 1, 10, "Source D", 1, 30),
				new DuplicationResult("Source D", 1, 10, "Source E", 1, 40),
				new DuplicationResult("Source E", 1, 10, "Source F", 1, 50),
				new DuplicationResult("Source F", 1, 10, "Source G", 1, 60),
				new DuplicationResult("Source G", 1, 10, "Source H", 1, 70),
				new DuplicationResult("Source H", 1, 10, "Source I", 1, 80),
				new DuplicationResult("Source I", 1, 10, "Source J", 1, 90),
				new DuplicationResult("Source J", 1, 10, "Source K", 1, 100),
				new DuplicationResult("Source K", 1, 10, "Source L", 1, 110),
		};
		
		// act
		for (DuplicationResult duplication : list) {
			detailAnalyst.add(duplication);
		}
		
		List<Duplication> result = detailAnalyst.getTopList();
		
		// assert
		assertEquals(10, result.size());
		assertEquals(110, result.get(0).getTotalDuplicatedLines());
		assertEquals(100, result.get(1).getTotalDuplicatedLines());
		assertEquals(20, result.get(9).getTotalDuplicatedLines());
	}

}
