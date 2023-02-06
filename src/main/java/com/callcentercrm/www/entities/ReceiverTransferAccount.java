package com.callcentercrm.www.entities;

import com.callcentercrm.www.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("ReceiverTransferAccounts")
public class ReceiverTransferAccount {

    private String id;
    private String userId;
    private String name;
    private String bankName;
    private String iban;
    private Currency currency;
    private Date createDate;
    private Date updateDate;
    private boolean isDeleted;

}
