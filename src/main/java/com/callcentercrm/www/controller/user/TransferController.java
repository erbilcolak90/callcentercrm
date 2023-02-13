package com.callcentercrm.www.controller.user;

import com.callcentercrm.www.entities.Transfer;
import com.callcentercrm.www.inputs.PaginationInput;
import com.callcentercrm.www.inputs.TransferInput;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.TransferService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/user/transfer")
@ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
public class TransferController {

    private TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @GetMapping("/getById")
    public Result<Transfer> getById(@RequestParam String transferId){
        Result<Transfer> result = transferService.getById(transferId);
        return new Result<>(result.getMessage(),result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @GetMapping("/getAll")
    public Result<Page<Transfer>> getAllTransfer(PaginationInput paginationInput){
        Result<Page<Transfer>> result = transferService.getAllTransfer(paginationInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/withdrawal")
    public Result<String> withdrawal(@RequestBody TransferInput transferInput) {
        Result<String> result = transferService.withdrawal(transferInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/deposit")
    public Result<String> deposit(@RequestBody TransferInput transferInput) {
        Result<String> result = transferService.deposit(transferInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/update")
    public Result<String> updateTransfer(@RequestBody Transfer transfer) {
        Result<String> result = transferService.updateTransfer(transfer);
        return new Result<>(result.getMessage(), result.getData());
    }

}
