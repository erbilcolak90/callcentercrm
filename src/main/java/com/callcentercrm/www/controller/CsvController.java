package com.callcentercrm.www.controller;

import com.callcentercrm.www.csv.CSVHelper;
import com.callcentercrm.www.csv.CSVService;
import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.result.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/csv")
public class CsvController {

    @Autowired
    private CSVService csvService;

    @ApiOperation(value = "Bearer", authorizations = {@Authorization(value = "JWT")})
    @PostMapping(value = "/uploadFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<List<User>> uploadCSV(@RequestPart MultipartFile file){
        if(CSVHelper.hasCSVFormat(file)){
            Result<List<User>> result = csvService.save(file);
            return new Result<>(result.getMessage(), result.getData());
        }
        return null;
    }

}
