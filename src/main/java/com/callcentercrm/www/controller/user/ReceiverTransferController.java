package com.callcentercrm.www.controller.user;

import com.callcentercrm.www.entities.ReceiverTransferAccount;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.ReceiverTransferAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/receiverTransfer")
public class ReceiverTransferController {

    private ReceiverTransferAccountService receiverTransferAccountService;

    @Autowired
    public ReceiverTransferController(ReceiverTransferAccountService receiverTransferAccountService) {
        this.receiverTransferAccountService = receiverTransferAccountService;
    }

    @GetMapping("/getById")
    public Result<ReceiverTransferAccount> getById(@RequestParam String id){
        Result<ReceiverTransferAccount> result = receiverTransferAccountService.getById(id);
        return new Result<>(result.getMessage(), result.getData());
    }

    @GetMapping("/getAllAccount")
    public Result<Page<ReceiverTransferAccount>> getAllAccount(@RequestBody PaginationWithUser paginationWithUser){
        Result<Page<ReceiverTransferAccount>> result = receiverTransferAccountService.getAllAccount(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/create")
    public Result<String> createAccount(@RequestBody ReceiverTransferAccount receiverTransferAccount){
        Result<String> result = receiverTransferAccountService.createAccount(receiverTransferAccount);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/update")
    public Result<String> updateAccount(@RequestBody ReceiverTransferAccount receiverTransferInput){
        Result<String> result = receiverTransferAccountService.updateAccount(receiverTransferInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/delete")
    public Result<String> deleteAccount(@RequestParam String id){
        Result<String> result = receiverTransferAccountService.deleteAccount(id);
        return new Result<>(result.getMessage(), result.getData());
    }
}
