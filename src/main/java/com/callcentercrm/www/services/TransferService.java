package com.callcentercrm.www.services;

import com.callcentercrm.www.entities.ReceiverTransferAccount;
import com.callcentercrm.www.entities.TradingAccount;
import com.callcentercrm.www.entities.Transfer;
import com.callcentercrm.www.enums.ProcessNames;
import com.callcentercrm.www.enums.TransferEnums;
import com.callcentercrm.www.enums.TransferStatus;
import com.callcentercrm.www.inputs.PaginationInput;
import com.callcentercrm.www.inputs.TransferInput;
import com.callcentercrm.www.repositories.ReceiverTransferAccountRepository;
import com.callcentercrm.www.repositories.TradingAccountRepository;
import com.callcentercrm.www.repositories.TransferRepository;
import com.callcentercrm.www.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
public class TransferService {

    private TransferRepository transferRepository;
    private TradingAccountRepository tradingAccountRepository;
    private ReceiverTransferAccountRepository receiverTransferAccountRepository;
    private ProcessService processService;

    @Autowired
    public TransferService(TransferRepository transferRepository, TradingAccountRepository tradingAccountRepository, ReceiverTransferAccountRepository receiverTransferAccountRepository, ProcessService processService) {
        this.transferRepository = transferRepository;
        this.tradingAccountRepository = tradingAccountRepository;
        this.receiverTransferAccountRepository = receiverTransferAccountRepository;
        this.processService = processService;
    }

