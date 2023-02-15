package com.callcentercrm.www.services;

import com.callcentercrm.www.entities.*;
import com.callcentercrm.www.entities.Process;
import com.callcentercrm.www.enums.*;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.repositories.ProcessRepository;
import com.callcentercrm.www.repositories.TradingAccountRepository;
import com.callcentercrm.www.repositories.TransferRepository;
import com.callcentercrm.www.repositories.UserDocumentRepository;
import com.callcentercrm.www.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;

@Service
public class ProcessService {

    private ProcessRepository processRepository;
    private CustomUserService customUserService;
    private TransferRepository transferRepository;
    private TradingAccountRepository tradingAccountRepository;
    private UserDocumentRepository userDocumentRepository;
    private StorageService storageService;


    @Autowired
    public ProcessService(ProcessRepository processRepository, CustomUserService customUserService, TransferRepository transferRepository, TradingAccountRepository tradingAccountRepository, UserDocumentRepository userDocumentRepository, StorageService storageService) {
        this.processRepository = processRepository;
        this.customUserService = customUserService;
        this.transferRepository = transferRepository;
        this.tradingAccountRepository = tradingAccountRepository;
        this.userDocumentRepository = userDocumentRepository;
        this.storageService = storageService;
    }

    @Transactional
    public void addProcess(String operationId, ProcessNames processNames, Object data) {
        Process process = new Process();
        Date date = new Date();
        process.setProcessName(processNames);
        String admin = customUserService.shuffleAdmin();
        process.setInspectorId(admin);
        process.setOperationId(operationId);
        process.setData(data);
        process.setStatus(Status.PENDING);
        process.setCreateDate(date);
        process.setUpdateDate(date);
        process.setDeleted(false);

        processRepository.save(process);

    }

    @Transactional
    public Result<Process> approveTransfer(String processId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Process db_process = processRepository.findById(processId).orElse(null);

        if (db_process == null) {
            return new Result<>("Process not found", null,false);
        }
        if (!authentication.getName().equals(db_process.getInspectorId())) {
            return new Result<>("forbidden admin", null,false);
        }

        Transfer db_transfer = transferRepository.findById(db_process.getOperationId()).orElse(null);
        if (db_transfer == null) {
            return new Result<>("Transfer not found", null,false);
        }

        if (db_process.getProcessName().equals(ProcessNames.WITHDRAW) || db_process.getProcessName().equals(ProcessNames.DEPOSIT)) {
            TradingAccount db_tradingAccount = tradingAccountRepository.findById(db_transfer.getTradingAccountId()).orElse(null);

            if(db_tradingAccount ==null ){
                return new Result<>("Trading account not found",null,false);
            }

            Date date = new Date();

            if (db_process.getProcessName().equals(ProcessNames.DEPOSIT)) {
                double amount = (double) db_process.getData();
                db_tradingAccount.setBalance(db_tradingAccount.getBalance() + amount);
            } else {
                double amount = (double) db_process.getData();
                db_tradingAccount.setBalance(db_tradingAccount.getBalance() - amount);
            }
            db_transfer.setTransferStatus(TransferStatus.DONE);
            db_transfer.setUpdateDate(date);
            db_tradingAccount.setUpdateDate(date);
            db_process.setStatus(Status.APPROVED);
            db_process.setUpdateDate(date);
            transferRepository.save(db_transfer);
            processRepository.save(db_process);
            tradingAccountRepository.save(db_tradingAccount);

            return new Result<>("Success", db_process,true);
        } else {
            return new Result<>("This is not transfer process", null,false);
        }
    }

    @Transactional
    public Result<String> rejectTransfer(String processId,String reasonForReject){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Process db_process = processRepository.findById(processId).orElse(null);
        Transfer db_transfer = transferRepository.findById(db_process.getOperationId()).orElse(null);
        if (db_process == null) {
            return new Result<>("Process not found", null,false);
        }

        if (authentication.getName().equals(db_process.getInspectorId())){
            db_process.setStatus(Status.REJECT);
            Date date = new Date();
            db_transfer.setTransferStatus(TransferStatus.REFUSED);

            db_transfer.setUpdateDate(date);
            db_process.setUpdateDate(date);
            db_process.setData(reasonForReject);
            processRepository.save(db_process);
            transferRepository.save(db_transfer);

            return new Result<>("Transfer rejected", reasonForReject,true);
        }else{
            return new Result<>("Forbidden admin",null,false);
        }

    }

    @Transactional
    public byte[] downloadFile(String fileName) throws IOException{
        return storageService.downloadFile(fileName);
    }

