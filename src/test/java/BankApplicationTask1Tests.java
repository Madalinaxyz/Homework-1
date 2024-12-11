import com.luxoft.bankapp.model.AbstractAccount;
import com.luxoft.bankapp.model.CheckingAccount;
import com.luxoft.bankapp.model.Client;
import com.luxoft.bankapp.model.SavingAccount;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.BankingImpl;
import com.luxoft.bankapp.service.storage.ClientRepository;
import com.luxoft.bankapp.service.storage.MapClientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

//@SpringJUnitConfig(locations = "classpath:test-clients.xml")
@SpringJUnitConfig(classes = BankApplication.class)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BankApplicationTask1Tests {
    private static final String[] CLIENT_NAMES =
            {"Jonny Bravo", "Adam Budzinski", "Anna Smith"};

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Banking banking;

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    public void init() {
        BankApplication.initialize(applicationContext);
    }

    @Test
    public void repositoryBeanConfiguration() {
        assertNotNull(repository, "repository bean should be configured");
        assertTrue((repository instanceof MapClientRepository),
                "repository should be instantiated with MapClientRepository class");
    }

    @Test
    public void bankingBeanConfiguration() {
        assertNotNull(banking, "banking bean should be configured");
        assertTrue((banking instanceof BankingImpl), "banking should be instantiated with BankingImpl class");
    }

    @Test
    public void initializationClient1() {
        Client client = banking.getClient(CLIENT_NAMES[0]);
        assertNotNull(client, "banking should have client with name: " + CLIENT_NAMES[0]);

        assertEquals(2, client.getAccounts().size());
    }

    @Test
    public void client1SavingAccount() {
        Client client = banking.getClient(CLIENT_NAMES[0]);

        AbstractAccount account = client.getAccount(SavingAccount.class);

        assertNotNull(account,
                client.getName() + "should have "
                        + SavingAccount.class.getSimpleName() + " account");

        assertEquals(1000, account.getBalance());
    }

    @Test
    public void client1CheckingAccount() {
        Client client = banking.getClient(CLIENT_NAMES[0]);

        CheckingAccount account = (CheckingAccount) client.getAccount(CheckingAccount.class);

        assertNotNull(account,
                client.getName() + "should have "
                        + CheckingAccount.class.getSimpleName() + " account");

        assertEquals(0, account.getBalance());
        assertEquals(1000, account.getOverdraft());
    }

    @Test
    public void initializationClient2() {
        Client client = banking.getClient(CLIENT_NAMES[1]);
        assertNotNull(client, "banking should have client with name: " + CLIENT_NAMES[1]);

        assertEquals(1, client.getAccounts().size());
    }

    @Test
    public void client2CheckingAccount() {
        Client client = banking.getClient(CLIENT_NAMES[1]);

        CheckingAccount account = (CheckingAccount) client.getAccount(CheckingAccount.class);

        assertNotNull(account,
                client.getName() + "should have "
                        + CheckingAccount.class.getSimpleName() + " account");

        assertEquals(0, account.getBalance());
        assertEquals(1500, account.getOverdraft());
    }

    @Test
    public void workWithExistingClientsTest() {
        BankApplication.workWithExistingClients(banking);

        Client jonny = banking.getClient(CLIENT_NAMES[0]);
        assertEquals(4000, jonny.getActiveAccount().getBalance());

        Client adam = banking.getClient(CLIENT_NAMES[1]);
        assertEquals(-500, adam.getActiveAccount().getBalance());
    }

    @Test
    public void bankingServiceDemoTest() {
        BankApplication.bankingServiceDemo(banking);

        Client anna = banking.getClient(CLIENT_NAMES[2]);
        assertNotNull(anna, "banking should have client with name: " + CLIENT_NAMES[2]);

        AbstractAccount saving = anna.getAccount(SavingAccount.class);

        assertNotNull(saving, CLIENT_NAMES[2] + " should have "
                + SavingAccount.class.getSimpleName() + " account.");
        assertEquals(1000, saving.getBalance());


        AbstractAccount checking = anna.getAccount(CheckingAccount.class);

        assertNotNull(checking, CLIENT_NAMES[2] + " should have "
                + CheckingAccount.class.getSimpleName() + " account.");
        assertEquals(3000, checking.getBalance());
    }

}
