package com.callcentercrm.www.entities;

import com.callcentercrm.www.enums.DocumentType;
import com.callcentercrm.www.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("UserDocuments")
public class UserDocument {

    private String id;
    private String userId;
    private DocumentType documentType;
    private String documentURL;
    private Status status;
    private String statusMessage;
    private Date createDate;
    private Date updateDate;
    private boolean isDeleted;

}
