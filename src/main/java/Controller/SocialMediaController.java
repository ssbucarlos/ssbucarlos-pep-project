package Controller;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("register", this::registerHandler);
        app.post("login", this::loginHandler);
        app.post("messages", this::messageCreationHandler);
        app.get("messages", this::messageRetreiveAllHandler);
        app.get("messages/{message_id}", this::messageRetreiveByIdHandler);
        app.delete("messages/{message_id}", this::messageDeleteHandler);
        app.patch("messages/{message_id}", this::messagePatchHandler);
        app.get("accounts/{account_id}/messages", this::messageRetreiveAllFromUserHandler);
        return app;
    }

    private void registerHandler(Context ctx){
        Account account = ctx.bodyAsClass(Account.class);
        SocialMediaService.instance().registerAccount(account).ifPresentOrElse(
            a -> {
                ctx.json(a);
                ctx.status(200); // Not technically needed as this is the default
            },
            () -> {
                ctx.status(400);
            }
        );
    }
    
    private void loginHandler(Context ctx){
        Account account = ctx.bodyAsClass(Account.class);
        SocialMediaService.instance().loginToAccount(account).ifPresentOrElse(
            a -> {
                ctx.json(a);
                ctx.status(200);
            },
            ()-> {
                ctx.status(401);
            }
        );
    }

    private void messageCreationHandler(Context ctx){
        Message message = ctx.bodyAsClass(Message.class);
        SocialMediaService.instance().insertMessage(message).ifPresentOrElse(
            m -> {
                ctx.json(m);
                ctx.status(200);
            }, 
            () -> {
                ctx.status(400);
            }
        );
    }

    private void messageRetreiveAllHandler(Context ctx){
        ctx.json(SocialMediaService.instance().getAllMessages());
        ctx.status(200);
    }

    private void messageRetreiveByIdHandler(Context ctx) throws NumberFormatException{
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        SocialMediaService.instance().getMessage(id).ifPresentOrElse(
            m -> ctx.json(m),
            () -> ctx.json("")
        );
        ctx.status(200);
    }

    private void messageDeleteHandler(Context ctx) throws NumberFormatException{
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        SocialMediaService.instance().deleteMessage(id).ifPresentOrElse(
            m -> ctx.json(m),
            () -> ctx.json("") 
        );
        ctx.status(200);
    }

    private void messagePatchHandler(Context ctx) throws NumberFormatException{
        int message_that_needs_patching_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message incoming_message_but_it_only_has_the_new_text_we_want = ctx.bodyAsClass(Message.class);
        String new_message_text = incoming_message_but_it_only_has_the_new_text_we_want.getMessage_text();

        SocialMediaService.instance().updateMessage(message_that_needs_patching_id, new_message_text).ifPresentOrElse(
            m -> {
                ctx.json(m);
                ctx.status(200);
            },
            () -> {
                ctx.json(""); 
                ctx.status(400);
            }
        );
    }

    private void messageRetreiveAllFromUserHandler(Context ctx) throws NumberFormatException{
        int id = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(SocialMediaService.instance().getAllMessagesFromUser(id));
        ctx.status(200);
    }


}