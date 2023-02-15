package com.callcentercrm.www.services;


import com.callcentercrm.www.entities.UserDocument;
import com.callcentercrm.www.enums.DocumentType;
import com.callcentercrm.www.enums.ProcessNames;
import com.callcentercrm.www.enums.Status;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.inputs.UserDocumentInput;
import com.callcentercrm.www.repositories.UserDocumentRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class UserDocumentService {

    private UserDocumentRepository userDocumentRepository;
    private ProcessService processService;
    private StorageService storageService;

    @Autowired
    public UserDocumentService(UserDocumentRepository userDocumentRepository, ProcessService processService, StorageService storageService) {
        this.userDocumentRepository = userDocumentRepository;
        this.processService = processService;
        this.storageService = storageService;
    }

    public Result<UserDocument> getDocumentById(String documentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new Result<>("Success :", userDocumentRepository.findById(documentId).orElse(null), true);
        }

            UserDocument userDocument = userDocumentRepository.getById(documentId, authentication.getName());
            if (userDocument == null) {
                return new Result<>("Document is not found: ", null, false);
            }
            else{
                return new Result<>("Success",userDocument, true);
            }


    }

    public Result<Page<UserDocument>> getAllDocumentByUser(PaginationWithUser paginationWithUser) {
        Pageable pageable = PageRequest.of(paginationWithUser.getPage(), paginationWithUser.getSize(), Sort.by(Sort.Direction.valueOf(paginationWithUser.getSortType().toString()), paginationWithUser.getFieldName()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if user at context has admin role. the admin can access all user document.
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new Result<>("Success", userDocumentRepository.findAll(paginationWithUser.getUserId(),pageable), true);
        }
        // if user at context has just user role.
        return new Result<>("Success", userDocumentRepository.findByIsDeletedFalse(authentication.getName(), pageable), true);
    }

    @Transactional
    public Result<String> uploadDocument(UserDocumentInput userDocumentInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(userDocumentInput.getUserId())) {
            UserDocument document = new UserDocument();
            DocumentType documentType = userDocumentInput.getDocumentType();

            document.setDocumentType(documentType);
            document.setUserId(userDocumentInput.getUserId());
            document.setDocumentURL(userDocumentInput.getDocumentURL());
            document.setStatus(Status.PENDING);
            document.setStatusMessage("Waiting for approve");
            document.setCreateDate(new Date());
            document.setUpdateDate(new Date());
            document.setDeleted(false);

            userDocumentRepository.save(document);

            processService.addProcess(document.getId(), ProcessNames.USER_DOCUMENT,document.getDocumentType());

            return new Result<>("Success", "Waiting for approve", true);

        } else if (!userDocumentInput.getUserId().equals(authentication.getName())) {
            return new Result<>("forbidden", null, false);
        }
        return null;
    }

    public Result<String> uploadDocument(String userId, DocumentType documentType , MultipartFile file ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(userId)) {
            String fileURL = storageService.uploadFile(file,userId,documentType);

            UserDocument document = new UserDocument();

            document.setDocumentType(documentType);
            document.setUserId(userId);
            document.setDocumentURL(fileURL);
            document.setStatus(Status.PENDING);
            document.setStatusMessage("Waiting for approve");
            document.setCreateDate(new Date());
            document.setUpdateDate(new Date());
            document.setDeleted(false);

            userDocumentRepository.save(document);

            processService.addProcess(document.getId(), ProcessNames.USER_DOCUMENT,document.getDocumentType());

            return new Result<>("Success", "Waiting for approve", true);

        }
        else{
            return new Result<>("forbidden",null, false);
        }
    }



    @Transactional
    public Result<String> changeToDocument(UserDocumentInput userDocumentInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (userDocumentInput.getUserId() == null || userDocumentInput.getUserId().isEmpty()) {
            return new Result<>("user id is not available", null, false);
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getName().equals(userDocumentInput.getUserId())) {
            UserDocument userDocument = userDocumentRepository.findById(userDocumentInput.getId()).orElseThrow();
            DocumentType documentType = userDocumentInput.getDocumentType();

            userDocument.setDocumentType(documentType);
            userDocument.setDocumentURL(userDocumentInput.getDocumentURL());
            userDocument.setStatus(Status.PENDING);
            userDocument.setStatusMessage("Waiting for approve");
            userDocument.setUpdateDate(new Date());
            userDocumentRepository.save(userDocument);

            processService.addProcess(userDocument.getId(), ProcessNames.USER_DOCUMENT,userDocument.getDocumentType());

            return new Result<>("Success", "Waiting for approve", true);

        } else if (!userDocumentInput.getUserId().equals(authentication.getName())) {
            return new Result<>("forbidden", null, true);
        }
        return null;

    }

    @Transactional
    public Result<String> deleteDocument(String documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDocument userDocument = userDocumentRepository.findById(documentId).orElse(null);
        if (userDocument == null) {
            return new Result<>("Document not found", null, false);
        }
        if (authentication.getName().equals(userDocument.getUserId()) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            userDocument.setDeleted(true);
            userDocument.setUpdateDate(new Date());
            userDocumentRepository.save(userDocument);

            return new Result<>("Success", "document deleted", true);
        } else {
            return new Result<>("forbidden", null, false);
        }
    }


}
