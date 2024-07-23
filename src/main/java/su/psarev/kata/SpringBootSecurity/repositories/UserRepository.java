package su.psarev.kata.SpringBootSecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import su.psarev.kata.SpringBootSecurity.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "from User u left join fetch u.authorities where u.username = :username")
    Optional<User> findUserByUsername(final String username);

    @Query(value = "from User u left join fetch u.authorities")
    List<User> findAllUsers();

    @Query(value = "select u.password from User u where u.id = :id")
    Optional<String> findUserPasswordById(Long id);
}