package com.nimfid.commons.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;


@Data
@SuperBuilder
public class CustomResponse {

    private String              timeStamp;
    private String              message;
    private HttpStatus          status;
    private int                 statusCode;
    private String              reason;
    private Object              data;
    private boolean             success;

    public CustomResponse() {
        data        =   new Object();
    }
}
