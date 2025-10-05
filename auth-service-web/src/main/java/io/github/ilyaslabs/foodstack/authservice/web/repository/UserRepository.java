package io.github.ilyaslabs.foodstack.authservice.web.repository;

import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByUsernameOrEmail(String username, String email);

}
