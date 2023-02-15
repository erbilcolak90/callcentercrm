package com.callcentercrm.www.inputs;

import com.callcentercrm.www.enums.SortType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationWithUser {

    private String userId;
    private int page;
    private int size;
    private String fieldName;
    private SortType sortType;

}
