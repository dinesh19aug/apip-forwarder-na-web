package com.apip.forwarder.servlet;

import com.acn.util.ApipPluginConstants;
import com.acn.util.PropertiesLoader;
import com.apip.bean.WrittenPaymentData;
import com.apip.callback.PaymentCancelCallback;
import com.apip.callback.PaymentResponseCallback;
import com.apip.exception.ResponseException;
import com.apip.forwarder.PaymentCancelPlugin;
import com.apip.forwarder.dao.ApipDao;
import com.apip.forwarder.servlet.plugin.CallbackManager;
import com.apip.forwarder.servlet.plugin.PluginManager;
import com.apip.util.ApipForwarderProperties;
import com.apip.util.ApipServletConstants;
import com.apip.util.CommonUtilities;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: darora
 * Date: 6/12/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PaymentCancellationController extends AbstractController {
    private static Logger log = Logger.getLogger(PaymentCancellationController.class);
    PluginManager pluginManager;
    CallbackManager callbackManager;
    ApipForwarderProperties appProperties;
    ApipDao apipDao;
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = null;
        PaymentCancelPlugin plugin = null;

        log.debug("In PaymentCancellationServlet->handleRequestInternal .......");
        // get cookie information if available
        String cookieApplication = "";
        @SuppressWarnings("unused")
        String cookieTransactionId = "";
        try{
            // set application and transaction id
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equalsIgnoreCase(ApipServletConstants.COOKIE_APPLICATION)) {
                    cookieApplication = cookie.getValue();
                } else if (cookieName.equalsIgnoreCase(ApipServletConstants.COOKIE_TRANSACTIONID)) {
                    cookieTransactionId = cookie.getValue();
                }
            }
            plugin = pluginManager.getPaymentcancelPlugin();

            // get list of call-backs to perform
            List<PaymentCancelCallback> callbacks = callbackManager.getPaymentCancelCallbacks();

            // perform each call-back
            for (PaymentCancelCallback callback : callbacks) {
                callback.process(request, response, null, null);
            }
            Map dbParams =plugin.convertDataForDatabase(cookieApplication,cookieTransactionId,ApipServletConstants.PAY_METHOD_CC);
            Map<String, Object> responseMap = plugin.convertDataForResponsePage(cookieApplication,cookieTransactionId);

            //Update the Apip.paymentTxn table with status = cancel
            WrittenPaymentData writtenData = apipDao.writePaymentResponseData(dbParams, null);

            log.debug("CUSTOMER WITH EPAYTXNID: " +cookieTransactionId + " FROM APPLICATION:  " + cookieApplication +  "CANCELLED THE PAYMENT ON CYBERSOURCE......");

            String forwardUrl = plugin.getResponsePageUrl(request,cookieApplication);
            modelAndView = new ModelAndView("/WEB-INF/jsp/sendRedirect.jsp");
            responseMap.put("redirectUrl", forwardUrl);
            modelAndView.addObject("redirectMap", responseMap);
        }catch (Exception e) {
            try {
                if (!CommonUtilities.isNullOrEmpty(cookieApplication)) {
                    log.error(e.getMessage(), e);
                    HashMap<String,Object> canonicalData = new HashMap<String, Object>();

                    canonicalData.put(ApipServletConstants.CANONICAL_APPLICATION, cookieApplication);
                    canonicalData.put(ApipServletConstants.CANONICAL_ERROR_MESSAGE,
                            CommonUtilities.isNullOrEmpty(e.getMessage()) ? "Error" : e.getMessage());
                    // the plugin must look for error message and direct accordingly
                    String forwardUrl = plugin.getResponsePageUrl(request, cookieApplication);
                    Map<String, Object> responseMap = plugin.convertDataForResponsePage(cookieApplication,cookieTransactionId);
                    responseMap.put("redirectUrl", forwardUrl);
                    modelAndView = new ModelAndView("/WEB-INF/jsp/sendRedirect.jsp");
                    modelAndView.addObject("redirectMap", responseMap);
                } else {
                    response.sendRedirect(appProperties.getErrorForwardUrl());
                }

                // if that fails, just send to general error page
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
                Map<String, Object> responseMap = plugin.convertDataForResponsePage(cookieApplication,cookieTransactionId);
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
