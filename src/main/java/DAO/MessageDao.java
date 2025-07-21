package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import Model.Message;
import Util.ConnectionUtil;

public class MessageDao {
    private static MessageDao messageDao = null;

    public static MessageDao instance(){
        if (messageDao == null){
            messageDao = new MessageDao();
        }
        return messageDao;
    }

    private MessageDao(){}

    public Optional<Message> insertMessage(Message message){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "INSERT INTO `message`(`posted_by`, `message_text`, `time_posted_epoch`) "+
                         "VALUES (?, ? ,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());
            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()){
                int generated_id = (int) pkeyResultSet.getLong(1);
                return Optional.of(new Message(
                    generated_id,
                    message.getPosted_by(),
                    message.getMessage_text(),
                    message.getTime_posted_epoch()
                ));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();

        ArrayList<Message> messages = new ArrayList<>();
        try{
            String sql = "SELECT * FROM `message`";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public Optional<Message> getMessage(int id){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "SELECT * FROM `message` WHERE `message_id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                return Optional.of(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Message> deleteMessage(int id){
        Connection connection = ConnectionUtil.getConnection();
        try{
            Message message = this.getMessage(id).orElseThrow();
            String sql = "DELETE FROM `message` WHERE `message_id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            return Optional.of(message);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch (NoSuchElementException e){
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<Message> updateMessage(int id, String new_message_text){
        Connection connection = ConnectionUtil.getConnection();
        try {
            Message message = this.getMessage(id).orElseThrow();
            String sql = "UPDATE `message` SET `message_text` = ? WHERE `message_id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, new_message_text);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            message.setMessage_text(new_message_text);
            return Optional.of(message);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (NoSuchElementException e){
            return Optional.empty();
        }
        return Optional.empty();
    }

    public List<Message> getAllMessagesFromUser(int id){
        Connection connection = ConnectionUtil.getConnection();

        ArrayList<Message> messages = new ArrayList<>();
        try{
            String sql = "SELECT * FROM `message` WHERE `posted_by`=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;
    }
}
