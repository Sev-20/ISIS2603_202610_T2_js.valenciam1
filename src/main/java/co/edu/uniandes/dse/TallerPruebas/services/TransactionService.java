package co.edu.uniandes.dse.TallerPruebas.services;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.TallerPruebas.entities.AccountEntity;
import co.edu.uniandes.dse.TallerPruebas.entities.TransactionEntity;
import co.edu.uniandes.dse.TallerPruebas.exceptions.BusinessLogicException;
import co.edu.uniandes.dse.TallerPruebas.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.TallerPruebas.repositories.AccountRepository;
import co.edu.uniandes.dse.TallerPruebas.repositories.TransactionRepository;

@Service
public class TransactionService {

    private static final String TIPO_ENTRADA = "ENTRADA";
    private static final String TIPO_SALIDA = "SALIDA";

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    
    @Transactional
    public void transferenciaEntreCuentas(Long originAccountId, Long destinationAccountId, Double monto)
            throws EntityNotFoundException, BusinessLogicException {

        accountService.validatePositiveAmount(monto);

        AccountEntity origin = accountService.getAccountOrThrow(originAccountId);
        AccountEntity destination = accountService.getAccountOrThrow(destinationAccountId);

        if (origin.getId().equals(destination.getId())) {
            throw new BusinessLogicException("La cuenta origen no puede ser la misma cuenta destino");
        }

        accountService.validateSufficientFunds(origin, monto);

        accountService.validateActive(origin);
        accountService.validateActive(destination);

        origin.setSaldo(origin.getSaldo() - monto);
        destination.setSaldo(destination.getSaldo() + monto);

        accountRepository.save(origin);
        accountRepository.save(destination);

        transactionRepository.save(buildTransaction(origin, monto, TIPO_SALIDA));
        transactionRepository.save(buildTransaction(destination, monto, TIPO_ENTRADA));
    }

    public List<TransactionEntity> getTransactionsByAccount(Long accountId) throws EntityNotFoundException {
        accountService.getAccountOrThrow(accountId);
        return transactionRepository.findByAccount_IdOrderByFechaDesc(accountId);
    }

    private TransactionEntity buildTransaction(AccountEntity account, Double monto, String tipo) {
        TransactionEntity tx = new TransactionEntity();
        tx.setAccount(account);
        tx.setMonto(monto);
        tx.setFecha(new Date());
        tx.setTipo(tipo);
        return tx;
    }
}
