package ma.enset.ebankingbackend;

import ma.enset.ebankingbackend.dtos.BankAccountDTO;
import ma.enset.ebankingbackend.dtos.CurrentBankAccountDTO;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.dtos.SavingBankAccountDTO;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.enums.AccountStatus;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficentException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}
	//partie consultation
	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService){

		return args -> {
			//pour enregistrer les clients
			Stream.of("salima" ,"salma", "imane","khaoula").forEach(name->{
				CustomerDTO customerDTO=new CustomerDTO();
				customerDTO.setName(name);
				customerDTO.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customerDTO);
			});
			//pour cree les comptes des clients
			//pour chaque client (current & saving)
			bankAccountService.listcustomers().forEach(cust->{
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,cust.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*12000,5.5,cust.getId());



                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
			//consulter les comptes pour crediter et debiter
			List<BankAccountDTO> bankAccounts= bankAccountService.bankAccountList();
			for (BankAccountDTO bankAccount:bankAccounts) {

				String accountId;
				if(bankAccount instanceof SavingBankAccountDTO){
					accountId=((SavingBankAccountDTO) bankAccount).getId();
				}else {
					accountId=((CurrentBankAccountDTO) bankAccount).getId();
				}

				bankAccountService.credit(accountId, 100000 + Math.random() * 120000, "Credit");
				bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "Debit");
			}

		};

	}










	//@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository){
		return args -> {

			Stream.of("Hassane" ,"yassine", "aicha").forEach(name ->{
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});


			//pour chaque client je cree 2 account

			customerRepository.findAll().forEach(cust->{

				//currentAccount
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(90000);
				bankAccountRepository.save(currentAccount);

				//SavingAccount
					SavingAccount savingAccount = new SavingAccount();
				    savingAccount.setId(UUID.randomUUID().toString());
				    savingAccount.setBalance(Math.random()*90000);
					savingAccount.setCreatedAt(new Date());
					savingAccount.setStatus(AccountStatus.CREATED);//stocker dans la BD (0->CREATED,1,2)
					savingAccount.setCustomer(cust);
					savingAccount.setInterestRate(5.5);
					bankAccountRepository.save(savingAccount);

			});
			//parcouri tous les comptes
			//chaque compte -> cree operation
			// 5 comptes donc -> 5 operations
			//(OperationDate,Amount,Type,BankAccount)
			bankAccountRepository.findAll().forEach(acc->{
				for (int i = 0; i < 10; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random()*12000);
					accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}

			});
		};


	}

}
