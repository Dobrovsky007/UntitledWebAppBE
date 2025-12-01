package com.webapp.Eventified.dto.user.mailing;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractEmailContext {
 
    private String from;
    private String to;
    private String subject;
    private String email;
    private String attachment;
    private String fromDisplayName;
    private String emailLanguage;
    private String displayName;
    private String templateLocation;
    private Map<String, Object> context;

    public AbstractEmailContext() {
        this.context = new HashMap<>();
    }

    public <T> AccountVerificationEmailContext init(T context){
        return null;
    }

    
}
