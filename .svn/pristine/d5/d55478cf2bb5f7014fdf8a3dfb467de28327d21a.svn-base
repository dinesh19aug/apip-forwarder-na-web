package com.apip.forwarder.utility;

import com.acn.custmgmt.oep2.ws.client.APIPUIREQUESTGENERATORBindingStub;
import com.acn.custmgmt.oep2.ws.client.ApipUIRequestGeneratorServiceLocator;
import com.acn.util.ApipPluginConstants;
import com.acn.util.PropertiesLoader;
import com.apip.forwarder.dao.ApipPluginDao;
import com.apip.forwarder.dao.ApipPluginDaoImpl;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: darora
 * Date: 5/15/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApipUtil {
    private static Logger log = Logger.getLogger(ApipUtil.class);
    public  final String APIP_REQ_GEN_SERVICE_NAME = "ApipUIRequestGenerator";

    public   APIPUIREQUESTGENERATORBindingStub apipRequestGeneratorBinding;

    /**
     * This method will get the result Column from APIP.PAYMENTTXN table using the transaction ID*/
    public String getRequestColumnString(HttpServletRequest req) throws Exception {
        String resultString = null;
        try{
            ApplicationContext context = new ClassPathXmlApplicationContext("/WEB-INF/applicationContext.xml");
            ApipPluginDao dao = context.getBean("apipPluginDao", ApipPluginDaoImpl.class);
            resultString = dao.getRequestDataFromPaymentTxn(req.getParameter("transactionId"));
        }catch(Exception ex){
            log.error("***** EXCEPTION THROWN WHILE TRYING TO GET REQUEST COLUMN VALUE FROM APIP.PAYMENTTXN FOR TXNID:" + req.getParameter("transactionId"));
            throw new Exception();
        }
        return resultString;

    }

    /**
     * This method will get the result Column from APIP.PAYMENTTXN table using the transaction ID for PaymentResponseForwarder*/
    public String getRequestColumnString(String txnId) throws Exception {
        String resultString = null;
        try{
            ApplicationContext context = new ClassPathXmlApplicationContext("/WEB-INF/applicationContext.xml");
            ApipPluginDao dao = context.getBean("apipPluginDao", ApipPluginDaoImpl.class);
            resultString = dao.getRequestDataFromPaymentTxn(txnId);
        }catch(Exception ex){
            log.error("***** EXCEPTION THROWN WHILE TRYING TO GET REQUEST COLUMN VALUE FROM APIP.PAYMENTTXN FOR TXNID:" + txnId);
            throw new Exception();
        }
        return resultString;

    }
   /**
    * This method will get the user name
    * from the request column in the APIP.PaymentTxn
    * The value in the request column is a JSON String
    * and so this method will first do Json to HashMap
    * before it retrieves the value of User.
    * */
    public HashMap<String,String> getLightsOutParams(String requestColumnValue){
        HashMap<String,String> loMap = new HashMap<String, String>();
        String userName = null;
        String countryCode = null;
        HashMap<String,String> dataMap = this.convertJsonStringToMap(requestColumnValue);
        userName = dataMap.get("user");
        countryCode = dataMap.get("countryCode");
        loMap.put("user",userName);
        loMap.put("countryCode",countryCode);
        return dataMap;
    }

    /**
     * This method will compare the signature in HTTPRequest and new signature created using the values in the
     * APIP.PAYMENTXN. This method will call Ent-API webservice comparedSignedData.
     * */
    public boolean compareSignature(String requestColumnValue, String incomingSignature) throws Exception {
        //Adding signature in the map as webservice will need this to compare signatures
        HashMap<String,String> dataMap = this.convertJsonStringToMap(requestColumnValue);
        log.info("COMPARING JSON STRING: " + requestColumnValue);
        log.info("EXISTING SIGNATURE: " + incomingSignature);
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.serialize(dataMap);
        boolean result=false;
        try {
            if(this.apipRequestGeneratorBinding==null){
                this.setAPIPUIREQUESTGENERATORBindingStub();
            }
            result=apipRequestGeneratorBinding.compareSignedData(jsonString);
        } catch (RemoteException e) {
            log.error("ERROR OCCURED WHILE CALLING COMPARE SIGNATURE.......");
            e.getMessage();
            throw new Exception();
        } catch (Exception e) {
            log.error("ERROR OCCURED WHILE CALLING COMPARE SIGNATURE.......");
            e.getMessage();
            throw new Exception();
        }
        return result;
    }

    /** Web Service call and Stub Bindings*/
    private void setAPIPUIREQUESTGENERATORBindingStub() throws Exception {
        try{
            ApipUIRequestGeneratorServiceLocator serviceLocator = new ApipUIRequestGeneratorServiceLocator();
            String EntApiUrl = PropertiesLoader.getUrlFromPropertiesFile("EntApiUrl", ApipPluginConstants.APIP_FWD_FILE_NAME);
            serviceLocator.setAPIPUIREQUESTGENERATORPortEndpointAddress(EntApiUrl + '/' + APIP_REQ_GEN_SERVICE_NAME);
            apipRequestGeneratorBinding = (APIPUIREQUESTGENERATORBindingStub) serviceLocator.getAPIPUIREQUESTGENERATORPort();
            apipRequestGeneratorBinding.setTimeout(60000);
        }catch (Exception ex){
            log.error("***** ERROR OCCURRED WHILE TRYING TO GET THE STUB BINDING FOR ENT_API SERVICE IN APIPUTIL CLASS");
            ex.getMessage();
            throw new Exception();
        }
    }

    private HashMap<String, String> convertJsonStringToMap(String requestColumnValue) {
        JSONDeserializer deserializer = new JSONDeserializer();
        return (HashMap<String, String>) deserializer.deserialize(requestColumnValue);
    }


}
