package bot.telegram.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    static User user;

    @BeforeAll
    static void prepareUser() {
        user = new User();
    }

    @Test
    void testConstructorWithParams() {
        User user = new User(User.ROLE.ADMIN, 1000001, User.CONDITION.START);
        assertEquals(User.ROLE.ADMIN, user.getRole());
        assertEquals(1000001, user.getChatId());
        assertEquals(User.CONDITION.START, user.getCondition());
    }
    @Test
    void getId() {
        user.setId(1);
        assertEquals(1, user.getId());
    }

    @Test
    void getRole() {
        user.setRole(User.ROLE.DEFAULT);
        assertEquals(User.ROLE.DEFAULT, user.getRole());
    }

    @Test
    void getChatId() {
        user.setChatId(123L);
        assertEquals(123L, user.getChatId());
    }

    @Test
    void getCondition() {
        user.setCondition(User.CONDITION.START);
        assertEquals(User.CONDITION.START, user.getCondition() );
    }

    @Test
    void setId() {
        user.setId(777);
        assertEquals(777, user.getId());
    }

    @Test
    void setRole() {
        user.setRole(User.ROLE.ADMIN);
        assertEquals(User.ROLE.ADMIN, user.getRole());
    }

    @Test
    void setChatId() {
        user.setChatId(123L);
        assertEquals(123L, user.getChatId());
    }

    @Test
    void setCondition() {
        user.setCondition(User.CONDITION.START);
        assertEquals(User.CONDITION.START, user.getCondition());
    }
}