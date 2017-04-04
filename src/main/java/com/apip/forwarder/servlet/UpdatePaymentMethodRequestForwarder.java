package com.apip.forwarder.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.apip.bean.WrittenPaymentMethodData;
import com.apip.callback.PaymentMethodUpdateRequestCallback;
import com.apip.forwarder.PaymentMethodUpdateRequestPlugin;
import com.apip.forwarder.dao.ApipDao;
import com.apip.forwarder.servlet.plugin.CallbackManager;
import com.apip.forwarder.servlet.plugin.PluginManager;
import com.apip.gateway.adapter.GatewayAdapter;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.ApipServletConstants;
import com.apip.util.CommonUtilities;

@Controller
public class UpdatePaymentMethodRequestForwarder extends AbstractController {

    private static Logger log = Logger.getLogger(UpdatePaymentMethodRequestForwarder.class);

    PluginManager pluginManager;
    CallbackManager callbackManager;
    ApipForwarderProperties appProperties;
    ApipDao apipDao;
    
    @Override
    @SuppressWarnings("rawtypes")
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView modelAndView = null;
        Map<String, Object> canonicalData = null;
        String application = null;
        PaymentMethodUpdateRequestPlugin plugin = null;
        try {
            log.debug("In UpdatePaymentMethodRequestForwarder->handleRequestInternal");
            
            // get forwarder plug-in
            plugin = pluginManager.getPaymentMethodUpdateRequestPlugin();
            canonicalData = plugin.convertRequestToCanonical(req, appProperties);
            application = (String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION);
            
            // allow plug-in to convert to our standard parameter map before writing update to database
            Map<String, Object> dbData = plugin.convertDataForDatabase(req,canonicalData, appProperties);

            // write request data to apip database
            // this isn't going to write to the database for now, but will return the object
            WrittenPaymentMethodData writtenData = apipDao.writePaymentMethodUpdateRequestData(dbData);
            application = (String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION);
            String transactionId = (String) canonicalData.get(ApipServletConstants.CANONICAL_CLIENT_TRANSACTIONID);
            writtenData.setApplication(application);
            writtenData.setTransactionId(transactionId);
        
            try {

                // get the call-backs to perform (if any) for request
                List<PaymentMethodUpdateRequestCallback> callbacks = callbackManager.getPaymentMethodUpdateRequestCallbacks();

                // perform each request call-back (if any)
                for (PaymentMethodUpdateRequestCallback callback : callbacks) {
                    callback.process(req, resp, canonicalData, writtenData);
                }

                // set application and transaction id
                resp.addCookie(new Cookie(ApipServletConstants.COOKIE_APPLICATION, application));
                resp.addCookie(new Cookie(ApipServletConstants.COOKIE_TRANSACTIONID, transactionId));

                // get forward url from request plugin (i.e. cybersourceRedirect)
                // this must be defined in the apip-forwarder-servlet.xml file
                GatewayAdapter gatewayAdapter = plugin.getGatewayAdapter(req, canonicalData, writtenData, appProperties);

                Map<String, String> gatewayParameterMap = gatewayAdapter.getPaymentMethodUpdateRequestAsVendorMap();
                String forwardUrl = gatewayAdapter.getPaymentMethodUpdateUrl();

                // go ahead and force to cybersource for now
                modelAndView = new ModelAndView(forwardUrl);

                modelAndView.addObject("gatewayParameterMap", gatewayParameterMap);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                resp.sendRedirect(plugin.getErrorPageUrl(req, writtenData, appProperties));
            }
            
        // try and send user to the application's error page    
        } catch (Exception e) {
            try {
                if (!CommonUtilities.isNullOrEmpty(application)) {
                    log.error(e.getMessage(), e);
                    // the plugin must look for error message and direct accordingly
                    String forwardUrl = plugin.getErrorPageUrl(req, null, appProperties);
                    Map<String, Object> responseMap = new HashMap<String, Object>();
                    responseMap.put("redirectUrl", forwardUrl);
                    modelAndView = new ModelAndView("/WEB-INF/jsp/sendRedirect.jsp");
                    modelAndView.addObject("redirectMap", responseMap);
                } else {
                    resp.sendRedirect(appProperties.getErrorForwardUrl());
                }
                
            // if that fails, just send to general error page
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
                resp.sendRedirect(appProperties.getErrorForwardUrl());
            }
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




    
    public ApipDao getApipDao() {
    
        return apipDao;
    }




    
    public void setApipDao(ApipDao apipDao) {
    
        this.apipDao = apipDao;
    }



    
}
 

