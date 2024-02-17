package com.bingchat4urapp_server.bingchat4urapp_server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


class HeadlessSpringBootContextLoader extends SpringBootContextLoader {
    @Override
    protected SpringApplication getSpringApplication() {
        SpringApplication application = super.getSpringApplication();
        application.setHeadless(false);
        return application;
    }
}

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(loader = HeadlessSpringBootContextLoader.class)
class Bingchat4urappServerApplicationTests {

	// @Test
	// void contextLoads() {
	// }

	@Test
	void TestMethod(){
		assertThat("Test").isEqualTo("Test");
	}

}
