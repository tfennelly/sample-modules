$.foxweave.addComponentView(function(config, datastoreProxy, appURL) {
        var webhookURL = $("td#Webhook_webhookURL");

        function contextPathChanged() {
            var contextPathInput = $("input#Webhook_context_path");
            var contextPath = contextPathInput.val();

            if (contextPath.length > 0 && contextPath.charAt(0) !== '/') {
                contextPath = "/" + contextPath;
                contextPathInput.val(contextPath);
            }

            return contextPath;
        }

        if (appURL !== undefined) {
            if (appURL.length > 0 && appURL.charAt(appURL.length - 1) === '/') {
                appURL = appURL.substring(0, appURL.length - 1);
            }

            function initWebhookURL() {
                webhookURL.empty().append(appURL + contextPathChanged());
            }

            $("input#Webhook_context_path").change(initWebhookURL);
            initWebhookURL();
        } else {
            $("input#Webhook_context_path").change(contextPathChanged);
            webhookURL.append('<i>Application not deployed.  You must deploy it to a target first!!</i>');
        }
    }
);
