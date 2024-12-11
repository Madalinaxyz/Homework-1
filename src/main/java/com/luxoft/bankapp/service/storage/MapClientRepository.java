package com.luxoft.bankapp.service.storage;

import com.luxoft.bankapp.model.Client;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class MapClientRepository implements ClientRepository {

    private Map<Long, Client> data;

    private long currentId = 0;

    public MapClientRepository() {

        this.data = new HashMap<>();
    }

    @Override
    public Client add(Client client) {

        client.setId(currentId++);
        data.put(client.getId(), client);

        return client;
    }

    @Override
    public Client get(long id) {

        return data.get(id);
    }

    @Override
    public List<Client> getAll() {

        return new ArrayList<>(data.values());
    }

    @Override
    public Client getBy(String name) {
        for (Client client : getAll()) {
            if (name.equals(client.getName())) {
                return client;
            }
        }

        return null;
    }

    @Override
    public Client update(Client client) {
        return data.put(client.getId(), client);
    }

    @Override
    public boolean remove(long id) {
        return data.remove(id) != null;
    }
}
