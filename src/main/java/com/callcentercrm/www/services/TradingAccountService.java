package com.callcentercrm.www.services;

import com.callcentercrm.www.entities.TradingAccount;
import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.enums.ProcessNames;
import com.callcentercrm.www.enums.Status;
import com.callcentercrm.www.enums.TradingAccountCategory;
import com.callcentercrm.www.enums.TradingAccountType;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.inputs.TradingAccountInput;
import com.callcentercrm.www.repositories.TradingAccountRepository;
import com.callcentercrm.www.repositories.UserRepository;
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

import java.util.Arrays;
import java.util.Date;

@Service
public class TradingAccountService {

    private TradingAccountRepository tradingAccountRepository;
    private UserRepository userRepository;
    private ProcessService processService;

    @Autowired
    public TradingAccountService(TradingAccountRepository tradingAccountRepository, UserRepository userRepository, ProcessService processService) {
        this.tradingAccountRepository = tradingAccountRepository;
        this.userRepository = userRepository;
        this.processService = processService;
    }

    public Result<TradingAccount> getById(String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new Result<>("Success", tradingAccountRepository.findById(id).orElse(null), true);
        } else {
            return new Result<>("Trading Account information : ", tradingAccountRepository.getById(id), true);
        }

    }

    @Transactional
    public Result<Page<TradingAccount>> getAllTradingAccount(PaginationWithUser paginationWithUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(paginationWithUser.getPage(), paginationWithUser.getSize(), Sort.by(Sort.Direction.valueOf(paginationWithUser.getSortType().toString()), paginationWithUser.getFieldName()));
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new Result<>("Success", tradingAccountRepository.findAll(paginationWithUser.getUserId(),pageable), true);
        } else {
            return new Result<>("Success", tradingAccountRepository.findByIsDeletedFalse(authentication.getName(), pageable), true);
        }
    }

    @Transactional
    public Result<String> createTradingAccount(@RequestBody TradingAccountInput tradingAccountInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (tradingAccountInput.getUserId() == null || tradingAccountInput.getUserId().isEmpty()) {
            return new Result<>("user id is not available", null, false);
        }

        TradingAccount tradingAccount = new TradingAccount();

        //for admin
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            createTradingAccount(tradingAccountInput, tradingAccount);

            tradingAccountRepository.save(tradingAccount);
            processService.addProcess(tradingAccount.getId(), ProcessNames.TRADING_ACCOUNT,"Create Trading Account");
            return new Result<>("Trading account waiting for approve", tradingAccount.getId(), true);
        }

        // for user
        User user = userRepository.findById(authentication.getName()).orElse(null);
        if (user != null && user.getStatus().equals(Status.APPROVED) && authentication.getName().equals(tradingAccountInput.getUserId())) {
            createTradingAccount(tradingAccountInput, tradingAccount);
            tradingAccountRepository.save(tradingAccount);
            processService.addProcess(tradingAccount.getId(), ProcessNames.TRADING_ACCOUNT,"Create Trading Account");

            return new Result<>("Trading account waiting for approve", tradingAccount.getId(), true);
        } else {
            return new Result<>("forbidden", null, false);
        }
    }

    @Transactional
    public Result<String> updateTradingAccount(@RequestBody TradingAccountInput tradingAccountInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (tradingAccountInput.getUserId() == null || tradingAccountInput.getUserId().isEmpty() || tradingAccountInput.getId() == null || tradingAccountInput.getId().isEmpty()) {
            return new Result<>("user id or trading account id cannot be empty or null", null, false);
        }

        TradingAccount tradingAccount = tradingAccountRepository.findById(tradingAccountInput.getId()).orElseThrow();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            tradingAccount.setTradingAccountType(tradingAccountInput.getTradingAccountType() != null ?
                    tradingAccountInput.getTradingAccountType() : tradingAccount.getTradingAccountType());
            tradingAccount.setBalance(tradingAccountInput.getBalance() > 0 ? tradingAccountInput.getBalance() : tradingAccount.getBalance());
            tradingAccount.setLeverage(tradingAccountInput.getLeverage() >0 ? tradingAccountInput.getLeverage() : tradingAccount.getLeverage());
        }


        if (!authentication.getName().equals(tradingAccountInput.getUserId())) {
            return new Result<>("forbidden", null, false);
        }

        tradingAccount.setStatus(Status.PENDING);
        tradingAccount.setTradingAccountCategory(tradingAccountInput.getTradingAccountCategory() != null ? tradingAccountInput.getTradingAccountCategory() : tradingAccount.getTradingAccountCategory());
        tradingAccount.setCurrency(tradingAccountInput.getCurrency() != null ? tradingAccountInput.getCurrency() :tradingAccount.getCurrency());
        tradingAccount.setUpdateDate(new Date());
        tradingAccountRepository.save(tradingAccount);
        processService.addProcess(tradingAccount.getId(), ProcessNames.TRADING_ACCOUNT,"Update Trading Account");

        return new Result<>("Waiting for approve", tradingAccount.getId(), true);
    }

    @Transactional
    public Result<String> deleteTradingAccount(String tradingAccountId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TradingAccount tradingAccount = tradingAccountRepository.findById(tradingAccountId).orElse(null);
        if (tradingAccount == null) {
            return new Result<>("Success", null, false);
        }
        if (authentication.getName().equals(tradingAccount.getUserId()) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            tradingAccount.setDeleted(true);
            tradingAccount.setUpdateDate(new Date());
            tradingAccountRepository.save(tradingAccount);

            return new Result<>("Success", tradingAccountId, true);
        } else {
            return new Result<>("forbidden", null, true);
        }

    }

    public void createTradingAccount(TradingAccountInput tradingAccountInput, TradingAccount tradingAccount) {
        String tradingAccountType = tradingAccountInput.getTradingAccountType().toString().toUpperCase();
        TradingAccountType upperCaseTradingAccountEnum = TradingAccountType.valueOf(tradingAccountType);

        String tradingAccountCategory = tradingAccountInput.getTradingAccountCategory().toString().toUpperCase();

        tradingAccount.setUserId(tradingAccountInput.getUserId());
        tradingAccount.setBalance(tradingAccountInput.getBalance());
        tradingAccount.setCurrency(tradingAccountInput.getCurrency());
        tradingAccount.setLeverage(tradingAccountInput.getLeverage());
        tradingAccount.setStatus(Status.PENDING);
        tradingAccount.setCreateDate(new Date());
        tradingAccount.setUpdateDate(new Date());
        tradingAccount.setDeleted(false);
        if (Arrays.stream(TradingAccountType.values()).toList().contains(upperCaseTradingAccountEnum)) {
            tradingAccount.setTradingAccountType(tradingAccountInput.getTradingAccountType());
            tradingAccount.setTradingAccountCategory(tradingAccountInput.getTradingAccountCategory());

        } else {
            tradingAccount.setTradingAccountType(TradingAccountType.DEMO_ACCOUNT);
            tradingAccount.setTradingAccountCategory(TradingAccountCategory.STANDARD);
        }
    }

}
