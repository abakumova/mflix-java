package mflix.api.daos;

import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import mflix.api.models.Session;
import mflix.api.models.User;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class UserDao extends AbstractMFlixDao {

  private final MongoCollection<User> usersCollection;
  //Ticket: User Management - do the necessary changes so that the sessions collection returns a Session object
  private final MongoCollection<Session> sessionsCollection;

  private final Logger log;

  @Autowired
  public UserDao(
      MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
    super(mongoClient, databaseName);
    CodecRegistry pojoCodecRegistry =
        fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    usersCollection = db.getCollection("users", User.class).withCodecRegistry(pojoCodecRegistry);
    log = LoggerFactory.getLogger(this.getClass());
    //Ticket: User Management - implement the necessary changes so that the sessions collection returns a Session objects instead of Document objects.
    sessionsCollection = db.getCollection("sessions", Session.class).withCodecRegistry(pojoCodecRegistry);
  }

  /**
   * Inserts the `user` object in the `users` collection.
   *
   * @param user - User object to be added
   * @return True if successful, throw IncorrectDaoOperation otherwise
   */
  public boolean addUser(User user) {
    //Ticket: Durable Writes -  you might want to use a more durable write concern here!
    //Ticket: Handling Errors - make sure to only add new users and not users that already exist.
    usersCollection.withWriteConcern(WriteConcern.W2);
    Optional optional = Optional.ofNullable(getUser(user.getEmail()));
    if(!optional.isPresent()){
      usersCollection.insertOne(user);
    } else {
      throw new IncorrectDaoOperation("this user is already added", new Throwable());
    }

    return true;
  }

    /**
     * Creates session using userId and jwt token.
     *
     * @param userId - user string identifier
     * @param jwt    - jwt string token
     * @return true if successful
     */
    public boolean createUserSession(String userId, String jwt) {
        //Ticket: User Management - implement the method that allows session information to be stored in it's designated collection.
        //Ticket: Handling Errors - implement a safeguard against creating a session with the same jwt token.
        Session session = new Session();
        session.setUserId(userId);
        session.setJwt(jwt);

        Optional optional = Optional.ofNullable(getUserSession(userId));
        if (optional.isPresent()) {
            deleteUserSessions(userId);
        }

        sessionsCollection.insertOne(session);
        return true;
    }

  /**
   * Returns the User object matching the an email string value.
   *
   * @param email - email string to be matched.
   * @return User object or null.
   */
  public User getUser(String email) {
    Bson emailFilter = Filters.in("email", email);
    //Ticket: User Management - implement the query that returns the first User object.
    User user = usersCollection.find(emailFilter).first();
    return user;
  }

  /**
   * Given the userId, returns a Session object.
   *
   * @param userId - user string identifier.
   * @return Session object or null.
   */
  public Session getUserSession(String userId) {
    //Ticket: User Management - implement the method that returns Sessions for a given userId
    Bson userIdFilter = Filters.in("user_id", userId);
    return sessionsCollection.find(userIdFilter).first();
  }

  public boolean deleteUserSessions(String userId) {
    //Ticket: User Management - implement the delete user sessions method
    Bson userIdFilter = Filters.in("user_id", userId);
    sessionsCollection.deleteOne(userIdFilter);
    return true;
  }

  /**
   * Removes the user document that match the provided email.
   *
   * @param email - of the user to be deleted.
   * @return true if user successfully removed
   */
  public boolean deleteUser(String email) {
    // remove user sessions
    deleteUserSessions(email);
    //Ticket: User Management - implement the delete user method
    //Ticket: Handling Errors - make this method more robust by handling potential exceptions.
    Bson emailFilter = Filters.in("email", email);
    Optional optional = Optional.ofNullable(getUser(email));
    if(optional.isPresent()) {
      usersCollection.findOneAndDelete(emailFilter);
      return true;
    } else {
      return false;
    }
  }

    /**
     * Updates the preferences of an user identified by `email` parameter.
     *
     * @param email           - user to be updated email
     * @param userPreferences - set of preferences that should be stored and replace the existing
     *                        ones. Cannot be set to null value
     * @return User object that just been updated.
     */
    public boolean updateUserPreferences(String email, Map<String, ?> userPreferences) {
        //Ticket: User Preferences - implement the method that allows for user preferences to be updated.
        //Ticket: Handling Errors - make this method more robust by handling potential exceptions when updating an entry.
        Bson emailFilter = Filters.in("email", email);
        Optional optional = Optional.ofNullable(getUser(email));
        if(optional.isPresent() && userPreferences != null) {
          usersCollection.updateOne(emailFilter, Updates.set("preferences", new Document((Map<String, Object>) userPreferences)));
          return true;
        } else {
          return false;
        }
    }
}
