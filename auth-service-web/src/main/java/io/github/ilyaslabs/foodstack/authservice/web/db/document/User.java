package io.github.ilyaslabs.foodstack.authservice.web.db.document;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Document(collection = "users")
@Data
public class User {

    private ObjectId id;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Indexed(unique = true)
    private String email;

    private Boolean enabled;

    private List<Scope> scopes;

    private Instant createdAt;

    private Instant updatedAt;

    @Indexed
    private Instant deletedAt;

    public enum Scope {
        SUPER_ADMIN, ADMIN, USER
    }
}
