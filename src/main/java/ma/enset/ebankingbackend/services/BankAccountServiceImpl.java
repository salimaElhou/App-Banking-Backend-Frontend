package ma.enset.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficentException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.mappers.BankAccountMapperImpl;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j //pour loger les messages
public class BankAccountServiceImpl implements BankAccountService{

    //@Autowired //pour faire l'injection des dependences soit avec consructeur
    private CustomerRepository customerRepository;

    private BankAccountRepository bankAccountRepository;

    private AccountOperationRepository accountOperationRepository;

    private BankAccountMapperImpl bankAccountMapper;


    //pour cree client
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new castomer");
        //transfeere DTO vers UNTITI (DAO)
        Customer customer=bankAccountMapper.fromCutomerDTO(customerDTO);
       Customer saveCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(saveCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("customer not found");

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);

        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double Rate, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("customer not found");

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(Rate);
        savingAccount.setCustomer(customer);

        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return bankAccountMapper.fromSavingBankAccount(savingAccount);
    }


    //transferer liste customers vers customersDTO
    @Override
    public List<CustomerDTO> listcustomers() {
        List<Customer> customers=customerRepository.findAll();
       List<CustomerDTO> customerDTOS= customers.stream()
               .map(customer -> bankAccountMapper.fromCustomer(customer))
               .collect(Collectors.toList());
        /*List<CustomerDTO> customerDTOS=new ArrayList<>();

        for (Customer customer:customers){
            CustomerDTO customerDTO =bankAccountMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }*/
        return customerDTOS;
    }

    //returner bank account par ID
    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException{
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount Not Found"));

        if(bankAccount instanceof SavingAccount){
                SavingAccount savingAccount= (SavingAccount) bankAccount;
                    return bankAccountMapper.fromSavingBankAccount(savingAccount);
            }
            else  {
                CurrentAccount currentAccount= (CurrentAccount) bankAccount;
                 return bankAccountMapper.fromCurrentBankAccount(currentAccount);
            }

    }

    //debiter -> retirer l'argent
    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount Not Found"));

        if(bankAccount.getBalance()<amount){
            throw new BalanceNotSufficentException("Balance not sufficient");
        }
            //operation de debit
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);

        //maj le solde du compte
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);


    }

    //pour ajouter l'argent
    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount Not Found"));

        //operation de crediter -> l ajout
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperationRepository.save(accountOperation);

        //maj le solde du compte
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    //pour trasferer l argent
    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BalanceNotSufficentException, BankAccountNotFoundException {
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);
    }
    @Override
    public List<BankAccountDTO> bankAccountList(){
      List<BankAccount> bankAccounts=  bankAccountRepository.findAll();
      List<BankAccountDTO> bankAccountDTOS=new ArrayList<>();
          for (BankAccount bankAccount:bankAccounts){
              if (bankAccount instanceof CurrentAccount){
                  CurrentAccount currentAccount= (CurrentAccount) bankAccount;
                  bankAccountDTOS.add(bankAccountMapper.fromCurrentBankAccount(currentAccount));
              }else {
                  SavingAccount savingAccount= (SavingAccount) bankAccount;
                  bankAccountDTOS.add(bankAccountMapper.fromSavingBankAccount(savingAccount));
              }
          }
      return bankAccountDTOS;
    }

    @Override
    public  CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElseThrow(()->new CustomerNotFoundException("customer Not Found"));
        return bankAccountMapper.fromCustomer(customer);
    }


    @Override
    public CustomerDTO UpdateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new castomer");
        //transfeere DTO vers UNTITI (DAO)
        Customer customer=bankAccountMapper.fromCutomerDTO(customerDTO);
        Customer saveCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(saveCustomer);
    }

    @Override
    public void DeleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

   @Override
    public List<AccountOperationDTO> historique(String accountId){
       List<AccountOperation> accountOperations= accountOperationRepository.findByBankAccountId(accountId);
       return accountOperations.stream().map(op->bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null){
            throw new BankAccountNotFoundException("bank not found");
        }
            Page<AccountOperation> accountOperationPage= accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page,size));
            AccountHistoryDTO accountHistoryDTO= new AccountHistoryDTO();
            List<AccountOperationDTO> accountOperationDTOS= accountOperationPage.getContent().stream().map(op->bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());

            accountHistoryDTO.setAccountOperationDTOList(accountOperationDTOS);
            accountHistoryDTO.setAccountId(bankAccount.getId());
            accountHistoryDTO.setBalance(bankAccount.getBalance());
            accountHistoryDTO.setCurrentPage(page);
            accountHistoryDTO.setPageSize(size);
            accountHistoryDTO.setTotalPages(accountOperationPage.getTotalPages());

        return accountHistoryDTO;
    }

}
