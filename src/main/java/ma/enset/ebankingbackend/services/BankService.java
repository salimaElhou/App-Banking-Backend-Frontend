package ma.enset.ebankingbackend.services;


import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.SavingAccount;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public void consulter(){
        //Afficher les infos par ID
        BankAccount
                bankAccount = bankAccountRepository.findById("181d6abf-1977-46a4-92e7-3fefa82b6b32").orElse(null);
        if(bankAccount != null){


            System.out.println("\n"+"********les infos sur un compte par ID**********"+"\n");
            System.out.println(bankAccount.toString());
            System.out.println(bankAccount.getClass().getSimpleName());//nom de classe (SA CA)

            //tester (saving account or current account)
            if(bankAccount instanceof CurrentAccount){
                System.out.println("overDraft=>"+((CurrentAccount)bankAccount).getOverDraft());
            }else if(bankAccount instanceof SavingAccount){
                System.out.println("taux d'int (Rate)=>"+((SavingAccount)bankAccount).getInterestRate());

            }
            //Afficher les operations des compte par ID
            System.out.println("\n"+"********les operations de ce compte par ID ********"+"\n");
            bankAccount.getAccountOperations().forEach(op->{
                System.out.println(op.toString());
            });
    }
    }
}
