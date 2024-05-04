package ma.enset.ebankingbackend.mappers;

import ma.enset.ebankingbackend.dtos.AccountOperationDTO;
import ma.enset.ebankingbackend.dtos.CurrentBankAccountDTO;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.dtos.SavingBankAccountDTO;
import ma.enset.ebankingbackend.entities.AccountOperation;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Service
public class BankAccountMapperImpl {

    //Mapping Costomer
    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO=new CustomerDTO();
        BeanUtils.copyProperties(customer,customerDTO);
        //customerDTO.setId(customer.getId());
       return customerDTO;
    }

    public Customer fromCutomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO,customer);
        return  customer;
    }



    //Mapping Saving account
    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount){
            SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
            BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
            savingBankAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
            savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
            //savingBankAccountDTO.setStatus(savingAccount.getStatus());
            return savingBankAccountDTO;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO){
        SavingAccount savingAccount=new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        savingAccount.setCustomer(fromCutomerDTO(savingBankAccountDTO.getCustomerDTO()));
        return savingAccount;
    }



    //Mapping Current account
    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount){
       CurrentBankAccountDTO currentBankAccountDTO=new CurrentBankAccountDTO();
       BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
       currentBankAccountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
        currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        //currentBankAccountDTO.setStatus(currentAccount.getStatus());
        return currentBankAccountDTO;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO){
        CurrentAccount currentAccount=new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        currentAccount.setCustomer(fromCutomerDTO(currentBankAccountDTO.getCustomerDTO()));
            return currentAccount;
    }



    //Mapping Account Operation
    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO=new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);
        return accountOperationDTO;
    }



}
