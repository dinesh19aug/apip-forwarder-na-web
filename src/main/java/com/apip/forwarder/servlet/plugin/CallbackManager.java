package com.apip.forwarder.servlet.plugin;

import java.util.ArrayList;
import java.util.List;

import com.apip.callback.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.apip.exception.CallbackException;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.CommonUtilities;

@Component
@Scope(value = "singleton")
public class CallbackManager {
    
    private ApipForwarderProperties appProperties;

    public CallbackManager() {

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<PaymentRequestCallback> getPaymentRequestCallbacks() throws CallbackException {

        ArrayList callbacks = new ArrayList();
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentRequestCallbacks())) {
            String pluginStrs[] = appProperties.getPaymentRequestCallbacks().split(";");
            String arr$[] = pluginStrs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String pluginStr = arr$[i$];
                try {
                    Class pluginClass = loadPluginClass(pluginStr);
                    String superClass = pluginClass.getSuperclass().getName();
                    if (superClass.equals(PaymentRequestCallback.class.getName()))
                        callbacks.add((PaymentRequestCallback) pluginClass.newInstance());
                } catch (Exception e) {
                    throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
                }
            }

        }
        return callbacks;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<PaymentResponseCallback> getPaymentResponseCallbacks() throws CallbackException {

        ArrayList callbacks = new ArrayList();
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentResponseCallbacks())) {
            String pluginStrs[] = appProperties.getPaymentResponseCallbacks().split(";");
            String arr$[] = pluginStrs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String pluginStr = arr$[i$];
                try {
                    Class pluginClass = loadPluginClass(pluginStr);
                    String superClass = pluginClass.getSuperclass().getName();
                    if (superClass.equals(PaymentResponseCallback.class.getName()))
                        callbacks.add((PaymentResponseCallback) pluginClass.newInstance());
                } catch (Exception e) {
                    throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
                }
            }

        }
        return callbacks;
    }

    public List<PaymentCancelCallback> getPaymentCancelCallbacks() throws CallbackException {

        ArrayList callbacks = new ArrayList();
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentCancelCallbacks())) {
            String pluginStrs[] = appProperties.getPaymentCancelCallbacks().split(";");
            String arr$[] = pluginStrs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String pluginStr = arr$[i$];
                try {
                    Class pluginClass = loadPluginClass(pluginStr);
                    String superClass = pluginClass.getSuperclass().getName();
                    if (superClass.equals(PaymentCancelCallback.class.getName()))
                        callbacks.add((PaymentCancelCallback) pluginClass.newInstance());
                } catch (Exception e) {
                    throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
                }
            }

        }
        return callbacks;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<PaymentMethodUpdateRequestCallback> getPaymentMethodUpdateRequestCallbacks() throws CallbackException {

        ArrayList callbacks = new ArrayList();
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentMethodUpdateRequestCallbacks())) {
            String pluginStrs[] = appProperties.getPaymentMethodUpdateRequestCallbacks().split(";");
            String arr$[] = pluginStrs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String pluginStr = arr$[i$];
                try {
                    Class pluginClass = loadPluginClass(pluginStr);
                    String superClass = pluginClass.getSuperclass().getName();
                    if (superClass.equals(PaymentMethodUpdateRequestCallback.class.getName()))
                        callbacks.add((PaymentMethodUpdateRequestCallback) pluginClass.newInstance());
                } catch (Exception e) {
                    throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
                }
            }

        }
        return callbacks;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<PaymentMethodUpdateResponseCallback> getPaymentMethodUpdateResponseCallbacks() throws CallbackException {

        ArrayList callbacks = new ArrayList();
        if (!CommonUtilities.isNullOrEmpty(appProperties.getPaymentMethodUpdateResponseCallbacks())) {
            String pluginStrs[] = appProperties.getPaymentMethodUpdateResponseCallbacks().split(";");
            String arr$[] = pluginStrs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String pluginStr = arr$[i$];
                try {
                    Class pluginClass = loadPluginClass(pluginStr);
                    String superClass = pluginClass.getSuperclass().getName();
                    if (superClass.equals(PaymentMethodUpdateResponseCallback.class.getName()))
                        callbacks.add((PaymentMethodUpdateResponseCallback) pluginClass.newInstance());
                } catch (Exception e) {
                    throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
                }
            }

        }
        return callbacks;
    }


    public List<LightsOutCallback> getLightsOutCallbacks() throws CallbackException {

        ArrayList callbacks = new ArrayList();
        if (!CommonUtilities.isNullOrEmpty(appProperties.getLightsOutCallbacks())) {
            String pluginStrs[] = appProperties.getLightsOutCallbacks().split(";");
            String arr$[] = pluginStrs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String pluginStr = arr$[i$];
                try {
                    Class pluginClass = loadPluginClass(pluginStr);
                    String superClass = pluginClass.getSuperclass().getName();
                    if (superClass.equals(LightsOutCallback.class.getName()))
                        callbacks.add((LightsOutCallback) pluginClass.newInstance());
                } catch (Exception e) {
                    throw new CallbackException("Unable to initiate request callback: " + pluginStr + ": " + e.getMessage(), e);
                }
            }

        }
        return callbacks;
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
