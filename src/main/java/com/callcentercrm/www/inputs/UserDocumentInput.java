package com.callcentercrm.www.inputs;

import com.callcentercrm.www.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocumentInput {

    private String id;
    private String userId;
    private DocumentType documentType;
    private String documentURL;
}
