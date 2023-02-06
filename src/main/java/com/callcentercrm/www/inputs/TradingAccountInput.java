package com.callcentercrm.www.inputs;

import com.callcentercrm.www.enums.Currency;
import com.callcentercrm.www.enums.TradingAccountCategory;
import com.callcentercrm.www.enums.TradingAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradingAccountInput {

    private String id;
    private String userId;
    private TradingAccountType tradingAccountType;
    private TradingAccountCategory tradingAccountCategory;
    private Currency currency;
    private double balance;
    private int leverage;
}
