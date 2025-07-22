package Service;

import DAO.AccountDao;
import DAO.MessageDao;
import Model.Account;
import Model.Message;

import java.util.Optional;
import java.util.List;

public class SocialMediaService {
    private static SocialMediaService socialMediaService = null;

    public static SocialMediaService instance(){
        if (socialMediaService == null){
            socialMediaService = new SocialMediaService();
        }
        return socialMediaService;
    }

    private SocialMediaService(){}

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
        if (AccountDao.instance().getAccount(account.getUsername()).isPresent()){
            return false;
        }
        return true;
    }

    public Optional<Account> registerAccount(Account account){
        if (!isAccountInsertable(account)){
            return Optional.empty();
        }
        return AccountDao.instance().insertAccount(account);
    }

    public Optional<Account> loginToAccount(Account account){
        return AccountDao.instance().getAccount(account.getUsername(), account.getPassword());
    }

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

    public Optional<Message> insertMessage(Message message){
        if (!isMessageValid(message)){
            return Optional.empty();
        }
        return MessageDao.instance().insertMessage(message);
    }

    public List<Message> getAllMessages(){
        return MessageDao.instance().getAllMessages();
    }

    public Optional<Message> getMessage(int id){
        return MessageDao.instance().getMessage(id);
    }

    public Optional<Message> deleteMessage(int id){
        return MessageDao.instance().deleteMessage(id);
    }

    public Optional<Message> updateMessage(int id, String new_text){
        if (new_text.length() > 255 || new_text.isBlank() || new_text.isEmpty()){
            return Optional.empty();
        }
        return MessageDao.instance().updateMessage(id, new_text);
    }

    public List<Message> getAllMessagesFromUser(int id){
        return MessageDao.instance().getAllMessagesFromUser(id);
    }

}
