/*
 * Copyright (C) 2012 FoxWeave, Ireland.
 */
package com.foxweave.component.webhook;

import com.foxweave.pipeline.component.AbstractPipelineComponent;
import com.foxweave.pipeline.component.InputConnector;
import com.foxweave.pipeline.exchange.ExchangeFactory;
import com.foxweave.pipeline.lifecycle.ComponentLifecycle;
import com.foxweave.pipeline.lifecycle.Configurable;

import java.util.Properties;

/**
 * <a href="http://www.webhooks.org/">Webhook</a> Input Connector.
 * <p/>
 * This input connector sets up a Webhook style endpoint using an
 * {@link com.foxweave.pipeline.component.ApplicationRequestListener} implementation
 * (implemented in {@link WebhookRequestListener}).  {@link WebhookRequestListener} is registered in
 * the {@link #initialize()} method.
 */
public class WebhookInputConnector extends AbstractPipelineComponent implements InputConnector, ComponentLifecycle, Configurable<Properties> {
    
    private String contextPath;
    private String handshakeKeyName;
    private String handshakeKeyValue;
    private boolean setParamsAsPayload;

    @Override
    public void setConfiguration(final Properties config) {
        contextPath = cleanupContextPath(config.getProperty("Webhook_context_path"));
        setParamsAsPayload = config.getProperty("Webhook_set_params_as_payload", "true").equals("true");
        // Webhook handshake params.  See http://www.webhooks.org/
        handshakeKeyName = config.getProperty("Webhook_handskake_key_name", "HandshakeKey");
        handshakeKeyValue = config.getProperty("Webhook_handskake_key_value");
    }
    
    @Override
    public void initialize() throws Exception {
        // Create the Webhook ApplicationRequestListener implementation instance...
        WebhookRequestListener webhookRequestListener = new WebhookRequestListener().
                setHandshakeKeyName(handshakeKeyName).
                setExpectedHandshakeKeyValue(handshakeKeyValue).
                setSetParamsAsPayload(setParamsAsPayload);

        // ... and register it with the PipelineContext, capturing requests on the user configured
        // sub-context path ...
        getPipelineContext().registerApplicationRequestListener(contextPath, webhookRequestListener);
    }

    @Override
    public void setExchangeFactory(final ExchangeFactory exchangeFactory) {
        // NoOp
    }

    private String cleanupContextPath(final String path) {
        if (path == null || path.equals(""))  {
            return "/";
        }

        if (path.charAt(0) != '/') {
            return "/" + path;
        }

        return path;
    }
}
