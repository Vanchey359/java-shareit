package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class IntegrationUserTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private User user1, user2;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "Ivan", "Averin@yandex.ru");
        user2 = new User(null, "Mike", "Mike@yandex.ru");
    }

    @Test
    void findAllTest() {
        restTemplate.postForEntity("/users", user1, User.class);
        restTemplate.postForEntity("/users", user2, User.class);

        HttpHeaders headers = new HttpHeaders();
        User[] usersFromController = restTemplate
                .exchange("/users", HttpMethod.GET, new HttpEntity<Object>(headers), User[].class).getBody();

        List<User> usersFromDB = userRepository.findAll();

        assertEquals(usersFromController.length, 2);
        assertEquals(usersFromDB.size(), 2);

        for (int i = 0; i < usersFromDB.size(); i++) {
            System.out.println(usersFromDB.get(i).getName() + usersFromDB.get(i).getEmail());
            assertEquals(usersFromController[i].getId(), usersFromDB.get(i).getId());
            assertEquals(usersFromController[i].getName(), usersFromDB.get(i).getName());
            assertEquals(usersFromController[i].getEmail(), usersFromDB.get(i).getEmail());
        }
    }
}