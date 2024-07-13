package su.psarev.kata.SpringBootSecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import su.psarev.kata.SpringBootSecurity.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "from User u left join fetch u.authorities where u.username = :username")
    Optional<User> findByUsername(final String username);

    @NonNull
    @Query(value = "from User u left join fetch u.authorities where u.id = :id")
    Optional<User> findById(final @NonNull Long id);

    @NonNull
    @Query(value = "from User u left join fetch u.authorities")
    List<User> findAll();
}