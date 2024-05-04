package ma.enset.ebankingbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebankingbackend.enums.AccountStatus;

import java.util.Date;
import java.util.List;
@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//tous va stocker dans un seul table
@DiscriminatorColumn(name = "TYPE", length = 4)//colone type char peut avoir 4 lettre
@Data
@NoArgsConstructor @AllArgsConstructor
public abstract class BankAccount {
    @Id
    private String id;
    private double balance;//solde
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne //plusienrs comptes concerne un client
    private Customer customer;

    //fetch =
    // FetchType.EAGER => pour afficher les infos de ce attribute
    // FetchType.LAZY => le chargement ce fait a la demande et le demade -> f la Couche service
    @OneToMany(mappedBy = "bankAccount",fetch = FetchType.LAZY)    //compte -> +operations
    private List<AccountOperation> accountOperations;
}
