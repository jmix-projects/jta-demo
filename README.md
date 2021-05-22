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
    @Bean(name = "ordersDataSource")
    @Qualifier("orders")
    public DataSource ordersDataSource() {
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
    public LocalContainerEntityManagerFactoryBean ordersEntityManagerFactory(@Qualifier("orders") DataSource ordersDataSource,
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