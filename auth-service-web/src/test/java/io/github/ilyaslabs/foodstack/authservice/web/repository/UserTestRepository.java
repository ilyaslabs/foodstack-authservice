package io.github.ilyaslabs.foodstack.authservice.web.repository;

import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
public interface UserTestRepository extends MongoRepository<User, ObjectId> {
}
