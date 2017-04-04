package com.apip.forwarder.servlet.plugin;

import com.apip.forwarder.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.apip.exception.CallbackException;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.CommonUtilities;

@Component
@Scope(value = "singleton")
public class PluginManager {

    private ApipForwarderProperties appProperties;
    
    public PluginManager() {

    }

    @SuppressWarnings("rawtypes")
    public PaymentRequestPlugin getPaymentRequestPlugin() throws CallbackException {

        PaymentRequestPlugin forwarder = null;
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentRequestPlugin())) {
            String pluginStr = CommonUtilities.getNullOrTrimmed(appProperties.getPaymentRequestPlugin());
            try {
                Class pluginClass = loadPluginClass(pluginStr);
                String superClass = pluginClass.getSuperclass().getName();
                if (superClass.equals(PaymentRequestPlugin.class.getName()))
                    forwarder = (PaymentRequestPlugin) pluginClass.newInstance();
            } catch (Exception e) {
                throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
            }
        }
        return forwarder;
    }

    @SuppressWarnings("rawtypes")
    public PaymentResponsePlugin getPaymentResponsePlugin() throws CallbackException {

        PaymentResponsePlugin forwarder = null;
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentResponsePlugin())) {
            String pluginStr = CommonUtilities.getNullOrTrimmed(appProperties.getPaymentResponsePlugin());
            try {
                Class pluginClass = loadPluginClass(pluginStr);
                String superClass = pluginClass.getSuperclass().getName();
                if (superClass.equals(PaymentResponsePlugin.class.getName()))
                    forwarder = (PaymentResponsePlugin) pluginClass.newInstance();
            } catch (Exception e) {
                throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
            }
        }
        return forwarder;
    }

    public PaymentCancelPlugin getPaymentcancelPlugin() throws CallbackException{
        PaymentCancelPlugin forwarder = null;
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentCancelPlugin())) {
            String pluginStr = CommonUtilities.getNullOrTrimmed(appProperties.getPaymentCancelPlugin());
            try {
                Class pluginClass = loadPluginClass(pluginStr);
                String superClass = pluginClass.getSuperclass().getName();
                if (superClass.equals(PaymentCancelPlugin.class.getName()))
                    forwarder = (PaymentCancelPlugin) pluginClass.newInstance();
            } catch (Exception e) {
                throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
            }
        }
        return forwarder;
    }

    @SuppressWarnings("rawtypes")
    public PaymentMethodUpdateRequestPlugin getPaymentMethodUpdateRequestPlugin() throws CallbackException {

        PaymentMethodUpdateRequestPlugin plugin = null;
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentMethodUpdateRequestPlugin())) {
            String pluginStr = CommonUtilities.getNullOrTrimmed(appProperties.getPaymentMethodUpdateRequestPlugin());
            try {
                Class pluginClass = loadPluginClass(pluginStr);
                String superClass = pluginClass.getSuperclass().getName();
                if (superClass.equals(PaymentMethodUpdateRequestPlugin.class.getName()))
                    plugin = (PaymentMethodUpdateRequestPlugin) pluginClass.newInstance();
            } catch (Exception e) {
                throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
            }
        }
        return plugin;
    }
    
    @SuppressWarnings("rawtypes")
    public PaymentMethodUpdateResponsePlugin getPaymentMethodUpdateResponsePlugin() throws CallbackException {

        PaymentMethodUpdateResponsePlugin plugin = null;
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentMethodUpdateResponsePlugin())) {
            String pluginStr = CommonUtilities.getNullOrTrimmed(appProperties.getPaymentMethodUpdateResponsePlugin());
            try {
                Class pluginClass = loadPluginClass(pluginStr);
                String superClass = pluginClass.getSuperclass().getName();
                if (superClass.equals(PaymentMethodUpdateResponsePlugin.class.getName()))
                    plugin = (PaymentMethodUpdateResponsePlugin) pluginClass.newInstance();
            } catch (Exception e) {
                throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
            }
        }
        return plugin;
    }

    public LightsOutPlugin getLightsOutPlugin() throws CallbackException{
        LightsOutPlugin forwarder = null;
        if (!CommonUtilities.isNullOrEmpty(appProperties.getLightsOutPlugin())) {
            String pluginStr = CommonUtilities.getNullOrTrimmed(appProperties.getLightsOutPlugin());
            try {
                Class pluginClass = loadPluginClass(pluginStr);
                String superClass = pluginClass.getSuperclass().getName();
                if (superClass.equals(LightsOutPlugin.class.getName()))
                    forwarder = (LightsOutPlugin) pluginClass.newInstance();
            } catch (Exception e) {
                throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
            }
        }
        return forwarder;
    }

    public ApipForwarderProperties getAppProperties() {
    
        return appProperties;
    }

    
    public void setAppProperties(ApipForwarderProperties appProperties) {
    
        this.appProperties = appProperties;
    }

    public Class loadPluginClass(String fullClassName) throws Exception {
        return this.getClass().getClassLoader().loadClass(fullClassName);

    }
    
}
