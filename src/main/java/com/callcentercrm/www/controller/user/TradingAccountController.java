package com.callcentercrm.www.controller.user;

import com.callcentercrm.www.entities.TradingAccount;
import com.callcentercrm.www.inputs.PaginationInput;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.inputs.TradingAccountInput;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.TradingAccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/tradingAccount")
public class TradingAccountController {

    private TradingAccountService tradingAccountService;

    @Autowired
    public TradingAccountController(TradingAccountService tradingAccountService) {
        this.tradingAccountService = tradingAccountService;
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @GetMapping("/getById")
    public Result<TradingAccount> getById(@RequestParam String id){
        Result<TradingAccount> result = tradingAccountService.getById(id);
        return new Result<>(result.getMessage(),result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/getAll")
    public Result<Page<TradingAccount>> getAllTradingAccount(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<TradingAccount>> result = tradingAccountService.getAllTradingAccount(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/create")
    public Result<String> createTradingAccount(@RequestBody TradingAccountInput tradingAccountInput) {
        Result<String> result = tradingAccountService.createTradingAccount(tradingAccountInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/update")
    public Result<String> updateTradingAccount(@RequestBody TradingAccountInput tradingAccountInput) {
        Result<String> result = tradingAccountService.updateTradingAccount(tradingAccountInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = { @Authorization(value="JWT") })
    @PostMapping("/delete")
    public Result<String> deleteTradingAccount(@RequestParam String tradingAccountId) {
        Result<String> result = tradingAccountService.deleteTradingAccount(tradingAccountId);
        return new Result<>(result.getMessage(), result.getData());
    }



}
