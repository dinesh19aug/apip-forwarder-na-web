package com.apip.forwarder.servlet;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acn.util.ApipPluginConstants;
import com.acn.util.PropertiesLoader;
import com.apip.forwarder.LightsOutPlugin;
import com.apip.forwarder.utility.ApipUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.apip.bean.WrittenPaymentData;
import com.apip.bean.WrittenPaymentMethodData;
import com.apip.callback.PaymentResponseCallback;
import com.apip.forwarder.PaymentResponsePlugin;
import com.apip.forwarder.dao.ApipDao;
import com.apip.forwarder.servlet.plugin.CallbackManager;
import com.apip.forwarder.servlet.plugin.PluginManager;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.ApipServletConstants;
import com.apip.util.CommonUtilities;

@Controller
public class PaymentResponseForwarder extends AbstractController {

    private static Logger log = Logger.getLogger(PaymentResponseForwarder.class);

    PluginManager pluginManager;
    CallbackManager callbackManager;
    ApipForwarderProperties appProperties;
    ApipDao apipDao;
 
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView modelAndView = null;
        String application = null;
        PaymentResponsePlugin plugin = null;
        LightsOutPlugin lightOutPlugin = null;
        Map<String, Object> canonicalData = null;
        ApipUtil util = new ApipUtil();

        try {
            log.debug("In PaymentResponseForwarder->handleRequestInternal");

            log.debug("PRINTING ALL THE REQUEST PARAMS FROM CYBERSOURCE....");
            lightOutPlugin = pluginManager.getLightsOutPlugin();
            Map m = req.getParameterMap();
            Set set = m.entrySet();
            Iterator  it = set.iterator();
            while(it.hasNext()){

                Map.Entry<String, String[]> entry =
                        (Map.Entry<String, String[]>) it.next();
                String paramName = entry.getKey();
                String[] paramValues = entry.getValue();
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() == 0)
                        log.debug("No Value");
                    else
                        log.debug(paramName + " = " + paramValue);
                } else {
                    for (int i = 0; i < paramValues.length; i++) {
                        log.debug(paramValues[i] );
                    }

                }
            }

            String transactionId;
            
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
              } else if (cookieName.equalsIgnoreCase(ApipServletConstants.COOKIE_TRANSACTIONID)) {
                  cookieTransactionId = cookie.getValue();
              }
            }
            
            // get forwarder plug-in
            plugin = pluginManager.getPaymentResponsePlugin();
            canonicalData = plugin.convertResponseToCanonical(req, appProperties);
            
            // first see if you can retrieve transactionId/application from vendor result
            // otherwise use cookie as a backup
            application = (String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION);
            if (CommonUtilities.isNullOrEmpty(application) && !CommonUtilities.isNullOrEmpty(cookieApplication)) {
                application = cookieApplication;
            }
            canonicalData.put(ApipServletConstants.CANONICAL_APPLICATION, application);
            
            transactionId = (String) canonicalData.get(ApipServletConstants.CANONICAL_VENDOR_TRANSACTIONID);
            if (CommonUtilities.isNullOrEmpty(transactionId) && !CommonUtilities.isNullOrEmpty(cookieTransactionId)) {
                transactionId = cookieTransactionId;
                
            }
            canonicalData.put(ApipServletConstants.CANONICAL_VENDOR_TRANSACTIONID, transactionId);
            //TxnId that comes back is of format NA_[AppName]_EpayTxnId. We need to get TxnId from this String
            String appTxnId= (String)canonicalData.get(ApipServletConstants.CANONICAL_CLIENT_TRANSACTIONID);
            String [] tokenTxnId = appTxnId.split("-");
            String requestColumn = util.getRequestColumnString(tokenTxnId[2]);
            HashMap<String,String> loMap = util.getLightsOutParams(requestColumn);
            String windowsLoginId =  loMap.get("user");
            String countryCode = loMap.get("countryCode");
            // allow plug-in to convert to our standard parameter map before writing update to database
            Map dbParams = plugin.convertDataForDatabase(req, canonicalData, appProperties);
            
            // update apip database with response data
            WrittenPaymentMethodData writtenMethodData = apipDao.writePaymentResponseMethodData(dbParams);
            WrittenPaymentData writtenData = apipDao.writePaymentResponseData(dbParams, writtenMethodData);
            writtenData.setApplication(application);
            writtenData.setTxnReferenceNumber(transactionId);
            
            // get list of call-backs to perform
            List<PaymentResponseCallback> callbacks = callbackManager.getPaymentResponseCallbacks();

            // perform each call-back
            for (PaymentResponseCallback callback : callbacks) {
                callback.process(req, resp, canonicalData, writtenData);
            }

            // redirect to plugin-based response URL
            String forwardUrl = plugin.getResponsePageUrl(req, appProperties, canonicalData, writtenData);
            Map<String, Object> responseMap = plugin.convertDataForResponsePage(req, canonicalData, writtenData, appProperties);
            //Check if the intercept response flag is set to true
            String interceptCSRespose = PropertiesLoader.getUrlFromPropertiesFile("interceptCsResponseFlag", ApipPluginConstants.APIP_FWD_FILE_NAME);
            if (interceptCSRespose.equalsIgnoreCase("Y") && plugin.isAppAllowedForInterception(req)){
                responseMap.put("forwardUrl",forwardUrl);
                forwardUrl = PropertiesLoader.getUrlFromPropertiesFile("interceptCsResponseUrl", ApipPluginConstants.APIP_FWD_FILE_NAME);
            }
            //Call Lights out to turn on Vitual Observer.
            lightOutPlugin.callLightsOut(false,windowsLoginId,application,countryCode);

            modelAndView = new ModelAndView("/WEB-INF/jsp/sendRedirect.jsp");
            responseMap.put("redirectUrl", forwardUrl);
            modelAndView.addObject("redirectMap", responseMap);

        // if there's an error, try and use the plugin to determine the correct landing page
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
                Map<String, Object> responseMap = plugin.convertDataForResponsePage(req, canonicalData, null, appProperties);
                responseMap.put("redirectUrl", appProperties.getErrorForwardUrl());
                modelAndView = new ModelAndView("/WEB-INF/jsp/sendRedirect.jsp");
                modelAndView.addObject("redirectMap", responseMap);

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
 