    @Transactional
    public Result<String> approveUserDocument(String processId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Process process = processRepository.findById(processId).orElse(null);

        if (process == null) {
            return new Result<>("Process not found", null,false);
        }

        if (authentication.getName().equals(process.getInspectorId())) {
            UserDocument db_userDocument = userDocumentRepository.findById(process.getOperationId()).orElse(null);
            if (db_userDocument != null) {
                db_userDocument.setStatus(Status.APPROVED);
                db_userDocument.setUpdateDate(new Date());

                process.setStatus(Status.APPROVED);
                process.setUpdateDate(new Date());
                userDocumentRepository.save(db_userDocument);
                processRepository.save(process);

                return new Result<>("Success", db_userDocument.getId(), true);

            } else {
                return new Result<>("document is not found", null, false);
            }
        } else {
            return new Result<>("This process not your confirm list", null, false);
        }

    }
    @Transactional
    public Result<String> rejectUserDocument(String processId, String reasonForReject){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Process db_process = processRepository.findById(processId).orElse(null);
        UserDocument db_UserDocument = userDocumentRepository.findById(db_process.getOperationId()).orElse(null);

        if (authentication.getName().equals(db_process.getInspectorId())){
            db_process.setStatus(Status.REJECT);
            Date date = new Date();
            db_UserDocument.setStatus(Status.REJECT);
            db_UserDocument.setStatusMessage(ReasonForReject.valueOf(reasonForReject).toString());

            db_process.setUpdateDate(date);
            db_UserDocument.setUpdateDate(date);

            processRepository.save(db_process);
            userDocumentRepository.save(db_UserDocument);

            return new Result<>("Document rejected",reasonForReject,true);
        }
        else{
            return new Result<>("Forbidden admin",null, false);
        }



    }

    public Result<Process> getProcess(String processId) {

        return new Result<>("", processRepository.findById(processId).orElse(null), true);
    }
    public Result<Page<Process>> getAllProcessByAdmin(PaginationWithUser paginationWithUser) {

        Result<Page<Process>> result = getAllProcess(null,paginationWithUser);

        return new Result<>(result.getMessage(), result.getData(), true);

    }

    public Result<Page<Process>> getAllProcessByUser(PaginationWithUser paginationWithUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(paginationWithUser.getPage(), paginationWithUser.getSize(), Sort.by(Sort.Direction.valueOf(paginationWithUser.getSortType().toString()), paginationWithUser.getFieldName()));
        User user = customUserService.findByUserId(paginationWithUser.getUserId());

        // get process user with process type
        if(!user.getRoles().contains(Role.ADMIN) && !user.getId().equals(authentication.getName())){
            return new Result<>("Success", processRepository.findAll(paginationWithUser.getUserId(), pageable), true);
        }
        else{
            return new Result<>("Forbidden",null, false);
        }

    }

    public Result<Page<Process>> getAllDocumentProcess(PaginationWithUser paginationWithUser){

        Result<Page<Process>> result = getAllProcess(ProcessNames.USER_DOCUMENT,paginationWithUser);

        return new Result<>(result.getMessage(), result.getData(), true);

    }

    public Result<Page<Process>> getAllWithdrawProcess(PaginationWithUser paginationWithUser){

        Result<Page<Process>> result = getAllProcess(ProcessNames.WITHDRAW,paginationWithUser);

        return new Result<>(result.getMessage(), result.getData(), true);

    }

    public Result<Page<Process>> getAllDepositProcess(PaginationWithUser paginationWithUser){

        Result<Page<Process>> result = getAllProcess(ProcessNames.DEPOSIT,paginationWithUser);

        return new Result<>(result.getMessage(), result.getData(), true);

    }

    public Result<Page<Process>> getAllProcess(ProcessNames processType,PaginationWithUser paginationWithUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(paginationWithUser.getPage(), paginationWithUser.getSize(), Sort.by(Sort.Direction.valueOf(paginationWithUser.getSortType().toString()), paginationWithUser.getFieldName()));
        User user = customUserService.findByUserId(paginationWithUser.getUserId());

        // get processes admin at authentication without process type
        if(processType == null && authentication.getName().equals(paginationWithUser.getUserId())){
            return new Result<>("Success", processRepository.findAll(paginationWithUser.getUserId(), pageable), true);
        }

        // get processes admin at authentication with process type
        assert processType != null;
        if(paginationWithUser.getUserId().equals(authentication.getName())){

            return new Result<>("Success", processRepository.findAll(paginationWithUser.getUserId(),processType.toString(), pageable), true);
        }

        else{
            return new Result<>("You cannot see other admins process",null, false);
        }
    }

}
