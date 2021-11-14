package com.example.krg;

import com.example.krg.models.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = KrgApplication.class,
        webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIT
{
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final static String METHOD = "http";
    private final static String DMOAIN = "localhost";
    private final static String PORT = "8087";
    private final static String RESOURCE = "/api/user/all";
    private final static String PATH = METHOD + "://" + DMOAIN + ":" + PORT + RESOURCE;


    @Sql(scripts={"classpath:schema.sql"})
    @Test
    public void testAllUsers()
    {
        UserDTO[] userDTOList = this.restTemplate
                .getForObject(PATH, UserDTO[].class);
        for (UserDTO user : userDTOList) {
            if (user.getId() == 1L) {
                assertEquals("Alice", user.getName());
                return;
            }
        }
    }
}