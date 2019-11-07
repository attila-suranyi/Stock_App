package com.codecool.stockapp.controller;

import com.codecool.stockapp.model.Currencies.CryptoCurrency;
import com.codecool.stockapp.model.Currencies.DataItem;
import com.codecool.stockapp.service.Trader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TraderController.class)
class TraderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Trader trader;

    @BeforeEach
    void setUp() {
    }

    @Test
    void showCryptoCurrenciesTest() throws Exception {

        CryptoCurrency crypto = new CryptoCurrency();
        List<DataItem> dataItem = new ArrayList<>();
        dataItem.add(new DataItem());

        crypto.setData(dataItem);

        when(trader.getCurrencies()).thenReturn(crypto);

        mvc.perform( MockMvcRequestBuilders
                .get("/")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }
}
