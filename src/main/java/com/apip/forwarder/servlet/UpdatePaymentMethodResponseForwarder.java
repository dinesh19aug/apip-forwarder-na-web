package com.apip.forwarder.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.apip.bean.WrittenPaymentMethodData;
import com.apip.callback.PaymentMethodUpdateResponseCallback;
import com.apip.forwarder.PaymentMethodUpdateResponsePlugin;
import com.apip.forwarder.dao.ApipDao;
import com.apip.forwarder.dao.ApipDaoImpl;
import com.apip.forwarder.servlet.plugin.CallbackManager;
import com.apip.forwarder.servlet.plugin.PluginManager;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.ApipServletConstants;
import com.apip.util.CommonUtilities;

@Controller
public class UpdatePaymentMethodResponseForwarder extends AbstractController {

    private static Logger log = Logger.getLogger(UpdatePaymentMethodResponseForwarder.class);

    PluginManager pluginManager;
    CallbackManager callbackManager;
    ApipForwarderProperties appProperties;
    ApipDao apipDao;

    @Override
    @SuppressWarnings("rawtypes")
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView modelAndView = new ModelAndView("abscontroller");
        Map<String, Object> canonicalData = null;
        PaymentMethodUpdateResponsePlugin plugin = null;
        String application = null;
        try {
            log.debug("In UpdatePaymentMethodResponseForwarder->handleRequestInternal");
           
            // get cookie information if available
            String cookieApplication = "";
            @SuppressWarnings("unused")
            String cookieTransactionId = "";
            
            // set application and transaction id
            Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
              String cookieName = cookie.getName();
              if (cookieName.equalsIgnoreCase(ApipServletConstants.COOKIE_APPLICATION)) {
                  cookieApplication = cookie.getValue();
                  break;
              } 
            }

            // get forwarder plug-in
            plugin = pluginManager.getPaymentMethodUpdateResponsePlugin();
            canonicalData = plugin.convertResponseToCanonical(req, appProperties);
            
            // first see if you can retrieve transactionId/application from vendor result
            // otherwise use cookie as a backup
            application = (String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION);
            if (CommonUtilities.isNullOrEmpty(application) && !CommonUtilities.isNullOrEmpty(cookieApplication)) {
                application = cookieApplication;
            }
            
            // allow plug-in to convert to our standard parameter map before writing update to database
            Map<String, Object> convertedParams = plugin.convertDataForDatabase(req,canonicalData, appProperties);
 
            // update apip database with response data
            WrittenPaymentMethodData writtenData = apipDao.writePaymentMethodUpdateResponseData(convertedParams);
            writtenData.setApplication((String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION));
            if (CommonUtilities.isNullOrEmpty(writtenData.getApplication())) {
                writtenData.setApplication(cookieApplication);
            }
            
            // get list of call-backs to perform
            List<PaymentMethodUpdateResponseCallback> callbacks = callbackManager.getPaymentMethodUpdateResponseCallbacks();

            // perform each call-back
            for (PaymentMethodUpdateResponseCallback callback : callbacks) {
                callback.process(req, resp, canonicalData, writtenData);
            }

            // redirect to plugin-based response URL
            String forwardUrl = plugin.getResponsePageUrl(req, appProperties, canonicalData, writtenData);
            Map<String, Object> responseMap = plugin.convertDataForResponsePage(req, canonicalData, writtenData, appProperties);
            
            modelAndView = new ModelAndView("/WEB-INF/jsp/sendRedirect.jsp");
            responseMap.put("redirectUrl", forwardUrl);
            modelAndView.addObject("redirectMap", responseMap);
            
        } catch (Exception e) {
            try {
                if (!CommonUtilities.isNullOrEmpty(application)) {
                    log.error(e.getMessage(), e);
                    if (canonicalData == null) {
                        canonicalData = new HashMap<String, Object>();
                    }
                    canonicalData.put(ApipServletConstants.CANONICAL_APPLICATION, application);
                    canonicalData.put(ApipServletConstants.CANONICAL_ERROR_MESSAGE, 
                                      CommonUtilities.isNullOrEmpty(e.getMessage()) ? "Error" : e.getMessage());
                    // the plugin must look for error message and direct accordingly
                    String forwardUrl = plugin.getResponsePageUrl(req, appProperties, canonicalData, null);
                    Map<String, Object> responseMap = plugin.convertDataForResponsePage(req, canonicalData, null, appProperties);
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
 

