package br.com.fabricyo.protocolos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ProtocolosApplicationTests {

	@Test
	void contextLoads() {
	}

}
