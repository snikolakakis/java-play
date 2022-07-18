package controllers.execution_context;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

/**
 * Custom execution context wired to "database.dispatcher" thread pool
 */
public class DatabaseExecutionContext extends CustomExecutionContext {
    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "database.dispatcher");
    }




}


