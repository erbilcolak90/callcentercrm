package com.callcentercrm.www.controller.user;

import com.callcentercrm.www.entities.UserDocument;
import com.callcentercrm.www.enums.DocumentType;
import com.callcentercrm.www.inputs.PaginationWithUser;
import com.callcentercrm.www.inputs.UserDocumentInput;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.StorageService;
import com.callcentercrm.www.services.UserDocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user/document")
public class UserDocumentController {

    private UserDocumentService userDocumentService;
    private StorageService storageService;

    public UserDocumentController(UserDocumentService userDocumentService) {
        this.userDocumentService = userDocumentService;
    }


    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @GetMapping("/getById")
    public Result<UserDocument> getDocumentById(@RequestParam String documentId) {
        Result<UserDocument> result = userDocumentService.getDocumentById(documentId);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/getAllDocument")
    public Result<Page<UserDocument>> getAllDocumentByUser(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<UserDocument>> result = userDocumentService.getAllDocumentByUser(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping(value = "/uploadFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadDocument(@RequestParam String userId, @RequestParam DocumentType documentType, @RequestPart MultipartFile file ) throws IOException {
        Result<String> result = userDocumentService.uploadDocument(userId, documentType, file);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/change")
    public Result<String> changeToDocument(@RequestBody UserDocumentInput userDocumentInput) {
        Result<String> result = userDocumentService.changeToDocument(userDocumentInput);
        return new Result<>(result.getMessage(), result.getData());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/delete")
    public Result<String> deleteDocument(@RequestParam String documentId) {
        Result<String> result = userDocumentService.deleteDocument(documentId);
        return new Result<>(result.getMessage(), result.getData());
    }

}
