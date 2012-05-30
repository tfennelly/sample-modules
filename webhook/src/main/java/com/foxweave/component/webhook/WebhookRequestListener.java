/*
 * Copyright (C) 2012 FoxWeave, Ireland.
 */
package com.foxweave.component.webhook;

import com.foxweave.pipeline.component.ApplicationRequestListener;
import com.foxweave.pipeline.exchange.Exchange;
import com.foxweave.pipeline.exchange.Header;
import com.foxweave.pipeline.exchange.Message;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.Map;

/**
 * Webhook request Listener.
 *
 */
public class WebhookRequestListener implements ApplicationRequestListener {

    private String handshakeKeyName;
    private String expectedHandshakeKeyValue;
    private boolean setParamsAsPayload;

    public WebhookRequestListener() {
    }

    public WebhookRequestListener setHandshakeKeyName(String handshakeKeyName) {
        this.handshakeKeyName = handshakeKeyName;
        return this;
    }

    public WebhookRequestListener setExpectedHandshakeKeyValue(String expectedHandshakeKeyValue) {
        this.expectedHandshakeKeyValue = expectedHandshakeKeyValue;
        return this;
    }

    public WebhookRequestListener setSetParamsAsPayload(boolean setParamsAsPayload) {
        this.setParamsAsPayload = setParamsAsPayload;
        return this;
    }

    @Override
    public void setRequestPath(String requestPath) {
    }

    @Override
    public Message onRequest(final Message message) throws Exception {
        final Exchange exchange = message.getExchange();

        Map requestParamMap = (Map) exchange.getExchangeScopedCache().get(PARAM_MAP);
        if (handshakeKeyName != null && expectedHandshakeKeyValue != null && expectedHandshakeKeyValue.trim().length() > 1) {
            String handshakeKeyValue = getParamVal(handshakeKeyName, requestParamMap);

            if (!expectedHandshakeKeyValue.equals(handshakeKeyValue)) {
                throw new LoginException("Invalid Webhook handshake.");
            }
        }

        Header requestMethod = message.getHeaders().get(REQUEST_METHOD);
        if (requestMethod != null && "GET".equalsIgnoreCase(requestMethod.getValue())) {
            message.setPayload(new JSONObject(requestParamMap));
        } else if (setParamsAsPayload) {
            message.setPayload(new JSONObject(requestParamMap));
        }

        return exchange.send(message);
    }

    private String getParamVal(String paramName, Map requestParamMap) {
        Object paramValueObj = requestParamMap.get(paramName);

        if (paramValueObj instanceof String[]) {
            return ((String[]) paramValueObj)[0];
        } else if (paramValueObj instanceof String) {
            return (String) paramValueObj;
        } else {
            return null;
        }
    }
}
