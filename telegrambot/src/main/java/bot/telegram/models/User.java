package bot.telegram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private ROLE role;

    private long chatId;
    public User(ROLE role, long chatId) {
        this.role = role;
        this.chatId = chatId;
    }

    public enum ROLE {
        ADMIN, DEFAULT
    }
}

