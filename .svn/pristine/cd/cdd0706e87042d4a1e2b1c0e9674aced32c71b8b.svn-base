package com.apip.forwarder.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.acn.util.ApipPluginConstants;
import com.acn.util.PropertiesLoader;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apip.bean.PaymentProfile;
import com.apip.bean.WrittenPaymentData;
import com.apip.bean.WrittenPaymentMethodData;
import com.apip.util.ApipServletConstants;

@Repository
@SuppressWarnings("unused")
public class ApipDaoImpl implements ApipDao {



    private static Logger log = Logger.getLogger(ApipDaoImpl.class);
    

    private HibernateTemplate hibernateTemplate;
    private JdbcTemplate jdbcTemplate;
    
    private SimpleJdbcCall procApipGetPaymentProfile;
    private SimpleJdbcCall procApipInsertPaymentProfile;
    private SimpleJdbcCall procApipUpdatePaymentProfile;
    private SimpleJdbcCall procApipInsertPaymentTxn;
    private SimpleJdbcCall procApipUpdatePaymentTxn;
    
    @SuppressWarnings("rawtypes")
    protected class LongRsExtractorWithOneRecordCheck implements ResultSetExtractor {
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            Object ret = null;
            if (rs.next()) {
                ret = (Object) rs.getLong(1);
                if (rs.next()) {
                    throw new SQLException("Multiple rows when just expected one.");
                }                
            }
            return ret;
        }
    }

    @SuppressWarnings("rawtypes")
    protected class BigDecimalRsExtractorWithOneRecordCheck implements ResultSetExtractor {
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            Object ret = null;
            if (rs.next()) {
                ret = (Object) rs.getBigDecimal(1);
                if (rs.next()) {
                    throw new SQLException("Multiple rows when just expected one.");
                }
            }
            return ret;
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static String getStringParameterForDB(Map map, String name, int maxLength) throws Exception {
       Object o = map.get(name);
       if (o != null) {
           if (o instanceof String) {
               if (maxLength > 0 && ((String) o).length() > maxLength) {
                   throw new Exception("DB Field " + name + " exceeds max length: " + maxLength);
               }
               return (String) o;
           } else {
               throw new Exception("DB Field " + name + " is invalid type: " + o.getClass().toString());
           }
       } 
       return null;
    }
    
    @SuppressWarnings("rawtypes")
    public static Integer getIntParameterForDB(Map map, String name) throws Exception {
        Object o = map.get(name);
        if (o != null) {
            if (o instanceof Integer) {
                return (Integer) o;
            } else {
                throw new Exception("DB Field " + name + " is invalid type: " + o.getClass().toString());
            }
        } 
        return null;
     }
    
    @SuppressWarnings("rawtypes")
    public static java.sql.Date getDateParameterForDB(Map map, String name) throws Exception {
        Object o = map.get(name);
        if (o != null) {
            if (o instanceof java.util.Date) {
                return new java.sql.Date(((java.util.Date) o).getTime());
            } else if (o instanceof java.sql.Date) {
                return (java.sql.Date) o;
            } else {
                throw new Exception("DB Field " + name + " is invalid type: " + o.getClass().toString());
            }
        } 
        return null;
     }
    
    @SuppressWarnings("rawtypes")
    public static BigDecimal getBigDecimalParameterForDB(Map map, String name) throws Exception {
        Object o = map.get(name);
        if (o != null) {
            if (o instanceof BigDecimal) {
                return (BigDecimal) o;
            } else if (o instanceof Double) {
                return BigDecimal.valueOf((Double) o);
            } else if (o instanceof Integer) {
                return BigDecimal.valueOf((Integer) o);
            } else if (o instanceof Long) {
                return BigDecimal.valueOf((Long) o);
            } else if (o instanceof String) {
                return new BigDecimal(((String) o).replaceAll( "[^\\d.]", "" ));
            } else {
                throw new Exception("DB Field " + name + " is invalid type: " + o.getClass().toString());
            }
        } 
        return null;
     }

    @SuppressWarnings("rawtypes")
    public WrittenPaymentData writePaymentRequestData(Map map) 
    throws Exception {
        log.debug("In ApipDaoImpl->writePaymentRequestData");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String piPaymentProfileRefNumber = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROFILEREFNUMBER, 0); //Payment Profile Reference Number
        String piPaymentProviderAccountNumber = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROVIDERACCOUNTNUMBER, 0); //Payment Provider Account Number
        String piPaymentProvider = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROVIDER, 0); //Payment Provider
        String piFirstSix = getStringParameterForDB(map, ApipServletConstants.DB_FIRSTSIX, 0); //Input First 6 digits of card number
        String piLastFour = getStringParameterForDB(map, ApipServletConstants.DB_LASTFOUR, 0); //Input Last 4 digits of card number
        String piPaymentMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTMETHOD, 0); //Payment Method
        String piPaymentSubMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTSUBMETHOD, 0); //Payment Sub Method
        String piCardExpMonth = getStringParameterForDB(map, ApipServletConstants.DB_CARDEXPMONTH, 0); //Input Card Expiry Month
        String piCardExpYear = getStringParameterForDB(map, ApipServletConstants.DB_CARDEXPYEAR, 0); //Input Card Expiry Year
        String piBillingName = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGNAME, 0); //Input Card Holder Name
        String piBillingCompany = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGCOMPANY, 0); //Input Card Holder Company
        String piBillingAddress1 = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGADDRESS1, 0); //Input Card Holder Billing Address1
        String piBillingAddress2 = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGADDRESS2, 0); //Input Card Holder Billing Address2
        String piBillingCity = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGCITY, 0);  //Input Card Holder Billing City
        String piBillingState = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGSTATE, 0); //Input Card Holder Billing State
        String piBillingPostalCode = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGPOSTALCODE, 0); //Input Card Holder Billing Postal Code
        String piBillingCountry = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGCOUNTRY, 0);  //Input Card Holder Billing Country
        String piBillingPhone = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGPHONE, 0); //Input Card Holder Billing Phone
        String piBillingAddressNumber = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGADDRESSNUMBER, 0); //Input Card Holder Billing Address Number
        String piBillingEmailAddress = getStringParameterForDB(map, ApipServletConstants.DB_BILLINGEMAILADDRESS, 0); //Input Card Holder Billing Email Address
        String piRequestingApplication = getStringParameterForDB(map, ApipServletConstants.DB_REQUESTINGAPPLICATION, 0); //Input Requesting Application of the Epay Transaction
        String piBusinessPurpose = getStringParameterForDB(map, ApipServletConstants.DB_BUSINESSPURPOSE, 0); //Input Business purpose of the Epay Transaction
        String piOriginType = getStringParameterForDB(map, ApipServletConstants.DB_ORIGINTYPE, 0); //Input Origin Type
        String piTxnReferenceNumberType = getStringParameterForDB(map, ApipServletConstants.DB_TXNREFERENCENUMBERTYPE, 0); //Input Transaction Reference Number Type
        String piTxnReferenceNumber = getStringParameterForDB(map, ApipServletConstants.DB_TXNREFERENCENUMBER, 0);; //Input Transaction Reference Number
        String piRequesterType = getStringParameterForDB(map, ApipServletConstants.DB_REQUESTERTYPE, 0); //Input Requester Type
        String piRequesterNumber = getStringParameterForDB(map, ApipServletConstants.DB_REQUESTERNUMBER, 0); //Input Requester Number
        String piRequesteripAddress = getStringParameterForDB(map, ApipServletConstants.DB_REQUESTERIPADDRESS, 0); //Input Requester IP Address
        String piTxnType = getStringParameterForDB(map, ApipServletConstants.DB_TXNTYPE, 0); //Input Transaction Type
        Integer piIsRecurring = getIntParameterForDB(map, ApipServletConstants.DB_ISRECURRING); //Input IS Recurring
        BigDecimal piTxnAmount = getBigDecimalParameterForDB(map, ApipServletConstants.DB_TXNAMOUNT); //Input Transaction Amount
        String piTxnCurrency = getStringParameterForDB(map, ApipServletConstants.DB_TXNCURRENCY, 0); //Input Transaction Currency
        String piRequest = getStringParameterForDB(map, ApipServletConstants.DB_REQUEST, 0); //Transaction Request
        String piResponse = getStringParameterForDB(map, ApipServletConstants.DB_RESPONSE, 0); //Input Transaction Response
        Integer piIsAvsValid = getIntParameterForDB(map, ApipServletConstants.DB_ISAVSVALID); //Flag indicating whether the AVS check was successful
        Integer piIsCvValid = getIntParameterForDB(map, ApipServletConstants.DB_ISCVVALID); //Flag indicating whether the Card Verification Code check was successful
        String piTxnResultCode = getStringParameterForDB(map, ApipServletConstants.DB_TXNRESULTCODE, 0); //Code returned from processor which indicates whether transaction was approved/declined
        String piAVSResultCode = getStringParameterForDB(map, ApipServletConstants.DB_AVSRESULTCODE, 0); //AVS response code returned from processor
        String piCVResultCode = getStringParameterForDB(map, ApipServletConstants.DB_CVRESULTCODE, 0); //Card Verification response code returned from processor
        String piApprovalCode = getStringParameterForDB(map, ApipServletConstants.DB_APPROVALCODE, 0); //Authorization code returned from processor when transaction is approved
        String piDeclinedMessage = getStringParameterForDB(map, ApipServletConstants.DB_DECLINEDMESSAGE, 0); //Message returned from processor describing reason for decline of transaction
        String piTxnNumber = getStringParameterForDB(map, ApipServletConstants.DB_TXNNUMBER, 0); //Number returned by processor to uniquely identify transaction
        String piRevenueSourceCode = getStringParameterForDB(map, ApipServletConstants.DB_REVENUESOURCECODE, 0); //Revenue Source Code
        String piCREATEDBY = getStringParameterForDB(map, ApipServletConstants.DB_CREATEDBY, 0); //Application/Process Name which is creating this record
        String piPaymentProviderConfig = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENT_PROVIDER_CONFIG, 0);
        String piTxnStatus = getStringParameterForDB(map, ApipServletConstants.DB_TXN_STATUS, 0);

        // return written data as client object
        return new WrittenPaymentData(piPaymentProfileRefNumber, piPaymentProviderAccountNumber, piPaymentProvider,
                                          piFirstSix, piLastFour, piPaymentMethod, piPaymentSubMethod, piCardExpMonth,
                                          piCardExpYear, piBillingName, piBillingCompany, piBillingAddress1,
                                          piBillingAddress2, piBillingCity, piBillingState, piBillingPostalCode,
                                          piBillingCountry, piBillingPhone, piBillingAddressNumber,
                                          piBillingEmailAddress, piRequestingApplication, piBusinessPurpose,
                                          piOriginType, piTxnReferenceNumberType, piTxnReferenceNumber,
                                          piRequesterType, piRequesterNumber, piRequesteripAddress, piTxnType,
                                          piIsRecurring, piTxnAmount, piTxnCurrency, piRequest, piResponse,
                                          piIsAvsValid, piIsCvValid,
                                          piTxnResultCode, piAVSResultCode, piCVResultCode, piApprovalCode,
                                          piDeclinedMessage, piTxnNumber, now, piRevenueSourceCode, piCREATEDBY,
                                          piPaymentProviderConfig, piTxnStatus);
    }
    
    @SuppressWarnings("rawtypes")
    public WrittenPaymentMethodData writePaymentResponseMethodData(Map map) 
    throws Exception {
        log.debug("In ApipDaoImpl->writePaymentResponseMethodData");

        // updates transaction in apip database and returns that data as client object
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String piPaymentProfileRefNumber = null; //Payment Profile Reference Number
        String piPaymentProvider = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROVIDER, 0); //Payment Provider
        String piPaymentMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTMETHOD, 0); //Payment Method
        String piPaymentSubMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTSUBMETHOD, 0); //Payment Sub Method
        String piCardExpMonth = getStringParameterForDB(map, ApipServletConstants.DB_CARDEXPMONTH, 0); //Input Card Expiry Month
        String piCardExpYear = getStringParameterForDB(map, ApipServletConstants.DB_CARDEXPYEAR, 0); //Input Card Expiry Year
        String piTxnReferenceNumberType = getStringParameterForDB(map, ApipServletConstants.DB_TXNREFERENCENUMBERTYPE, 0); //Input Transaction Reference Number Type
        String piTxnReferenceNumber = getStringParameterForDB(map, ApipServletConstants.DB_TXNREFERENCENUMBER, 0); //Input Transaction Reference Number
        String piResponse = getStringParameterForDB(map, ApipServletConstants.DB_RESPONSE, 0); //Input Transaction Response
        Integer piIsAvsValid = getIntParameterForDB(map, ApipServletConstants.DB_ISAVSVALID); //Flag indicating whether the AVS check was successful
        Integer piIsCvValid = getIntParameterForDB(map, ApipServletConstants.DB_ISCVVALID); //Flag indicating whether the Card Verification Code check was successful
        String piTxnResultCode = getStringParameterForDB(map, ApipServletConstants.DB_TXNRESULTCODE, 0); //Code returned from processor which indicates whether transaction was approved/declined
        String piAVSResultCode = getStringParameterForDB(map, ApipServletConstants.DB_AVSRESULTCODE, 0); //AVS response code returned from processor
        String piCVResultCode = getStringParameterForDB(map, ApipServletConstants.DB_CVRESULTCODE, 0); //Card Verification response code returned from processor
        String piApprovalCode = getStringParameterForDB(map, ApipServletConstants.DB_APPROVALCODE, 0); //Authorization code returned from processor when transaction is approved
        String piDeclinedMessage = getStringParameterForDB(map, ApipServletConstants.DB_DECLINEDMESSAGE, 0); //Message returned from processor describing reason for decline of transaction
        String piTxnNumber = getStringParameterForDB(map, ApipServletConstants.DB_TXNNUMBER, 0); //Number returned by processor to uniquely identify transaction
        String piMODIFIEDBY = getStringParameterForDB(map, ApipServletConstants.DB_MODIFIEDBY, 0); //Application/Process Name which is creating this record
        String piProviderPaymentProfileToken = getStringParameterForDB(map, ApipServletConstants.DB_PROVIDER_TOKEN, 0);
        Boolean isNewPaymentToken = new Integer(1).equals(getIntParameterForDB(map, ApipServletConstants.DB_IS_NEW_TOKEN));
        String card_first_six = getStringParameterForDB(map, ApipServletConstants.DB_CARDFIRSTSIX, 0);
        String card_last_four = getStringParameterForDB(map, ApipServletConstants.DB_CARDLASTFOUR, 0);
        
        // if a payment token was passed back, add that as a new payment method and add to written data
        WrittenPaymentMethodData methodData = null;
        if (isNewPaymentToken) {
            
            piPaymentProfileRefNumber = this.apipInsertPaymentProfile(piProviderPaymentProfileToken, piPaymentProvider,
                                                                      piPaymentMethod, piPaymentSubMethod, now,
                                                                      piMODIFIEDBY);
            
            methodData = new WrittenPaymentMethodData(piProviderPaymentProfileToken, piPaymentProvider, piPaymentMethod,
                                                      piPaymentSubMethod, now, piMODIFIEDBY, piPaymentProfileRefNumber);

            
        }
        return methodData;
    }
    
    @SuppressWarnings("rawtypes")
    public WrittenPaymentData writePaymentResponseData(Map map, WrittenPaymentMethodData methodData) 
    throws Exception {
        log.debug("In ApipDaoImpl->writePaymentResponseData");

        // updates transaction in apip database and returns that data as client object
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String piPaymentProfileRefNumber = null;
        if(null!=methodData){
            piPaymentProfileRefNumber = methodData.getPaymentProfileRefNumber(); //Payment Profile Reference Number
        }
        String piPaymentProvider = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROVIDER, 0); //Payment Provider
        String piPaymentMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTMETHOD, 0); //Payment Method
        String piPaymentSubMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTSUBMETHOD, 0); //Payment Sub Method
        String piCardExpMonth = getStringParameterForDB(map, ApipServletConstants.DB_CARDEXPMONTH, 0); //Input Card Expiry Month
        String piCardExpYear = getStringParameterForDB(map, ApipServletConstants.DB_CARDEXPYEAR, 0); //Input Card Expiry Year
        String piTxnReferenceNumberType = getStringParameterForDB(map, ApipServletConstants.DB_TXNREFERENCENUMBERTYPE, 0); //Input Transaction Reference Number Type
        String piTxnReferenceNumber = getStringParameterForDB(map, ApipServletConstants.DB_TXNREFERENCENUMBER, 0); //Input Transaction Reference Number
        String piResponse = getStringParameterForDB(map, ApipServletConstants.DB_RESPONSE, 0); //Input Transaction Response
        BigDecimal piIsAvsValid = getBigDecimalParameterForDB(map, ApipServletConstants.DB_ISAVSVALID); //Flag indicating whether the AVS check was successful
        BigDecimal piIsCvValid = getBigDecimalParameterForDB(map, ApipServletConstants.DB_ISCVVALID); //Flag indicating whether the Card Verification Code check was successful
        String piTxnResultCode = getStringParameterForDB(map, ApipServletConstants.DB_TXNRESULTCODE, 0); //Code returned from processor which indicates whether transaction was approved/declined
        String piAVSResultCode = getStringParameterForDB(map, ApipServletConstants.DB_AVSRESULTCODE, 0); //AVS response code returned from processor
        String piCVResultCode = getStringParameterForDB(map, ApipServletConstants.DB_CVRESULTCODE, 0); //Card Verification response code returned from processor
        String piApprovalCode = getStringParameterForDB(map, ApipServletConstants.DB_APPROVALCODE, 0); //Authorization code returned from processor when transaction is approved
        String piDeclinedMessage = getStringParameterForDB(map, ApipServletConstants.DB_DECLINEDMESSAGE, 0); //Message returned from processor describing reason for decline of transaction
        String piTxnNumber = getStringParameterForDB(map, ApipServletConstants.DB_TXNNUMBER, 0); //Number returned by processor to uniquely identify transaction
        String piMODIFIEDBY = getStringParameterForDB(map, ApipServletConstants.DB_MODIFIEDBY, 0); //Application/Process Name which is creating this record
        String piProviderPaymentProfileToken = getStringParameterForDB(map, ApipServletConstants.DB_PROVIDER_TOKEN, 0);
        Boolean isNewPaymentToken = new Integer(1).equals(getIntParameterForDB(map, ApipServletConstants.DB_IS_NEW_TOKEN));
        String piFirstSix = getStringParameterForDB(map, ApipServletConstants.DB_CARDFIRSTSIX, 0);
        String piLastFour = getStringParameterForDB(map, ApipServletConstants.DB_CARDLASTFOUR, 0);
        String piTxnStatus = getStringParameterForDB(map, ApipServletConstants.DB_TXN_STATUS, 0);
        String  piPaymentProviderAccountNumber = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROVIDERACCOUNTNUMBER, 0);
        String piPaymentProviderConfig=getStringParameterForDB(map, ApipServletConstants.DB_PAYMENT_PROVIDER_CONFIG, 0);
        //retrieve epayTxnId/txnReferenceNumber from piTxnReferenceNumber which is of type NA_<APP>_EPAYTXNID ex - NA_OEP2_200005
        String [] token = piTxnReferenceNumber.split("-");
        piTxnReferenceNumber= token[2];
        piTxnReferenceNumberType = piTxnReferenceNumberType + "-" + token[1];

        // write transaction data
        this.apipUpdatePaymentTxn(piTxnReferenceNumberType, piTxnReferenceNumber,piPaymentProfileRefNumber,piPaymentProviderAccountNumber,
                                  piPaymentProviderConfig,piCardExpMonth, piCardExpYear,
                                  piResponse, piIsAvsValid, piIsCvValid, piTxnResultCode, piAVSResultCode,
                                  piCVResultCode, piApprovalCode, piDeclinedMessage, piTxnNumber,
                                  piFirstSix, piLastFour, piPaymentMethod, piPaymentSubMethod, piTxnStatus, now, 
                                  piMODIFIEDBY);

        Integer isAvsValid = (piIsAvsValid == null ? null : piIsAvsValid.intValue());
        Integer isCvValid = (piIsCvValid == null ? null : piIsCvValid.intValue());
        
        WrittenPaymentData paymentData = new WrittenPaymentData(piCardExpMonth, piCardExpYear,
                                                                piTxnReferenceNumberType, piTxnReferenceNumber,
                                                                piResponse, isAvsValid, isCvValid, piTxnResultCode,
                                                                piAVSResultCode, piCVResultCode, piApprovalCode,
                                                                piDeclinedMessage, piTxnNumber, now, piMODIFIEDBY);
        
        paymentData.setPaymentProfileRefNumber(piPaymentProfileRefNumber);
        paymentData.setPaymentMethodData(methodData);
        paymentData.setPaymentProviderAccountNumber(piPaymentProviderAccountNumber);
        paymentData.setPaymentProviderConfig(piPaymentProviderConfig);

        return paymentData;
    }

    @SuppressWarnings("rawtypes")
    public WrittenPaymentMethodData writePaymentMethodUpdateRequestData(Map map) 
    throws Exception {
        log.debug("In ApipDaoImpl->writePaymentMethodUpdateRequestData");
        
        java.sql.Date now = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        String piMODIFIEDBY = getStringParameterForDB(map, ApipServletConstants.DB_MODIFIEDBY, 0); //Application/Process Name which is creating this record
        String piPaymentProfileRefNumber = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROFILEREFNUMBER, 0); //Payment Profile Reference Number
        
        // we decided not to do the update (i.e. end-date) on the request, but rather
        // to have the client app do this via the service call.
        //this.apipUpdatePaymentProfile(piPaymentProfileRefNumber, now, piMODIFIEDBY);
        
        WrittenPaymentMethodData updateData = 
            new WrittenPaymentMethodData(now, piMODIFIEDBY, piPaymentProfileRefNumber);
        
        return updateData;
    }

    @SuppressWarnings("rawtypes")
    public WrittenPaymentMethodData writePaymentMethodUpdateResponseData(Map map) 
    throws Exception {
        log.debug("In ApipDaoImpl->writePaymentMethodUpdateResponseData");

        Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        String piProviderPaymentProfileToken = getStringParameterForDB(map, ApipServletConstants.DB_PROVIDER_TOKEN, 0); // Provider Payment Profile Token
        String piPaymentProvider = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTPROVIDER, 0); //Payment Provider
        String piPaymentMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTMETHOD, 0); // Payment Method
        String piPaymentSubMethod = getStringParameterForDB(map, ApipServletConstants.DB_PAYMENTSUBMETHOD, 0); //Payment Sub Method
        String piCREATEDBY = getStringParameterForDB(map, ApipServletConstants.DB_CREATEDBY, 0); //Application/Process Name which is creating this record
        String piPaymentProfileRefNumber = "";
        Boolean isNewPaymentToken = new Integer(1).equals(getIntParameterForDB(map, ApipServletConstants.DB_IS_NEW_TOKEN));
        
        if (isNewPaymentToken) {

            // write profile information to database
            piPaymentProfileRefNumber = this.apipInsertPaymentProfile(piProviderPaymentProfileToken, piPaymentProvider,
                                                                      piPaymentMethod, piPaymentSubMethod, now,
                                                                      piCREATEDBY);

            // return written data with reference id
            WrittenPaymentMethodData updateData = new WrittenPaymentMethodData(piProviderPaymentProfileToken,
                                                                               piPaymentProvider, piPaymentMethod,
                                                                               piPaymentSubMethod, now, piCREATEDBY,
                                                                               piPaymentProfileRefNumber);
            updateData.setApprovalCode("APPROVED");
            return updateData;
        } else {
            return null;
        }
        
        
    }

    /**
     * Spring wired
     * @param dataSource
     */
    @Resource(name = "apipDataSource")
    public void setDataSource(DataSource dataSource) {
        String schemaName = PropertiesLoader.getUrlFromPropertiesFile("schemaName", ApipPluginConstants.APIP_FWD_FILE_NAME);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
        
        this.procApipGetPaymentProfile = new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName("APIPLIBPKG")
        .withProcedureName("GetPaymentProfile");
       
        this.procApipInsertPaymentProfile = new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName("APIPLIBPKG")
        .withProcedureName("InsertPaymentProfile");
        
        this.procApipUpdatePaymentProfile = new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName("APIPLIBPKG")
        .withProcedureName("UpdatePaymentProfile");
        
        this.procApipInsertPaymentTxn = new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName("APIPLIBPKG")
        .withProcedureName("InsertPaymentTxn");
        
        this.procApipUpdatePaymentTxn = new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName("APIPLIBPKG")
        .withProcedureName("UpdatePaymentTxn");
    }

    private void apipUpdatePaymentTxn(
        String piTxnReferenceNumberType, //Input Transaction Reference Number Type
        String piTxnReferenceNumber, //Input Transaction Reference Number
        String piPaymentProfileRefNumber, //Input Payment Profile ref Number
        String piPaymentProviderAccountNumber,
        String piPaymentProviderConfig,
        String piCardExpMonth, //Input Card Expiry Month
        String piCardExpYear, //Input Card Expiry Year
        String piResponse, //Input Transaction Response
        BigDecimal piIsAvsValid, //Flag indicating whether the AVS check was successful
        BigDecimal piIsCvValid, //Flag indicating whether the Card Verification Code check was successful
        String piTxnResultCode, //Code returned from processor which indicates whether transaction was approved/declined
        String piAVSResultCode, //AVS response code returned from processor
        String piCVResultCode, //Card Verification response code returned from processor
        String piApprovalCode, //Authorization code returned from processor when transaction is approved
        String piDeclinedMessage, //Message returned from processor describing reason for decline of transaction
        String piTxnNumber, //Number returned by processor to uniquely identify transaction
        String piFirstSix, //Input First 6 digits of card number
        String piLastFour, //Input Last 4 digits of card number
        String piPaymentMethod, //Payment Method
        String piPaymentSubMethod, //Payment Sub Method
        String piTxnStatus, //Input Transaction Status
        java.sql.Timestamp piTxnDate, //Date/Time returned from processor indicating the actual time the transaction was processed
        String piModifiedBy //Application/Process Name which is updating this record
        ) 
    throws Exception {
        
        Map out = null;
        
        MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("piTxnReferenceNumberType", piTxnReferenceNumberType)
        .addValue("piTxnReferenceNumber", piTxnReferenceNumber)
        .addValue("piPaymentProfileRefNumber",piPaymentProfileRefNumber)
        .addValue("piPaymentProviderAccountNumber",piPaymentProviderAccountNumber)
        .addValue("piPaymentProviderConfig",piPaymentProviderConfig)
        .addValue("piCardExpMonth", piCardExpMonth)
        .addValue("piCardExpYear", piCardExpYear)
        .addValue("piResponse", piResponse)
        .addValue("piIsAvsValid", piIsAvsValid)
        .addValue("piIsCvValid", piIsCvValid)
        .addValue("piTxnResultCode", piTxnResultCode)
        .addValue("piAVSResultCode", piAVSResultCode)
        .addValue("piCVResultCode", piCVResultCode)
        .addValue("piApprovalCode", piApprovalCode)
        .addValue("piDeclinedMessage", piDeclinedMessage)
        .addValue("piTxnNumber", piTxnNumber)
        .addValue("piTxnDate", piTxnDate, Types.TIMESTAMP)
        .addValue("piModifiedBy", piModifiedBy)
        .addValue("piFirstSix", piFirstSix)
        .addValue("piLastFour", piLastFour)
        .addValue("piPaymentMethod", piPaymentMethod)
        .addValue("piPaymentSubMethod", piPaymentSubMethod)
        .addValue("piTxnStatus", piTxnStatus)
        ;

        out = this.procApipUpdatePaymentTxn.execute(in);
        return;
    }

    public PaymentProfile apipGetPaymentProfile(String piPaymentProfileRefNumber) 
    throws Exception {
        String poProviderPaymentProfileToken; //Provider Payment Profile Token as the output
        String poPaymentProvider; //Payment Provider as the output
        String poPaymentMethod; //Payment Method as the output
        String poPaymentSubMethod; //Payment Sub Method as the output
        java.util.Date poStartDate; //Payment Profile Start Date as the output
        java.util.Date poEndDate; //Payment Profile End Date as the output
        
        @SuppressWarnings("rawtypes")
        Map out = null;
        
        MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("piPaymentProfileRefNumber", piPaymentProfileRefNumber);

        out = this.procApipUpdatePaymentTxn.execute(in);
        
        poProviderPaymentProfileToken = (String) out.get("poProviderPaymentProfileToken");
        poPaymentProvider = (String) out.get("poPaymentProvider");
        poPaymentMethod = (String) out.get("poPaymentMethod");
        poPaymentSubMethod = (String) out.get("poPaymentSubMethod");
        poStartDate = (java.util.Date) out.get("poStartDate");
        poEndDate = (java.util.Date) out.get("poEndDate");

        return new PaymentProfile(
                       poProviderPaymentProfileToken,
                       poPaymentProvider,
                       poPaymentMethod,
                       poPaymentSubMethod,
                       poStartDate,
                       poEndDate);
    }

    @Transactional
    private String apipInsertPaymentProfile(
        String piProviderPaymentProfileToken, // Provider Payment Profile Token
        String piPaymentProvider, // Payment Provider
        String piPaymentMethod, // Payment Method
        String piPaymentSubMethod, // Payment Sub Method
        Timestamp piStartDate, // Payment Profile Start Date
        String piCREATEDBY // Application/Process Creating the Payment Profile
        )  
    throws Exception {

        String piPaymentProfileRefNumber = null; // Payment Profile Reference Number
        Map out = null;
        
        MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("piProviderPaymentProfileToken", piProviderPaymentProfileToken)
        .addValue("piPaymentProvider", piPaymentProvider)
        .addValue("piPaymentMethod", piPaymentMethod)
        .addValue("piPaymentSubMethod", piPaymentSubMethod)
        .addValue("piStartDate", piStartDate,Types.TIMESTAMP)
        .addValue("piCREATEDBY", piCREATEDBY)
        .addValue("piPaymentProfileRefNumber", piPaymentProfileRefNumber);

        out = this.procApipInsertPaymentProfile.execute(in);
        piPaymentProfileRefNumber = (String) out.get("piPaymentProfileRefNumber");
        
        return piPaymentProfileRefNumber;
  
    }
    
    // this effectively ends the existing profile
    @SuppressWarnings("unused")
    private void apipUpdatePaymentProfile(
        String piPaymentProfileRefNumber, // Payment Profile Reference Number
        java.util.Date piEndDate, // Payment Profile End Date
        String piModifiedBy) // Application/Process Name which is updating this record
    throws Exception {
        
        Map out = null;
        
        MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("piPaymentProfileRefNumber", piPaymentProfileRefNumber)
        .addValue("piEndDate", piEndDate)
        .addValue("piModifiedBy", piModifiedBy);

        out = this.procApipUpdatePaymentProfile.execute(in);
    }
    
    private void apipInsertPaymentTxn(
        String piPaymentProfileRefNumber, //Payment Profile Reference Number
        String piPaymentProviderAccountNumber, //Payment Provider Account Number
        String piPaymentProvider, //Payment Provider
        String piFirstSix, //Input First 6 digits of card number
        String piLastFour, //Input Last 4 digits of card number
        String piPaymentMethod, //Payment Method
        String piPaymentSubMethod, //Payment Sub Method
        String piCardExpMonth, //Input Card Expiry Month
        String piCardExpYear, //Input Card Expiry Year
        String piBillingName, //Input Card Holder Name
        String piBillingCompany, //Input Card Holder Company
        String piBillingAddress1, //Input Card Holder Billing Address1
        String piBillingAddress2, //Input Card Holder Billing Address2
        String piBillingCity, //Input Card Holder Billing City
        String piBillingState, //Input Card Holder Billing State
        String piBillingPostalCode, //Input Card Holder Billing Postal Code
        String piBillingCountry, //Input Card Holder Billing Country
        String piBillingPhone, //Input Card Holder Billing Phone
        String piBillingAddressNumber, //Input Card Holder Billing Address Number
        String piBillingEmailAddress, //Input Card Holder Billing Email Address
        String piRequestingApplication, //Input Requesting Application of the Epay Transaction
        String piBusinessPurpose, //Input Business purpose of the Epay Transaction
        String piOriginType, //Input Origin Type
        String piTxnReferenceNumberType, //Input Transaction Reference Number Type
        String piTxnReferenceNumber, //Input Transaction Reference Number
        String piRequesterType, //Input Requester Type
        String piRequesterNumber, //Input Requester Number
        String piRequesteripAddress, //Input Requester IP Address
        String piTxnType, //Input Transaction Type
        Integer piIsRecurring, //Input IS Recurring
        BigDecimal piTxnAmount, //Input Transaction Amount
        String piTxnCurrency, //Input Transaction Currency
        String piRequest, //Transaction Request
        String piResponse, //Input Transaction Response
        Integer piIsAvsValid, //Flag indicating whether the AVS check was successful
        Integer piIsCvValid, //Flag indicating whether the Card Verification Code check was successful
        String piTxnResultCode, //Code returned from processor which indicates whether transaction was approved/declined
        String piAVSResultCode, //AVS response code returned from processor
        String piCVResultCode, //Card Verification response code returned from processor
        String piApprovalCode, //Authorization code returned from processor when transaction is approved
        String piDeclinedMessage, //Message returned from processor describing reason for decline of transaction
        String piTxnNumber, //Number returned by processor to uniquely identify transaction
        java.sql.Date piTxnDate, //Date/Time returned from processor indicating the actual time the transaction was processed
        String piRevenueSourceCode, //Revenue Source Code
        String piCREATEDBY, //Application/Process Name which is creating this record
        String piPaymentProviderConfig, //Payment Provider Config
        String piTxnStatus //Input Transaction Status
    ) throws Exception {
        
        Map out = null;

        MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("piPaymentProfileRefNumber", piPaymentProfileRefNumber)
        .addValue("piPaymentProviderAccountNumber", piPaymentProviderAccountNumber)
        .addValue("piPaymentProvider", piPaymentProvider)
        .addValue("piFirstSix", piFirstSix)
        .addValue("piLastFour", piLastFour)
        .addValue("piPaymentMethod", piPaymentMethod)
        .addValue("piPaymentSubMethod", piPaymentSubMethod)
        .addValue("piCardExpMonth", piCardExpMonth)
        .addValue("piCardExpYear", piCardExpYear)
        .addValue("piBillingName", piBillingName)
        .addValue("piBillingCompany", piBillingCompany)
        .addValue("piBillingAddress1", piBillingAddress1)
        .addValue("piBillingAddress2", piBillingAddress2)
        .addValue("piBillingCity", piBillingCity)
        .addValue("piBillingState", piBillingState)
        .addValue("piBillingPostalCode", piBillingPostalCode)
        .addValue("piBillingCountry", piBillingCountry)
        .addValue("piBillingPhone", piBillingPhone)
        .addValue("piBillingAddressNumber", piBillingAddressNumber)
        .addValue("piBillingEmailAddress", piBillingEmailAddress)
        .addValue("piRequestingApplication", piRequestingApplication)
        .addValue("piBusinessPurpose", piBusinessPurpose)
        .addValue("piOriginType", piOriginType)
        .addValue("piTxnReferenceNumberType", piTxnReferenceNumberType)
        .addValue("piTxnReferenceNumber", piTxnReferenceNumber)
        .addValue("piRequesterType", piRequesterType)
        .addValue("piRequesterNumber", piRequesterNumber)
        .addValue("piRequesteripAddress", piRequesteripAddress)
        .addValue("piTxnType", piTxnType)
        .addValue("piIsRecurring", piIsRecurring)
        .addValue("piTxnAmount", piTxnAmount)
        .addValue("piTxnCurrency", piTxnCurrency)
        .addValue("piRequest", piRequest)
        .addValue("piResponse", piResponse)
        .addValue("piPaymentProviderConfig", piPaymentProviderConfig)
        .addValue("piTxnStatus", piTxnStatus)
        .addValue("piIsAvsValid", piIsAvsValid)
        .addValue("piIsCvValid", piIsCvValid)
        .addValue("piTxnResultCode", piTxnResultCode)
        .addValue("piAVSResultCode", piAVSResultCode)
        .addValue("piCVResultCode", piCVResultCode)
        .addValue("piApprovalCode", piApprovalCode)
        .addValue("piDeclinedMessage", piDeclinedMessage)
        .addValue("piTxnNumber", piTxnNumber)
        .addValue("piTxnDate", piTxnDate)
        .addValue("piRevenueSourceCode", piRevenueSourceCode)
        .addValue("piCREATEDBY", piCREATEDBY);

        out = this.procApipInsertPaymentTxn.execute(in);
        
    }

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
