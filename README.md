# JTA Demo

Simple Jmix application able to use distributed transactions with Atomikos.


# Setting up JTA with Jmix

## Set up transaction manager

- Create JmixJtaTransactionController bean
```
@Bean
    JmixJtaTransactionController jtaTransactionController() {
        return new JmixJtaTransactionController();
    }
```
- Create UserTransaction and AtomikosTransactionManager beans
```
    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
     ...
        return userTransactionImp;
    }

    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public TransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
    ...
        return userTransactionManager;
    }
```
- Create PlatformTransactionManager bean using JmixEclipselinkJtaTransactionManager class
```
    @Bean(name = {"transactionManager", "ordersTransactionManager"})
    @DependsOn({ "userTransaction", "atomikosTransactionManager" })
    public PlatformTransactionManager transactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();
        TransactionManager atomikosTransactionManager = atomikosTransactionManager();
        return new JmixEclipselinkJtaTransactionManager("transactionManager", userTransaction, atomikosTransactionManager);
    }
```
Note that this bean must have a name for each datastore you want to use with it in format `%storeName%TransactionManager` or `transactionManager` for main datastore.
## Set up data stores

- Create DataSource bean using XADataSource for target database and AtomikosDataSourceBean
``` 
    @Bean
    @ConfigurationProperties("orders.datasource")
    DataSourceProperties ordersDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Bean(name = "ordersDataSource")
    public DataSource ordersDataSource(@Qualifier("ordersDataSourceProperties") DataSourceProperties dataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setURL(dsConfig.getJdbcUrl());
        ds.setUser(dsConfig.getUsername());
        ds.setPassword(dsConfig.getPassword());
    ...
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(ds);
    ...
        return atomikosDataSourceBean;
    }
```

- Create EntityManagerFactory bean using JmixJtaEntityManagerFactoryBean and set JTA DataSource for it
```
    @Bean
    @DependsOn({"ordersTransactionManager", "ordersDataSource"})
    public LocalContainerEntityManagerFactoryBean ordersEntityManagerFactory(@Qualifier("ordersDataSource") DataSource ordersDataSource,
                                                                             JpaVendorAdapter jpaVendorAdapter,
                                                                             DbmsSpecifics dbmsSpecifics,
                                                                             JmixModules jmixModules,
                                                                             Resources resources) {
        return new JmixJtaEntityManagerFactoryBean("orders",
                ordersDataSource,
                jpaVendorAdapter,
                dbmsSpecifics,
                jmixModules,
                resources);
    }
```

## Set up database
Note that database you are using must be able to work with `XADataSource`.
For example, if you use `Postgres` you should set `max_prepared_transactions` property. Use the following steps:
- Stop `Postgres` service.
- Set `max_prepared_transactions` property to a nonzero value (e.g. 5000) in the `postgresql.conf` file.
- Start `Postgres` service

## Sample project description
There are 3 data stores in the project. 
- "Main" data store and "orders" store belong to global transaction manager. 
  "Main" store manages `Customer` and `User` entities. 
- Data store "orders" manages "Order" entity. 
- Third data store "local" does not belong to the global transaction manager and manages `LocalEntity` entity.

Project also has a basic test with successful transaction and rollback: `com.company.jtatest.GlobalTxTest` 
To run it, it is better to set connection parameters to test databases in the test app properties, since some tables are cleared during tests.

There are two special buttons on the Order browser screen:
- The "Create test order and customer" button creates programmatically the Order entity, Customer entity and LocalEntity entity then saves them in same SaveContext via dataManager. 
- Button "Invoke Distributed Transaction Service" executes the CompositeJTAService service for selected in table Order instance and associated Customer, where you can see an example of declarative use of transactions.
