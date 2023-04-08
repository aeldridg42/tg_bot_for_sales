package bot.telegram.services;

import bot.telegram.models.User;
import bot.telegram.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(long chatId) {
        return userRepository.findAll().stream()
                .filter(user1 -> user1.getChatId() == chatId)
                .findAny()
                .orElseGet(() -> userRepository.save(new User(User.ROLE.DEFAULT, chatId)));
    }

    public void setAdmin(User user) {
        user.setRole(User.ROLE.ADMIN);
        userRepository.save(user);
    }

}
