package su.psarev.kata.SpringBootSecurity.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Укажите адрес электронной почты")
    @Email(message = "Проверьте адрес электронной почты")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotEmpty(message = "Укажите пароль")
    @Pattern(regexp = "^.{8,}$", message = "Пароль должен содержать не менее 8 символов")
    @Pattern(regexp = "^(?=.*[0-9]).*$", message = "Пароль должен содержать хотя бы одну цифру")
    @Pattern(regexp = "^(?=.*[a-z]).*$", message = "Пароль должен содержать хотя бы одну английскую букву в нижнем регистре")
    @Pattern(regexp = "^(?=.*[A-Z]).*$", message = "Пароль должен содержать хотя бы одну английскую букву в верхнем регистре")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Пароль может содержать только английские буквы и цифры")
    @Column(name = "password", nullable = false)
    private String password;

    @NotEmpty(message = "Укажите фамилию")
    @Pattern(regexp = "^[ёЁа-яА-Я -]+$", message = "Фамилия может содержать только русские буквы, дефисы и пробелы")
    @Column(name = "surname", nullable = false)
    private String surname;

    @NotEmpty(message = "Укажите имя")
    @Pattern(regexp = "^[ёЁа-яА-Я -]+$", message = "Имя может содержать только русские буквы, дефисы и пробелы")
    @Column(name = "name", nullable = false)
    private String name;

    @Pattern(regexp = "^[ёЁа-яА-Я -]*$", message = "Отчество может содержать только русские буквы, дефисы и пробелы")
    @Column(name = "patronymic")
    private String patronymic;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Past(message = "Проверьте дату рождения")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = Role.class)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role_id")
    private Collection<Role> authorities;

    public User(String username, String password, String surname, String name, String patronymic, LocalDate birthDate, Collection<Role> authorities) {
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "Пользователь [АйДи = " + id +
               ", Адрес электронной почты = " + username +
               ", Пароль = " + password +
               ", Фамилия = " + surname +
               ", Имя = " + name +
               ", Отчество = " + patronymic +
               ", Дата рождения = " + birthDate +
               ", Роли = " + authorities.toString() + "]";
    }
}