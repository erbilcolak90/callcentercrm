package com.callcentercrm.www.entities;

import com.callcentercrm.www.enums.TransferEnums;
import com.callcentercrm.www.enums.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("Transfers")
public class Transfer {

    private String id;
    private String userId;
    private TransferEnums transferType;
    private String tradingAccountId;
    private double amount;
    private String receiverTransferAccountId;
    private TransferStatus transferStatus;
    private Date createDate;
    private Date updateDate;
    private boolean isDeleted;

}
