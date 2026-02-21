package co.edu.uniandes.dse.TallerPruebas.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.uniandes.dse.TallerPruebas.entities.AccountEntity;
import co.edu.uniandes.dse.TallerPruebas.exceptions.BusinessLogicException;
import co.edu.uniandes.dse.TallerPruebas.exceptions.EntityNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@DataJpaTest
@Transactional
@Import({ TransactionService.class, AccountService.class })
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<AccountEntity> accountList = new ArrayList<>();

   
    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

   
    private void clearData() {
        
        entityManager.getEntityManager().createQuery("delete from TransactionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PocketEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AccountEntity").executeUpdate();
    }

    
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AccountEntity accountEntity = factory.manufacturePojo(AccountEntity.class);
            accountEntity.setEstado("ACTIVA");
            accountEntity.setSaldo(1000.0 + i * 100.0); 
            entityManager.persist(accountEntity);
            accountList.add(accountEntity);
        }
        entityManager.flush();
    }

   
    @Test
    void testTransferenciaEntreCuentas() throws EntityNotFoundException, BusinessLogicException {
        AccountEntity origen = accountList.get(0);
        AccountEntity destino = accountList.get(1);

        origen.setSaldo(1000.0);
        destino.setSaldo(200.0);
        entityManager.merge(origen);
        entityManager.merge(destino);
        entityManager.flush();

        transactionService.transferenciaEntreCuentas(origen.getId(), destino.getId(), 300.0);

        AccountEntity updatedOrigen = entityManager.find(AccountEntity.class, origen.getId());
        AccountEntity updatedDestino = entityManager.find(AccountEntity.class, destino.getId());

        assertEquals(700.0, updatedOrigen.getSaldo(), 0.001);
        assertEquals(500.0, updatedDestino.getSaldo(), 0.001);
    }

   
    @Test
    void testTransferBetweenAccountsWithInvalidOrigin() {
        assertThrows(EntityNotFoundException.class, () -> {
            AccountEntity destino = accountList.get(1);
            transactionService.transferenciaEntreCuentas(0L, destino.getId(), 100.0);
        });
    }

   
    @Test
    void testTransferBetweenAccountsWithInvalidDestination() {
        assertThrows(EntityNotFoundException.class, () -> {
            AccountEntity origen = accountList.get(0);
            transactionService.transferenciaEntreCuentas(origen.getId(), 0L, 100.0);
        });
    }

    
    @Test
    void testTransferBetweenAccountsSameAccount() {
        assertThrows(BusinessLogicException.class, () -> {
            AccountEntity origen = accountList.get(0);
            transactionService.transferenciaEntreCuentas(origen.getId(), origen.getId(), 100.0);
        });
    }

    
    @Test
    void testTransferBetweenAccountsInsufficientFunds() {
        assertThrows(BusinessLogicException.class, () -> {
            AccountEntity origen = accountList.get(0);
            AccountEntity destino = accountList.get(1);

            origen.setSaldo(50.0);
            destino.setSaldo(500.0);
            entityManager.merge(origen);
            entityManager.merge(destino);
            entityManager.flush();

            transactionService.transferenciaEntreCuentas(origen.getId(), destino.getId(), 100.0);
        });
    }

    
    @Test
    void testTransferBetweenAccountsInvalidAmount() {
        assertThrows(BusinessLogicException.class, () -> {
            AccountEntity origen = accountList.get(0);
            AccountEntity destino = accountList.get(1);

            transactionService.transferenciaEntreCuentas(origen.getId(), destino.getId(), 0.0);
        });
    }

    
    @Test
    void testTransferBetweenAccountsRollback() {
        AccountEntity origen = accountList.get(0);
        AccountEntity destino = accountList.get(1);

        origen.setSaldo(80.0);
        destino.setSaldo(300.0);
        entityManager.merge(origen);
        entityManager.merge(destino);
        entityManager.flush();

        assertThrows(BusinessLogicException.class, () -> {
            transactionService.transferenciaEntreCuentas(origen.getId(), destino.getId(), 1000.0);
        });

        AccountEntity updatedOrigen = entityManager.find(AccountEntity.class, origen.getId());
        AccountEntity updatedDestino = entityManager.find(AccountEntity.class, destino.getId());

        assertEquals(80.0, updatedOrigen.getSaldo(), 0.001);
        assertEquals(300.0, updatedDestino.getSaldo(), 0.001);
    }
}
