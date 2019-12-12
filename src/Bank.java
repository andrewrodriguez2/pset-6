import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bank {
	

    
    private final static int ACCT_START = 0;
    private final static int ACCT_END = 9;
    private final static int PIN_START = 9;
    private final static int PIN_END = 13;
    private final static int FIRST_NAME_START = 13;
    private final static int FIRST_NAME_END = 33;
    private final static int LAST_NAME_START = 33;
    private final static int LAST_NAME_END = 63;
    private final static int BALANCE_START = 63;
    
    private final static String DATA = "data/accounts.dat";		
    
    private List<BankAccount> accounts;							

    
    public Bank() throws IOException {
        accounts = init();
        
        if (accounts == null) {
        	throw new IOException();
        }
    }
    

    
    public BankAccount createAccount(int pin, User user) {
    	accounts.add(new BankAccount(pin, generateAccountNo(), 0.00, user));
    	
    	return accounts.get(accounts.size() - 1);
    }
    

    
    public BankAccount login(long accountNo, int pin) {
        BankAccount bankAccount = getAccount(accountNo);
        
        if (bankAccount.getPin() == pin) {
            return bankAccount;
        } else {
            return null;
        }
    }
    

    
    public BankAccount getAccount(long accountNo) {
        for (BankAccount account : accounts) {
            if (account.getAccountNo() == accountNo) {
                return account;
            }
        }
        
        return null;
    }
    

    
    public void update(BankAccount account) {
        int index = -1;
        
        for (int i = 0; i < accounts.size(); i++) {
            BankAccount storedAccount = accounts.get(i);
            
            if (storedAccount.getAccountNo() == account.getAccountNo()) {
                index = i;
                break;
            }
        }
        
        accounts.set(index, account);
    }
    

    
    public boolean save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA))) {
            for (BankAccount account : accounts) {
                bw.write(account.toString());
                bw.newLine();
            }
            
            return true;
        } catch (IOException e) {
        	System.err.println("Error: Unable to write to data file.");
        	
        	return false;
        }
    }
    
    
    private List<BankAccount> init() {
        List<BankAccount> accounts = new ArrayList<BankAccount>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(new File(DATA)))) {
            String account;
            
            while ((account = br.readLine()) != null) {
                accounts.add(Bank.parseBankAccount(account));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Unable to find data file.");
            
            accounts = null;
        } catch (IOException e) {
            System.err.println("Error: Unable to read from data file.");
            
            accounts = null;
        }
        
        return accounts;
    }
    

    
    private long generateAccountNo() {
        long accountNo = -1;
        
        for (BankAccount account : accounts) {
            if (account.getAccountNo() > accountNo) {
                accountNo = account.getAccountNo();
            }
        }
        
        return accountNo + 1;
    }

    
    private static BankAccount parseBankAccount(String account) {
        return new BankAccount(Bank.parsePin(account),
            Bank.parseAccountNo(account),
            Bank.parseBalance(account),
            Bank.parseUser(account)
        );
    }
    
    
    private static long parseAccountNo(String account) {
        return Long.parseLong(account.substring(ACCT_START, ACCT_END));
    }
    
    
    private static int parsePin(String account) {
        return Integer.parseInt(account.substring(PIN_START, PIN_END));
    }
    

    
    private static User parseUser(String account) {        
        return new User(account.substring(FIRST_NAME_START, FIRST_NAME_END).strip(),
            account.substring(LAST_NAME_START, LAST_NAME_END).strip()
        );
    }
    

    private static double parseBalance(String account) {
        return Double.parseDouble(account.substring(BALANCE_START).strip());
    }
}