    public Result<Transfer> getById(String transferId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Transfer transfer = transferRepository.findById(transferId).orElse(null);
        if(transfer == null){
            return new Result<>("Transfer not found",null);
        }
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return new Result<>("ADMIN / Transfer information",transfer);
        }
        if(authentication.getName().equals(transfer.getUserId())){

            return new Result<>("Success", transferRepository.getById(transferId));
        }else{
            return new Result<>("forbidden",null);
        }

    }

    public Result<Page<Transfer>> getAllTransfer(PaginationInput paginationInput){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(paginationInput.getPage(), paginationInput.getSize(), Sort.by(Sort.Direction.ASC, paginationInput.getSortBy()));
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new Result<>("Success", transferRepository.findAll(pageable));
        } else {
            return new Result<>("All transfers",transferRepository.findByIsDeletedFalse(pageable));
        }

    }

    @Transactional
    public Result<String> withdrawal(@RequestBody TransferInput transferInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TradingAccount tradingAccount =
                tradingAccountRepository.findById(transferInput.getTradingAccountId()).orElse(null);
        ReceiverTransferAccount receiverTransferAccount =
                receiverTransferAccountRepository.findById(transferInput.getReceiverTransferAccountId()).orElse(null);

        if (tradingAccount == null) {
            return new Result<>("Trading account not found", null);
        }

        if (receiverTransferAccount != null && !receiverTransferAccount.getCurrency().equals(tradingAccount.getCurrency())) {
            return new Result<>("Bank and trading account currencies cannot be differient", null);
        }
        if (tradingAccount.getBalance() < transferInput.getAmount()) {
            return new Result<>("transfer amount cannot bigger than trading account balance", null);
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || authentication.getName().equals(transferInput.getUserId())) {

            Transfer transfer = new Transfer();
            Date date = new Date();
            transfer.setUserId(transferInput.getUserId());
            transfer.setTransferType(TransferEnums.WITHDRAW);
            transfer.setTradingAccountId(transferInput.getTradingAccountId());
            transfer.setReceiverTransferAccountId(transferInput.getReceiverTransferAccountId());
            transfer.setAmount(transferInput.getAmount());
            transfer.setTransferStatus(TransferStatus.IN_PROGRESS);
            transfer.setCreateDate(date);
            transfer.setUpdateDate(date);
            transfer.setDeleted(false);

            tradingAccount.setUpdateDate(date);

            transferRepository.save(transfer);
            tradingAccountRepository.save(tradingAccount);
            processService.addProcess(transfer.getId(), ProcessNames.WITHDRAW,transferInput.getAmount());

            return new Result<>("Success", TransferStatus.IN_PROGRESS.toString());
        } else {
            return new Result<>("forbidden", null);
        }
    }

    @Transactional
    public Result<String> deposit(@RequestBody TransferInput transferInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TradingAccount tradingAccount = tradingAccountRepository.findById(transferInput.getTradingAccountId()).orElse(null);
        ReceiverTransferAccount receiverTransferAccount = receiverTransferAccountRepository.findById(transferInput.getReceiverTransferAccountId()).orElse(null);
        if (tradingAccount == null) {
            return new Result<>("Trading account not found", null);
        }

        if (receiverTransferAccount != null && !receiverTransferAccount.getCurrency().equals(tradingAccount.getCurrency())) {
            return new Result<>("Bank and trading account currencies cannot be differient", null);
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(transferInput.getUserId())) {

            Transfer transfer = new Transfer();
            transfer.setUserId(transferInput.getUserId());
            transfer.setTransferType(TransferEnums.DEPOSIT);
            transfer.setTradingAccountId(transferInput.getTradingAccountId());
            transfer.setReceiverTransferAccountId(transferInput.getReceiverTransferAccountId());
            transfer.setAmount(transferInput.getAmount());
            transfer.setTransferStatus(TransferStatus.IN_PROGRESS);
            transfer.setCreateDate(new Date());
            transfer.setUpdateDate(new Date());
            transfer.setDeleted(false);

            tradingAccount.setUpdateDate(new Date());

            transferRepository.save(transfer);
            tradingAccountRepository.save(tradingAccount);
            processService.addProcess(transfer.getId(), ProcessNames.DEPOSIT,transferInput.getAmount());

            return new Result<>("Success", TransferStatus.IN_PROGRESS.toString());
        } else {
            return new Result<>("forbidden", null);
        }



    }

    @Transactional
    public Result<String> updateTransfer(Transfer transferInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !authentication.getName().equals(transferInput.getUserId())) {
            return new Result<>("forbidden", null);
        }

        // transfer and trading account check!
        Transfer db_Transfer = transferRepository.findById(transferInput.getId()).orElse(null);
        TradingAccount db_tradingAccount = tradingAccountRepository.findById(transferInput.getTradingAccountId()).orElse(null);


        if (db_Transfer == null || db_tradingAccount == null) {
            return new Result<>("Transfer or trading account not found", null);
        }

        //
        if(transferInput.getReceiverTransferAccountId() != null){
            ReceiverTransferAccount receiverTransferAccount = receiverTransferAccountRepository.findById(transferInput.getReceiverTransferAccountId()).orElse(null);
            if(receiverTransferAccount == null){
                return new Result<>("Receiver transfer account not found",null);
            }
            db_Transfer.setReceiverTransferAccountId(transferInput.getReceiverTransferAccountId());
        }

        if(transferInput.getAmount() != 0){
            db_Transfer.setAmount(transferInput.getAmount());
        }

        if(db_Transfer.getTransferType().equals(TransferEnums.WITHDRAW)
                && db_tradingAccount.getBalance() >= transferInput.getAmount()){

            db_Transfer.setUpdateDate(new Date());
            processService.addProcess(db_Transfer.getId(), ProcessNames.WITHDRAW,transferInput.getAmount());
        }

        if(db_Transfer.getTransferType().equals(TransferEnums.DEPOSIT)
                && transferInput.getAmount()> 0){

            db_Transfer.setUpdateDate(new Date());
            processService.addProcess(db_Transfer.getId(), ProcessNames.DEPOSIT,transferInput.getAmount());
        }

        db_Transfer.setTransferStatus(TransferStatus.IN_PROGRESS);
        transferRepository.save(db_Transfer);

        return new Result<>("Success", TransferStatus.IN_PROGRESS.toString());


    }

}
