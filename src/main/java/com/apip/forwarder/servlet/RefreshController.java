package com.apip.forwarder.servlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.apip.callback.PaymentMethodUpdateRequestCallback;
import com.apip.callback.PaymentMethodUpdateResponseCallback;
import com.apip.callback.PaymentRequestCallback;
import com.apip.callback.PaymentResponseCallback;
import com.apip.forwarder.servlet.plugin.CallbackManager;
import com.apip.forwarder.servlet.plugin.PluginManager;
import com.apip.util.ApipForwarderProperties;

@Controller
public class RefreshController extends AbstractController {

    private static Logger log = Logger.getLogger(RefreshController.class);

    PluginManager pluginManager;
    CallbackManager callbackManager;
    ApipForwarderProperties appProperties;

    @Override
    @SuppressWarnings("rawtypes")
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView modelAndView = new ModelAndView("/WEB-INF/jsp/refreshResult.jsp");
        
        try {
            log.debug("In RefreshController->handleRequestInternal");

            pluginManager.getPaymentMethodUpdateRequestPlugin().refreshPlugin();
            pluginManager.getPaymentMethodUpdateResponsePlugin().refreshPlugin();
            pluginManager.getPaymentRequestPlugin().refreshPlugin();
            pluginManager.getPaymentResponsePlugin().refreshPlugin();
            pluginManager.getPaymentcancelPlugin().refreshPlugin();

            for (PaymentMethodUpdateRequestCallback callback : callbackManager.getPaymentMethodUpdateRequestCallbacks()) {
                callback.refreshCallback();
            }

            for (PaymentMethodUpdateResponseCallback callback : callbackManager.getPaymentMethodUpdateResponseCallbacks()) {
                callback.refreshCallback();
            }

            for (PaymentRequestCallback callback : callbackManager.getPaymentRequestCallbacks()) {
                callback.refreshCallback();
            }

            for (PaymentResponseCallback callback : callbackManager.getPaymentResponseCallbacks()) {
                callback.refreshCallback();
            }
            
            modelAndView.addObject("refreshMessageColor", "green");
            modelAndView.addObject("refreshMessage", "Refresh Successful!");

        } catch (Exception e) {
            log.error(e.getMessage(), e);

            modelAndView.addObject("refreshMessageColor", "red");
            modelAndView.addObject("refreshMessage", "Refresh Failed!");
            
        }

        return modelAndView;
    }


    
    public PluginManager getPluginManager() {
    
        return pluginManager;
    }


    
    public void setPluginManager(PluginManager pluginManager) {
    
        this.pluginManager = pluginManager;
    }


    
    public CallbackManager getCallbackManager() {
    
        return callbackManager;
    }


    
    public void setCallbackManager(CallbackManager callbackManager) {
    
        this.callbackManager = callbackManager;
    }


    
    public ApipForwarderProperties getAppProperties() {
    
        return appProperties;
    }


    
    public void setAppProperties(ApipForwarderProperties appProperties) {
    
        this.appProperties = appProperties;
    }

    
    
}


