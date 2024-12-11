package com.luxoft.bankapp.service;

import com.luxoft.bankapp.exceptions.AccountNotFoundException;
import com.luxoft.bankapp.exceptions.ClientNotFoundException;
import com.luxoft.bankapp.model.*;
import com.luxoft.bankapp.service.storage.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class BankingImpl implements Banking {

    @Autowired
    private ClientRepository repository;

    @Override
    public Client addClient(Client c) {

        Client created = repository.add(c);
        c.setRepository(repository);

        return created;
    }

    @Override
    public Client getClient(String name) {

        Client foundClient = repository.getBy(name);

        if (foundClient != null) {

            return foundClient;
        }

        throw new ClientNotFoundException(name);
    }

    @Override
    public List<Client> getClients() {

        return repository.getAll();
    }

    @Override
    public void deleteClient(Client c) {

        repository.remove(c.getId());
    }

    @Override
    public AbstractAccount createAccount(Client c, Class type) {

        AbstractAccount account = null;

        Client client = repository.get(c.getId());

        if (client != null) {

            account = new SavingAccount(0);

            if (type == CheckingAccount.class) {
                account = new CheckingAccount(0);
            }

            client.addAccount(account);

            repository.update(c);
        }

        return account;
    }

    @Override
    public void updateAccount(Client c, AbstractAccount account) {

        Client clientToUpdate = repository.get(c.getId());

        if (clientToUpdate != null) {

            clientToUpdate.removeAccount(account.getClass());
            clientToUpdate.addAccount(account);

            repository.update(c);
        }
    }

    @Override
    public AbstractAccount getAccount(Client c, Class type) {

        return c.getAccounts().stream()
                .filter(a -> a.getClass() == type)
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException());
    }

    @Override
    public List<AbstractAccount> getAllAccounts() {

        return repository.getAll()
                .stream()
                .flatMap(c -> c.getAccounts().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<AbstractAccount> getAllAccounts(Client c) {

        return new ArrayList<>(repository.get(c.getId()).getAccounts());
    }

    @Override
    public void transferMoney(Client from, Client to, double amount) {

        from.withdraw(amount);
        to.deposit(amount);
    }

    public void setRepository(ClientRepository repository) {

        this.repository = repository;
    }
}
