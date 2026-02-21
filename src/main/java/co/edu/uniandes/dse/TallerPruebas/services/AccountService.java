package co.edu.uniandes.dse.TallerPruebas.services;

import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.TallerPruebas.entities.AccountEntity;
import co.edu.uniandes.dse.TallerPruebas.exceptions.BusinessLogicException;
import co.edu.uniandes.dse.TallerPruebas.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.TallerPruebas.repositories.AccountRepository;

@Service
public class AccountService {

    private static final String ESTADO_ACTIVA = "ACTIVA";

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountEntity getAccountOrThrow(Long accountId) throws EntityNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("No existe la cuenta con id: " + accountId));
    }

    public void validatePositiveAmount(Double monto) throws BusinessLogicException {
        if (monto == null || monto <= 0) {
            throw new BusinessLogicException("El monto debe ser mayor que cero");
        }
    }

    public void validateActive(AccountEntity account) throws BusinessLogicException {
        if (account == null || account.getEstado() == null || !ESTADO_ACTIVA.equalsIgnoreCase(account.getEstado())) {
            throw new BusinessLogicException("La cuenta está bloqueada");
        }
    }

    public void validateSufficientFunds(AccountEntity account, Double monto) throws BusinessLogicException {
        if (account.getSaldo() == null || account.getSaldo() < monto) {
            throw new BusinessLogicException("Saldo insuficiente");
        }
    }
}

