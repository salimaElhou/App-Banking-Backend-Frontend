package ma.enset.ebankingbackend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@DiscriminatorValue("SA") //c est le type de compte car tous les enreg sont stocker dans un seul table (single table)
@Data
@NoArgsConstructor @AllArgsConstructor
public class SavingAccount extends BankAccount{
private double interestRate;
}
