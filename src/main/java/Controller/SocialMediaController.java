package Controller;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Optional;
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
    /*
    private boolean isAccountInsertable(Account account){
        if (account.getUsername() == null){
            return false;
        }
        if (account.getPassword() == null){
            return false;
        }
        if (account.getPassword().length() <= 3){
            return false;
        }
        if (account.getUsername().isBlank()){
            return false;
        }
        if (AccountDao.instance().getAccountByUsername(account.getUsername()).isPresent()){
            return false;
        }
        return true;
    }
     */
    /* 
    private void registerHandler(Context ctx){
        Account account = ctx.bodyAsClass(Account.class);
        if (!isAccountInsertable(account)){
            ctx.status(400);
            return;
        }
        Account new_account = AccountDao.instance().insertAccount(account);
        if (new_account == null) {
            ctx.status(400);
            return;
        }
        ctx.json(new_account);
        ctx.status(200); // Not technically needed as this is the default
    }
    */

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
    
    /*
    private void loginHandler(Context ctx){
        Account prospective_account = ctx.bodyAsClass(Account.class);
        Optional<Account> retreived_account = AccountDao
            .instance()
            .getAccountByUsernameAndPassword(prospective_account.getUsername(), prospective_account.getPassword());
        if (retreived_account.isEmpty()){
            ctx.status(401);
            return;
        }
        ctx.json(retreived_account.get());
        ctx.status(200);
    }
     */
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
    /* 
    private boolean isMessageValid(Message message){
        if (message.getMessage_text().isBlank()){
            return false;
        }
        if (message.getMessage_text().length() > 255){
            return false;
        }
        if (!AccountDao.instance().getAccount(message.getPosted_by()).isPresent()){
            return false;
        }
        return true;
    }
        
    private void messageCreationHandler(Context ctx){
        Message prospective_message = ctx.bodyAsClass(Message.class);
        if (!isMessageValid(prospective_message)){
            ctx.status(400);
            return;
        }
        MessageDao.instance().insertMessage(prospective_message).ifPresentOrElse(
            m -> {
                ctx.json(m);
                ctx.status(200);
            }, 
            () -> {
                ctx.status(400);
            }
        );
    }
    */
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

    /* 
    private void messageRetreiveAllHandler(Context ctx){
        ctx.json(MessageDao.instance().getAllMessages());
        ctx.status(200);
    }
    */
    private void messageRetreiveAllHandler(Context ctx){
        ctx.json(SocialMediaService.instance().getAllMessages());
        ctx.status(200);
    }

    /* 
    private void messageRetreiveByIdHandler(Context ctx){
        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("message_id"));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            ctx.status(400); // Not specified by the project requirements.
            return;
        }
        MessageDao.instance().getMessage(id).ifPresentOrElse(
            m -> ctx.json(m),
            () -> ctx.json("")
        );
        ctx.status(200);
    }
    */
    private void messageRetreiveByIdHandler(Context ctx) throws NumberFormatException{
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        SocialMediaService.instance().getMessage(id).ifPresentOrElse(
            m -> ctx.json(m),
            () -> ctx.json("")
        );
        ctx.status(200);
    }

    /* 
    private void messageDeleteHandler(Context ctx){
        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("message_id"));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            ctx.status(400); // Not specified by the project requirements.
            return;
        }
        MessageDao.instance().deleteMessage(id).ifPresentOrElse(
            m -> ctx.json(m),
            () -> ctx.json("") 
        );
        ctx.status(200);
    }
    */
    private void messageDeleteHandler(Context ctx) throws NumberFormatException{
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        SocialMediaService.instance().deleteMessage(id).ifPresentOrElse(
            m -> ctx.json(m),
            () -> ctx.json("") 
        );
        ctx.status(200);
    }

    /* 
    private void messagePatchHandler(Context ctx){
        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("message_id"));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            ctx.status(400); // Not specified by the project requirements.
            return;
        }
        Message just_message_text = ctx.bodyAsClass(Message.class); // Would it be better to just access the json field directly?
        String new_message_body = just_message_text.getMessage_text();
        if (new_message_body.length() > 255 || new_message_body.isBlank() || new_message_body.isEmpty()){
            ctx.status(400);
            return;
        }

        MessageDao.instance().updateMessage(id, new_message_body).ifPresentOrElse(
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
    */
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

    /* 
    private void messageRetreiveAllFromUserHandler(Context ctx){
        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("account_id"));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            ctx.status(400); // Not specified by the project requirements.
            return;
        }
          //Commenting out because the test case expects the status code to be 400 if an acount does't exist.
          //Uncomment if test is updated.
        if (AccountDao.instance().getAccountById(id).isEmpty()){
            ctx.status(400);// Not specified by the project requirements
            return;
        }
        
        ctx.json(MessageDao.instance().getAllMessagesFromUser(id));
        ctx.status(200);
    }
    */

    private void messageRetreiveAllFromUserHandler(Context ctx) throws NumberFormatException{
        int id = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(SocialMediaService.instance().getAllMessagesFromUser(id));
        ctx.status(200);
    }


}