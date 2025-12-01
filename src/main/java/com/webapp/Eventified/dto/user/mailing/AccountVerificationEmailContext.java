package com.webapp.Eventified.dto.user.mailing;

import org.springframework.web.util.UriComponentsBuilder;

import com.webapp.Eventified.model.User;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    private String token;

    @Override
    public <T> AccountVerificationEmailContext init(T context){
        User user = (User) context;
        getContext().put("username", user.getUsername());
        setTemplateLocation("mail/email-verification");
        setSubject("Complete your registration");
        setFrom("eventify@gmail.com");
        setTo(user.getEmail());

        return this;
    }

    public void setToken(String token){
        this.token = token;
        getContext().put("token", token);
    }
    
    public void buildVerificationUrl(final String baseUrl, String token){
        final String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/api/auth/verify")
                .queryParam("token", token)
                .toUriString();
        getContext().put("verificationUrl", url);
    }
}
