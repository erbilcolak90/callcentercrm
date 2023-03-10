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
@CrossOrigin
@RequestMapping("/api/v1/user/document")
public class UserDocumentController {

    private UserDocumentService userDocumentService;
    private StorageService storageService;

    public UserDocumentController(UserDocumentService userDocumentService) {
        this.userDocumentService = userDocumentService;
    }


    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @GetMapping("/getDocumentById")
    public Result<UserDocument> getDocumentById(@RequestParam String documentId) {
        Result<UserDocument> result = userDocumentService.getDocumentById(documentId);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/getAllDocumentByUser")
    public Result<Page<UserDocument>> getAllDocumentByUser(@RequestBody PaginationWithUser paginationWithUser) {
        Result<Page<UserDocument>> result = userDocumentService.getAllDocumentByUser(paginationWithUser);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping(value = "/uploadDocument",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadDocument(@RequestParam String userId, @RequestParam DocumentType documentType, @RequestPart MultipartFile file ) throws IOException {
        Result<String> result = userDocumentService.uploadDocument(userId, documentType, file);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/changeToDocument")
    public Result<String> changeToDocument(@RequestBody UserDocumentInput userDocumentInput) {
        Result<String> result = userDocumentService.changeToDocument(userDocumentInput);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/deleteDocument")
    public Result<String> deleteDocument(@RequestParam String documentId) {
        Result<String> result = userDocumentService.deleteDocument(documentId);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

}
