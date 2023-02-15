package com.callcentercrm.www.services;

import com.callcentercrm.www.entities.ReceiverTransferAccount;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.repositories.ReceiverTransferAccountRepository;
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

import java.util.Date;

@Service
public class ReceiverTransferAccountService {

    private final ReceiverTransferAccountRepository receiverTransferAccountRepository;

    @Autowired
    public ReceiverTransferAccountService(ReceiverTransferAccountRepository receiverTransferAccountRepository) {
        this.receiverTransferAccountRepository = receiverTransferAccountRepository;
    }

    public Result<ReceiverTransferAccount> getById(String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ReceiverTransferAccount receiverTransferAccount = receiverTransferAccountRepository.findById(id).orElse(null);
        if (receiverTransferAccount != null && (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(receiverTransferAccount.getUserId()))){
            return new Result<>("Success", receiverTransferAccount, true);
        } else {
            return new Result<>("forbidden ",null, false);
        }

    }

    public Result<Page<ReceiverTransferAccount>> getAllAccount(PaginationWithUser paginationWithUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(paginationWithUser.getPage(), paginationWithUser.getSize(), Sort.by(Sort.Direction.valueOf(paginationWithUser.getSortType().toString()), paginationWithUser.getFieldName()));
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return new Result<>("Success", receiverTransferAccountRepository.findById(paginationWithUser.getUserId(),pageable), true);
        }
        if(authentication.getName().equals(paginationWithUser.getUserId())){
            return new Result<>("Success",receiverTransferAccountRepository.findByIsDeletedFalse(paginationWithUser.getUserId(), pageable), true);
        }
        else{
            return new Result<>("Forbidden",null, false);
        }
    }

    public Result<String> createAccount(ReceiverTransferAccount receiverTransferAccount){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(receiverTransferAccount.getUserId() == null ||
                    receiverTransferAccount.getName() == null ||
                    receiverTransferAccount.getBankName() == null ||
                    receiverTransferAccount.getIban() == null){
            return new Result<>("Account cannot be empty",null, false);
        }

        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(receiverTransferAccount.getUserId())){
                ReceiverTransferAccount db_receiverTransferAccount = new ReceiverTransferAccount();
                db_receiverTransferAccount.setUserId(receiverTransferAccount.getUserId());
                db_receiverTransferAccount.setName(receiverTransferAccount.getName());
                db_receiverTransferAccount.setBankName(receiverTransferAccount.getBankName());
                db_receiverTransferAccount.setIban(receiverTransferAccount.getIban());
                db_receiverTransferAccount.setCurrency(receiverTransferAccount.getCurrency());
                receiverTransferAccountRepository.save(db_receiverTransferAccount);

                return new Result<>("Account created", db_receiverTransferAccount.getId(), true);
        }
        else {
            return new Result<>("forbidden",null, false);
        }
    }

    public Result<String> updateAccount(ReceiverTransferAccount receiverTransferInput){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ReceiverTransferAccount receiverTransferAccount = receiverTransferAccountRepository.findById(receiverTransferInput.getId()).orElse(null);
        if(receiverTransferAccount == null){
            return new Result<>("Account not found ", null, false);
        }
        if(!receiverTransferInput.getUserId().equals(receiverTransferAccount.getUserId())){
            return new Result<>("user cannot change",null , false);
        }
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(receiverTransferAccount.getUserId())){

              if(receiverTransferInput.getCurrency() != null ){
                  receiverTransferAccount.setCurrency(receiverTransferInput.getCurrency());
              }
              if(receiverTransferInput.getIban() != null){
                  receiverTransferAccount.setIban(receiverTransferInput.getIban());
              }
              if(receiverTransferInput.getBankName() != null){
                  receiverTransferAccount.setBankName(receiverTransferAccount.getBankName());
              }
              if(receiverTransferInput.getName()!= null){
                  receiverTransferAccount.setName(receiverTransferInput.getName());
              }
              receiverTransferAccount.setUpdateDate(new Date());

              receiverTransferAccountRepository.save(receiverTransferAccount);

              return new Result<>("Success", null, true);
        }
        else{
            return new Result<>("Forbidden",null, false);
        }
    }

    public Result<String> deleteAccount(String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ReceiverTransferAccount receiverTransferAccount = receiverTransferAccountRepository.findById(id).orElse(null);
        if(receiverTransferAccount == null){
            return new Result<>("Account Not Found",null, false);
        }
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(receiverTransferAccount.getUserId())){

            receiverTransferAccount.setDeleted(true);
            receiverTransferAccount.setUpdateDate(new Date());
            receiverTransferAccountRepository.save(receiverTransferAccount);

            return new Result<>("Account deleted", null, true);
        }
        else{
            return new Result<>("Forbidden",null, false);
        }
    }
}
