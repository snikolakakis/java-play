package controllers;
import controllers.execution_context.DatabaseExecutionContext;
import play.db.jpa.JPAApi;
import play.mvc.*;
import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private JPAApi jpaApi;
    private DatabaseExecutionContext executionContext;


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    @Inject
    public HomeController(JPAApi api,  DatabaseExecutionContext executionContext) {
        this.jpaApi = api;
        this.executionContext = executionContext;
    }





}
