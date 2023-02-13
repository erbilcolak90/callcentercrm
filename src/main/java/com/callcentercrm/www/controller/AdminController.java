package com.callcentercrm.www.controller;

import com.callcentercrm.www.entities.Process;
import com.callcentercrm.www.entities.TradingAccount;
import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.entities.UserDocument;
import com.callcentercrm.www.inputs.PaginationInput;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin")
public class AdminController {

    private CustomUserService customUserService;
    private UserDocumentService userDocumentService;
    private TradingAccountService tradingAccountService;
    private ProcessService processService;
    private TransferService transferService;
    private ReceiverTransferAccountService receiverTransferAccountService;

    @Autowired
    public AdminController(CustomUserService customUserService, UserDocumentService userDocumentService, TradingAccountService tradingAccountService, ProcessService processService, TransferService transferService, ReceiverTransferAccountService receiverTransferAccountService) {
        this.customUserService = customUserService;
        this.userDocumentService = userDocumentService;
        this.tradingAccountService = tradingAccountService;
        this.processService = processService;
        this.transferService = transferService;
        this.receiverTransferAccountService = receiverTransferAccountService;
    }

    @PostMapping("/addRoleToUser")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addRoleToUser(@RequestParam String userId, @RequestParam String rolename, HttpServletRequest request) {
        Result<String> result = customUserService.addRoleToUser(userId, rolename);
        return new Result<>(result.getMessage(), result.getData());
    }


    // User Operations

    @PostMapping("/getUser")
    public Result<User> getUserById(@RequestParam String userId) {
        Result<User> resultUser = customUserService.getUserById(userId);
        return new Result<>(resultUser.getMessage(), resultUser.getData());
    }

    @PostMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getAllUsers(@RequestBody PaginationInput paginationInput) {
        Result<Page<User>> result = customUserService.getAllUsers(paginationInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    // Document Operations

    @GetMapping("/getDocumentById")
    public Result<UserDocument> getDocumentById(@RequestParam String documentId) {
        Result<UserDocument> result = userDocumentService.getDocumentById(documentId);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllDocumentByUser")
    public Result<Page<UserDocument>> getAllDocumentByUser(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<UserDocument>> result = userDocumentService.getAllDocumentByUser(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllDocuments")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<UserDocument>> getAllUserDocuments(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<UserDocument>> result = userDocumentService.getAllDocumentByUser(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    // Trading Account Operations

    @PostMapping("/deleteTradingAccount")
    public Result<String> deleteTradingAccount(String tradingAccountId) {
        Result<String> result = tradingAccountService.deleteTradingAccount(tradingAccountId);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllTradingAccount")
    public Result<Page<TradingAccount>> getAllTradingAccount(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<TradingAccount>> result = tradingAccountService.getAllTradingAccount(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }


    // process operations

    @PostMapping("/approveTransfer")
    public Result<Process> approveTransfer(@RequestParam String processId) {
        Result<Process> result = processService.approveTransfer(processId);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/rejectTransfer")
    public Result<String> rejectTransfer(@RequestParam String processId, @RequestParam String reasonForDisapproval) {
        Result<String> result = processService.rejectTransfer(processId, reasonForDisapproval);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/downloadDocument")
    public byte[] downloadFile(@RequestParam String fileName) throws IOException{
        return processService.downloadFile(fileName);
    }

    @PostMapping("/approveDocument")
    public Result<String> approveUserDocument(@RequestParam String documentId) {
        Result<String> result = processService.approveUserDocument(documentId);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/rejectDocument")
    public Result<String> rejectUserDocument(@RequestParam String documentId, @RequestParam String reasonForDisapproval) {
        Result<String> result = processService.rejectUserDocument(documentId, reasonForDisapproval);
        return new Result<>(result.getMessage(), result.getData());
    }

    @GetMapping("/getProcess")
    public Result<Process> getProcess(@RequestParam String id) {

        Result<Process> result = processService.getProcess(id);

        return new Result<>("", result.getData());

    }

    @PostMapping("/getAllProcessByAdmin")
    public Result<Page<Process>> getAllProcessByAdmin(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<Process>> result = processService.getAllProcessByAdmin(paginationWithUser);

        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllDocumentProcess")
    public Result<Page<Process>> getAllDocumentProcess(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<Process>> result = processService.getAllDocumentProcess(paginationWithUser);

        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllWithdrawProcess")
    public Result<Page<Process>> getAllWithdrawProcess(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<Process>> result = processService.getAllWithdrawProcess(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllDepositProcess")
    public Result<Page<Process>> getAllDepositProcess(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<Process>> result = processService.getAllDepositProcess(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    @PostMapping("/getAllProcessByUser")
    public Result<Page<Process>> getAllProcessByUser(PaginationWithUser paginationWithUser){
        Result<Page<Process>> result = processService.getAllProcessByUser(paginationWithUser);

        return new Result<>(result.getMessage(), result.getData());
    }
}
