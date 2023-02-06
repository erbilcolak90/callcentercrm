package com.callcentercrm.www.entities;

import com.callcentercrm.www.enums.Currency;
import com.callcentercrm.www.enums.Status;
import com.callcentercrm.www.enums.TradingAccountCategory;
import com.callcentercrm.www.enums.TradingAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("TradingAccounts")
public class TradingAccount {

    private String id;
    private String userId;
    private TradingAccountType tradingAccountType;
    private TradingAccountCategory tradingAccountCategory;
    private Currency currency;
    private double balance;
    private int leverage;
    private Status status;
    private Date createDate;
    private Date updateDate;
    private boolean isDeleted;
}
