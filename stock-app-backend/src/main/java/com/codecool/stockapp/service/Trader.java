package com.codecool.stockapp.service;

import com.codecool.stockapp.model.Util;
import com.codecool.stockapp.model.entity.currency.CryptoCurrency;
import com.codecool.stockapp.model.entity.currency.CurrencyDetails;
import com.codecool.stockapp.model.entity.currency.SingleCurrency;
import com.codecool.stockapp.model.entity.transaction.Transaction;
import com.codecool.stockapp.model.entity.transaction.TransactionType;
import com.codecool.stockapp.model.repository.TransactionRepository;
import com.codecool.stockapp.model.repository.UserRepository;
import com.codecool.stockapp.service.api.CurrencyAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class Trader {

    @Autowired
    private CurrencyAPIService currencyAPIService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Trader() {
    }

    //TODO gives back boolean, return the value to the frontend and rename this method according to this
    @Transactional
    public boolean buy(Transaction transaction, long userId) {
        transaction.setUser(userRepository.findById(userId));

        if (checkBalance(transaction)) {
            if (this.isTransactionExecutable(transaction)) {
                this.saveTransactionWithDetails(transaction, true);
            } else {
                this.saveTransactionWithDetails(transaction, false);
            }
            System.out.println(transactionRepository.findAll());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean sell(Transaction transaction, long userId) {
        transaction.setUser(userRepository.findById(userId));
        this.saveTransactionWithDetails(transaction, true);
        System.out.println(transactionRepository.findAll());
        return true;
    }

    private void saveTransactionWithDetails(Transaction transaction, boolean isTransactionClosed) {
        transaction.setDate(Util.getCurrentDate());
        transaction.setClosedTransaction(isTransactionClosed);
        transactionRepository.saveAndFlush(transaction);

        this.modifyUserBalanceByTransactionTotal(transaction);
    }


    private void modifyUserBalanceByTransactionTotal(Transaction transaction) {
        double balance;

        if (transaction.getTransactionType().equals(TransactionType.BUY)) {
            balance = transaction.getUser().getBalance() - transaction.getTotal();
        } else {
            balance = transaction.getUser().getBalance() + transaction.getTotal();
        }
        userRepository.updateBalance(balance, transaction.getUser().getId());
    }

    public boolean isTransactionExecutable(Transaction transaction) {
        double currentPrice = currencyAPIService.getSingleCurrencyPrice(transaction.getCurrencyId());

        if (transaction.getTransactionType().equals(TransactionType.BUY)) {
            return transaction.getPrice() >= currentPrice;
        } else if (transaction.getTransactionType().equals(TransactionType.SELL)) {
            return transaction.getPrice() <= currentPrice;
        }
        return false;
    }

    //TODO review this snippet
    public void scanOpenOrders() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {

                    @Autowired
                    Trader trader;

                    @Override
                    public void run() {
                        List<Transaction> openTransactions = transactionRepository.findAllByClosedTransactionFalse();
                        openTransactions.forEach( transaction -> {
                            if (this.trader.isTransactionExecutable(transaction)) {
                                transactionRepository.closeTransaction(transaction.getId());
                            }
                        });
                        scanOpenOrders();
                    }
                },
                60000
        );
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public CryptoCurrency getCurrencies(String sortBy, String sortDir) {
        return currencyAPIService.getCurrencies(sortBy, sortDir);
    }

    public CurrencyDetails getCurrencyById(long id) {
        SingleCurrency currency = currencyAPIService.getSingleCurrency(id);
        return currency.getData().get(id);
    }

    private boolean checkBalance(Transaction transaction) {
        return (transaction.getTotal() < transaction.getUser().getBalance());
    }

    //TODO delete custom open order
    public List<Transaction> getOpenTransactions(Long userId) {
        Transaction openTransaction = Transaction.builder()
                .closedTransaction(false)
                .price(150.0)
                .symbol("BTC")
                .currencyId((long) 1)
                .amount(1.0)
                .total(150.0)
                .user(userRepository.findById(1))
                .date("2015-10-05")
                .transactionType(TransactionType.BUY)
                .build();

        transactionRepository.saveAndFlush(openTransaction);

        return transactionRepository.getOpenTransactionsByUserId(userId);
    }
}
