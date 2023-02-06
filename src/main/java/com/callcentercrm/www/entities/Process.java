package com.callcentercrm.www.entities;

import com.callcentercrm.www.enums.ProcessNames;
import com.callcentercrm.www.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Process")
public class Process {

    @Id
    private String id;
    @NotNull
    @NotEmpty
    private String inspectorId;
    @NotNull
    @NotEmpty
    private String operationId;
    @NotEmpty
    @NotNull
    private ProcessNames processName;
    private Object data;
    private Status status;
    private Date createDate;
    private Date updateDate;
    private boolean isDeleted;

}
