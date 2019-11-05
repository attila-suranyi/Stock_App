package com.codecool.stockapp.service;

import com.codecool.stockapp.model.CryptoCurrency;
import com.codecool.stockapp.model.DataItem;
import com.codecool.stockapp.service.api.CurrencyAPIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.stream.Stream;

@Service
public class Trader {

    @Autowired
    CurrencyAPIService currencyAPIService;

    public CryptoCurrency getCurrencies() {
        return currencyAPIService.getCurrencies();
    }
    public Stream<DataItem> getCurrency(String symbol) {
        return currencyAPIService.getCurrencies().getData().stream().filter(x -> x.getSymbol().equals(symbol));
    }

    public CryptoCurrency getSortedCurrencies(String sortBy) {
        return currencyAPIService.getSortedCurrencies(sortBy);
    }
}
