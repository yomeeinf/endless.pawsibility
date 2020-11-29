package kamillobinski.sheltybackend.repository;

import kamillobinski.sheltybackend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();

    Optional<User> findByUsername(String username);

    User getUserByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u.avatar FROM User u WHERE u.id = ?1")
    String getUserAvatar(int id);

}
