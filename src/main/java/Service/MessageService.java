package Service;

import java.util.List;
import java.util.Optional;

import DAO.AccountDao;
import DAO.MessageDao;
import Model.Message;

public class MessageService {
    private static MessageService messageService = null;

    public static MessageService instance(){
        if (messageService == null){
            messageService = new MessageService();
        }
        return messageService;
    }

    private MessageService(){}

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
