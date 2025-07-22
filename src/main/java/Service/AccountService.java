package Service;

import java.util.Optional;

import DAO.AccountDao;
import Model.Account;

public class AccountService {
    private static AccountService accountService = null;

    public static AccountService instance(){
        if (accountService == null){
            accountService = new AccountService();
        }
        return accountService;
    }

    private AccountService(){}

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
}
