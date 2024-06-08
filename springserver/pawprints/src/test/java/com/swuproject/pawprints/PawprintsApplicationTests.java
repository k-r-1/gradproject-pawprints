package com.swuproject.pawprints;

import com.swuproject.pawprints.service.SightReportsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PawprintsApplicationTests {

	@Autowired
	private SightReportsService sightReportsService;

	@Test
	void contextLoads() {
		assertThat(sightReportsService).isNotNull();
	}
}
