import com.luxoft.bankapp.exceptions.ActiveAccountNotSet;
import com.luxoft.bankapp.model.AbstractAccount;
import com.luxoft.bankapp.model.CheckingAccount;
import com.luxoft.bankapp.model.Client;
import com.luxoft.bankapp.model.SavingAccount;
import com.luxoft.bankapp.service.BankReportService;
import com.luxoft.bankapp.service.BankReportServiceImpl;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.BankingImpl;
import com.luxoft.bankapp.model.Client.Gender;
import com.luxoft.bankapp.service.storage.ClientRepository;
import com.luxoft.bankapp.service.storage.MapClientRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration // Marks this class as a configuration class for Spring
@ComponentScan("com.luxoft.bankapp") // Scans the specified package for components

public class BankApplication {

    private static final String[] CLIENT_NAMES =
            {"Jonny Bravo", "Adam Budzinski", "Anna Smith"};

    public static void main(String[] args) {

//        ClientRepository repository = new MapClientRepository();
//        Banking banking = initialize(repository);

        // Step 1: Load Spring Application Context

//        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml", "test-clients.xml");
//        ApplicationContext context = new ClassPathXmlApplicationContext("test-clients.xml");
            ApplicationContext context = new AnnotationConfigApplicationContext(BankApplication.class);

        // Step 2: Use the initialize method to prepare the Banking bean
        Banking banking = initialize(context);

        workWithExistingClients(banking);

        bankingServiceDemo(banking);

        // Call the updated bankReportsDemo
        bankReportsDemo(context);
    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("clients.properties"));
        return configurer;
    }
    // Account Beans
    @Bean
    public SavingAccount savingAccount1(@Value("${client1.savingAccount.balance}") double balance) {
        return new SavingAccount(balance);
    }

    @Bean
    public CheckingAccount checkingAccount1(
            @Value("${client1.checkingAccount.balance}") double balance,
            @Value("${client1.checkingAccount.overdraft}") double overdraft) {
        return new CheckingAccount(balance, overdraft);
    }

    @Bean
    public CheckingAccount checkingAccount2(
            @Value("${client2.checkingAccount.balance}") double balance,
            @Value("${client2.checkingAccount.overdraft}") double overdraft) {
        return new CheckingAccount(balance, overdraft);
    }
    @Bean
    public Client client1(
            @Value("${client1.name}") String name,
            @Value("${client1.city}") String city,
            @Qualifier("savingAccount1") SavingAccount savingAccount1,
            @Qualifier("checkingAccount1") CheckingAccount checkingAccount1) {
        Client client = new Client(name, Client.Gender.MALE);
        client.setCity(city);
        client.addAccount(savingAccount1);
        client.addAccount(checkingAccount1);
        return client;
    }

    @Bean
    public Client client2(
            @Value("${client2.name}") String name,
            @Value("${client2.city}") String city,
            @Qualifier("checkingAccount2") CheckingAccount checkingAccount2) {
        Client client = new Client(name, Client.Gender.MALE);
        client.setCity(city);
        client.addAccount(checkingAccount2);
        return client;
    }


    public static void bankReportsDemo(ApplicationContext context) {
        System.out.println("\n=== Using BankReportService ===\n");

        // Retrieve BankReportService bean from Spring context
        BankReportService reportService = context.getBean("bankReportService", BankReportService.class);

        // Use the BankReportService bean
        System.out.println("Number of clients: " + reportService.getNumberOfBankClients());
        System.out.println("Number of accounts: " + reportService.getAccountsNumber());
        System.out.println("Bank Credit Sum: " + reportService.getBankCreditSum());
    }

    public static void bankingServiceDemo(Banking banking) {

        System.out.println("\n=== Initialization using Banking implementation ===\n");

        Client anna = new Client(CLIENT_NAMES[2], Gender.FEMALE);
        anna = banking.addClient(anna);

        AbstractAccount saving = banking.createAccount(anna, SavingAccount.class);
        saving.deposit(1000);

        banking.updateAccount(anna, saving);

        AbstractAccount checking = banking.createAccount(anna, CheckingAccount.class);
        checking.deposit(3000);

        banking.updateAccount(anna, checking);

        banking.getAllAccounts(anna).stream().forEach(System.out::println);
    }

    public static void workWithExistingClients(Banking banking) {

        System.out.println("\n=======================================");
        System.out.println("\n===== Work with existing clients ======");


        Client jonny = banking.getClient(CLIENT_NAMES[0]);

        try {

            jonny.deposit(5_000);

        } catch (ActiveAccountNotSet e) {

            System.out.println(e.getMessage());

            jonny.setDefaultActiveAccountIfNotSet();
            jonny.deposit(5_000);
        }

        System.out.println(jonny);

        Client adam = banking.getClient(CLIENT_NAMES[1]);
        adam.setDefaultActiveAccountIfNotSet();

        adam.withdraw(1500);

        double balance = adam.getBalance();
        System.out.println("\n" + adam.getName() + ", current balance: " + balance);

        banking.transferMoney(jonny, adam, 1000);

        System.out.println("\n=======================================");
        banking.getClients().forEach(System.out::println);
    }
    /*
     * Method that creates a few clients and initializes them with sample values
     */
    public static Banking initialize(ApplicationContext context) {
        Banking banking = context.getBean("bankingService", Banking.class);

        // Retrieve clients from the context
        Client client1 = context.getBean("client1", Client.class);
        Client client2 = context.getBean("client2", Client.class);

        // Add clients to the banking service
        banking.addClient(client1);
        banking.addClient(client2);

        return banking;
    }
}
