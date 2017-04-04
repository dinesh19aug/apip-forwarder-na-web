package com.apip.forwarder.dao;

import java.math.BigDecimal;
import java.util.Map;

import com.apip.bean.WrittenPaymentMethodData;
import com.apip.bean.PaymentProfile;
import com.apip.bean.WrittenPaymentData;

public interface ApipDao {

    @SuppressWarnings("rawtypes")
    public WrittenPaymentData writePaymentRequestData(Map parameterMap) throws Exception;
    
    
    @SuppressWarnings("rawtypes")
    public WrittenPaymentData writePaymentResponseData(Map parameterMap, WrittenPaymentMethodData methodData) throws Exception;
    public WrittenPaymentMethodData writePaymentResponseMethodData(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public WrittenPaymentMethodData writePaymentMethodUpdateRequestData(Map parameterMap) throws Exception;
    
    
    @SuppressWarnings("rawtypes")
    public WrittenPaymentMethodData writePaymentMethodUpdateResponseData(Map parameterMap) throws Exception;
    
    public PaymentProfile apipGetPaymentProfile(String piPaymentProfileRefNumber) throws Exception;


}
