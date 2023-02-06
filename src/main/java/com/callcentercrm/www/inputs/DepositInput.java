package com.callcentercrm.www.inputs;

import com.callcentercrm.www.entities.ReceiverTransferAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepositInput {

    private String id;
    private String userId;
    private String tradingAccountId;
    private double amount;
    private ReceiverTransferAccount receiverTransferAccount;
}
