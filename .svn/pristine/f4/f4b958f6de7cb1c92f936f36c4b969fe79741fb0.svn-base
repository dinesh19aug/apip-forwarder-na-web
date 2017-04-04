package com.apip.forwarder.servlet;

import com.acn.util.ApipPluginConstants;

import com.apip.bean.WrittenPaymentData;
import com.apip.callback.LightsOutCallback;
import com.apip.callback.PaymentRequestCallback;
import com.apip.forwarder.LightsOutPlugin;
import com.apip.forwarder.PaymentRequestPlugin;
import com.apip.forwarder.dao.ApipDao;
import com.apip.forwarder.servlet.plugin.CallbackManager;
import com.apip.forwarder.servlet.plugin.PluginManager;
import com.apip.forwarder.utility.ApipUtil;
import com.apip.gateway.adapter.GatewayAdapter;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.ApipServletConstants;
import com.apip.util.CommonUtilities;
import com.apip.util.PropertiesLoader;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
public class PaymentRequestForwarder extends AbstractController {

    private static Logger log = Logger.getLogger(PaymentRequestForwarder.class);

    PluginManager pluginManager;
    CallbackManager callbackManager;
    ApipForwarderProperties appProperties;
    ApipDao apipDao;


    @Override
    @SuppressWarnings("rawtypes")
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView modelAndView = null;
        PaymentRequestPlugin plugin = null;
        LightsOutPlugin lightOutPlugin = null;
        String application = null;
        String windowsLoginId = null;
        String countryCode = null;
        ApipUtil util = new ApipUtil();
        Map<String, Object> canonicalData = null;
        WrittenPaymentData writtenData = null;
        Map<String, String> gatewayParameterMap=null;
        try {
            log.debug("START:::: In PaymentRequestForwarder->handleRequestInternal");

            // get forwarder plug-in
            plugin = pluginManager.getPaymentRequestPlugin();
            lightOutPlugin = pluginManager.getLightsOutPlugin();
            String requestColumnValue = util.getRequestColumnString(req);
            canonicalData = plugin.convertRequestToCanonical(req, appProperties,requestColumnValue);

            /*boolean isSignatureValid= util.compareSignature(requestColumnValue,req.getParameter("signature"));
            if(isSignatureValid == false){
                log.error("***** DATA WAS TAMPERED AND THE SIGNATURE DO NO MATCH ........");
                log.error("***** INCOMING SIGNATURE: " + req.getParameter("signature"));
                log.error("***** ACTUAL DATA FROM APIP.PAYMENTTXN.REQUEST : " + requestColumnValue);
                 throw new Exception();
            }*/
            application = (String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION);
            windowsLoginId = (String) canonicalData.get(ApipServletConstants.CANONICAL_AUDIT_USER);
            countryCode = (String) canonicalData.get(ApipServletConstants.CANONICAL_COUNTRYCODE);
            // allow plug-in to convert to our standard parameter map before writing update to database
            Map dbData = plugin.convertDataForDatabase(req, canonicalData, appProperties);

            // write request data to apip database
            writtenData = apipDao.writePaymentRequestData(dbData);
            writtenData.setApplication(application);

            try {

                // get the call-backs to perform (if any) for request
                List<PaymentRequestCallback> callbacks = callbackManager.getPaymentRequestCallbacks();

                // perform each request call-back (if any)
                for (PaymentRequestCallback callback : callbacks) {
                    callback.process(req, resp, canonicalData, writtenData);
                }


                //Call the Lights out before going to CS
                List<LightsOutCallback> lightOutcallbacks = callbackManager.getLightsOutCallbacks();
                for (LightsOutCallback callback : lightOutcallbacks) {
                    callback.process(req, resp, canonicalData, writtenData);
                }
                lightOutPlugin.callLightsOut(true,windowsLoginId,application,countryCode);
                // set application and transaction id
                Cookie cookieAppName = new Cookie(ApipServletConstants.COOKIE_APPLICATION,(String) canonicalData.get(ApipServletConstants.CANONICAL_APPLICATION));
                cookieAppName.setPath("/");
                Cookie cookieTxnId = new Cookie(ApipServletConstants.COOKIE_TRANSACTIONID,(String) canonicalData.get(ApipServletConstants.CANONICAL_CLIENT_TRANSACTIONID));
                cookieTxnId.setPath("/");

                resp.addCookie(cookieAppName);
                resp.addCookie(cookieTxnId);

                // get forward url from request plugin (i.e. cybersourceRedirect)
                // this must be defined in the apip-forwarder-servlet.xml file
                GatewayAdapter gatewayAdapter = plugin.getGatewayAdapter(req, canonicalData, writtenData, appProperties);

                gatewayParameterMap = gatewayAdapter.getPaymentChargeRequestAsVendorMap();
                String forwardName = gatewayAdapter.getPaymentChargeUrl();
                /*if(application.equalsIgnoreCase("OEP2")){*/
                String lis;
                String isNewSCreenTurnedOn= PropertiesLoader.getUrlFromPropertiesFile("isNewScreenTurnedOn", ApipPluginConstants.APIP_FWD_FILE_NAME);
                String appReadyForNewScrn= PropertiesLoader.getUrlFromPropertiesFile("appsReadyForNewScrn", ApipPluginConstants.APIP_FWD_FILE_NAME);
                if(appReadyForNewScrn.contains(application) && isNewSCreenTurnedOn.equalsIgnoreCase("Y")){
                    forwardName="/WEB-INF/jsp/cybersource/cybersourcePaymentNew.jsp";
                }
                modelAndView = new ModelAndView(forwardName);
                modelAndView.addObject("gatewayParameterMap", gatewayParameterMap);


            } catch (Exception e) {
                log.error("*****EXCEPTION THROWN IN PAYMENTREQUESTFORWARDER  INNER CATCH........", e);
                log.error(e.getMessage(), e);
                resp.sendRedirect(plugin.getErrorPageUrl(req, writtenData, appProperties));
            }

         // try and send user to the application's error page    
        } catch (Exception e) {
            try {
                if (!CommonUtilities.isNullOrEmpty(application)) {
                    log.error("*****EXCEPTION THROWN IN PAYMENTREQUESTFORWARDER  OUTER CATCH........", e);
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

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(req);
        localeResolver.setLocale(req, resp, StringUtils.parseLocaleString(this.getLocale(gatewayParameterMap)));
        return modelAndView;
    }

    private String getLocale (Map<String, String> gatewayParameterMap) {
        String locale= gatewayParameterMap.get("locale");
        if(locale.equalsIgnoreCase("fr-CA")){
            locale="fr";
        }else if(locale.contains("es")){
            locale="es";
        }else{
            locale="en";
        }
        return locale;
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


