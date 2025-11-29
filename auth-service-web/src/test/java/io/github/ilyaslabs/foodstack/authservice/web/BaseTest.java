package io.github.ilyaslabs.foodstack.authservice.web;

import io.github.ilyaslabs.microservice.test.common.FixedClockConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@SpringBootTest(classes = {Config.class, FixedClockConfiguration.class})
@ImportTestcontainers(TestContainersConfiguration.class)
@AutoConfigureMockMvc
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Convert object to json string
     *
     * @param obj object
     * @return json string
     */
    protected String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
