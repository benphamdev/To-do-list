package com.mynotes.repository;

import com.mynotes.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(
            value = "SELECT email FROM users WHERE email = :email",
            nativeQuery = true
    )
    List<String> doesEmailExist(
            @Param("email") String email
    );

    @Query(
            value = "SELECT * FROM users WHERE email = :email",
            nativeQuery = true
    )
    User getUserByEmail(
            @Param("email") String email
    );

    // GET USER ID BY EMAIL:
    @Query(
            value = "SELECT user_id FROM users WHERE email = :email ",
            nativeQuery = true
    )
    int getUserIdByEmail(
            @Param("email") String email
    );

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO users (first_name, last_name, email, password) VALUES (:first_name, :last_name, :email, :password)",
            nativeQuery = true
    )
    int signUpUser(
            @Param("first_name") String first_name,
            @Param("last_name") String last_name,
            @Param("email") String email,
            @Param("password") String password
    );

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE users SET profile_picture = :profile_picture WHERE user_id = :user_id",
            nativeQuery = true
    )
    int updateUserProfilePicture(
            @Param("user_id") int user_id,
            @Param("profile_picture") String profile_picture
    );

    // get user by id
    @Query(
            value = "SELECT * FROM users WHERE user_id = :user_id",
            nativeQuery = true
    )
    User loadUserById(
            @Param("user_id") int user_id
    );
}
// END OF USER REPOSITORY INTERFACE.
