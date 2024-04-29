package com.njha.inboxapp.emaillist;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class EmailListDto {
    private EmailListItem email;
    private String agoTimeString;
}
