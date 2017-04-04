<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%@ include file="security.jsp" %>
<head>
    <style type="text/css">
        #footer {
            position : absolute;
            bottom : 0;
            height : 40px;
            margin-top : 40px;
        }
    </style>
    <script type='text/javascript'>
        var invalidCardNum = '<spring:message code="error.invalidCardNum" text="default text" />';
        var invalidCardType = '<spring:message code="error.invalidCardType" text="default text" />';
        var invalidCvn = '<spring:message code="error.invalidCvn" text="default text" />';
        var invaliddate = '<spring:message code="error.invaliddate" text="default text" />';
        var mismatch='<spring:message code="error.mismatch" text="default text" />';

    </script>
    <link rel="stylesheet" href="../resources/css/creditCard.css"/>
    <script src="../resources/js/creditCard.js"></script>
    <script src="../resources/js/jquery-1.12.2.min.js"></script>
    <%--<fmt:setLocale value="fr_CA" scope="session"/>--%>
</head>
<%
    Map params = (Map) request.getAttribute("gatewayParameterMap");
    HashMap<String,String> vendorMap = new HashMap<String,String>();
    String redirectUrl = "", securityKey = "";
    String imgsrc ="";
    String cardImg = "../resources/images/cardrow.jpg";

    for (Object param : params.keySet()) {
        if (params.get(param) != null) {
            String value = params.get(param).toString();
            if (param.toString().equals("cybersource_url")) {
                redirectUrl = value;
            } else if (param.toString().equals("cybersource_key")) {
                securityKey = value;
            } else {
                vendorMap.put(param.toString(), value);
            }
        }
    }
    if(vendorMap.get("bill_to_address_country").equalsIgnoreCase("CA")){
        cardImg = "../resources/images/cardrow-ca.jpg";
    }

    if (vendorMap.get("merchant_defined_data1").equalsIgnoreCase("OEP2")){
        if(vendorMap.get("locale").equalsIgnoreCase("es-US")) {
            imgsrc = "../resources/images/OEP_HeaderFINAL_es.jpg";
        }else{
            imgsrc = "../resources/images/OEP_HeaderFINAL.jpg";
        }

    }else if (vendorMap.get("merchant_defined_data1").equalsIgnoreCase("OEPSFCA")){
        if(vendorMap.get("locale").equalsIgnoreCase("fr-CA")) {
            imgsrc = "../resources/images/OEP_Header_CA-FINAL_fr.png";
        }else{
            imgsrc = "../resources/images/OEP_Header_CA-FINAL.png";
        }
    }else if (vendorMap.get("merchant_defined_data1").equalsIgnoreCase("VOIPCP")) {
        if (vendorMap.get("locale").equalsIgnoreCase("fr-CA")) {
            imgsrc = "../resources/images/vcp_banner_fr.jpg";
        }else if(vendorMap.get("locale").equalsIgnoreCase("es-US")){
            imgsrc = "../resources/images/vcp_banner_es.jpg";
        }else {
            imgsrc = "../resources/images/vcp_banner_en.jpg";
        }
    }
%>
<body id="payment" onload="populateCCExpirationFields()">
<div id="header">
    <div class="container ">
        <img src='<%=imgsrc %>' style="width:840px;"/>
    </div>
</div>
<div class="container">
    <div id="payment_label" class="inner-container">
        <span class="info_label"><spring:message code="label.pmtInfo" text="default text" /></span>
    </div>

    <%--Left Container--%>
    <div class="left_column">
        <form action="<%=redirectUrl%>" method="post" target="_parent" onsubmit="return validateAndSubmit()"/>
        <% for (String param : vendorMap.keySet()) {
                  /*out.print("<span class=\"fieldName\">" + param + "</span>");
                        out.print("<span class=\"fieldValue\">" + vendorMap.get(param) + "</span>");*/
            out.print("<input type=\"hidden\" id=\"" + param + "\" name=\"" + param + "\" value=\"" + vendorMap.get(param) + "\"/>\n");
        }
            out.print("<input type=\"hidden\" id=\"signature\" name=\"signature\" value=\"" + sign(vendorMap, securityKey) + "\"/>\n");
        %>
        <div class="inner-container"><span class="input_label"><spring:message code="label.name" text="default text" /></span><span class="data"> <%=vendorMap.get("bill_to_forename")%> <%=vendorMap.get("bill_to_surname")%></span></div>
        <div class="inner-container"><span class="input_label"><spring:message code="label.billing" text="default text" /></span>
            <span class="data"><%=vendorMap.get("bill_to_address_line1")%><br><%=vendorMap.get("bill_to_address_city")%><br>
            <%=vendorMap.get("bill_to_address_state")%> <%=vendorMap.get("bill_to_address_postal_code")%></span>
        </div>

        <div id="inner-container-error" class="inner-container-error" style="display:none">
            <div id="errorDiv" class="error" style="display:none"></div>
        </div>
        <div class="inner-container">
            <span class="input_label">
                <spring:message code="label.cardType" text="default text" />*
            </span>
            <div class="lpadded">
                <table><tr>
                    <td>
                        <select id="cardOption">
                            <option value="001" selected>Visa</option>
                            <option value="002">MasterCard</option>
                            <option value="003">American Express</option>
                            <%
                                if(vendorMap.get("bill_to_address_country").equalsIgnoreCase("US")){
                            %>
                            <option value="004">Discover</option>
                            <%}%>
                        </select>
                    </td>
                    <td>
                        <img src="<%=cardImg%>">
                    </td>
                </tr></table>
            </div>
            <span class="input_label"><spring:message code="label.ccNumber" text="default text" />*</span><div class="lpadded"><input  type="text" name="ccnumber" id="ccnumber" maxlength="16"> </div>
            <span class="input_label"><spring:message code="label.cvv" text="default text" />*</span> <div class="lpadded"><input class="lpadded" type="text" name="cvv" id="cvv" maxlength="4" ></div>
            <span class="input_label"><spring:message code="label.expDate" text="default text" />*</span> <div class="lpadded"><select class="lpadded" name="expMonth" id="expMonth" ></select> - <select class="lpadded" name="expYear" id="expYear"></select></div>
            <input type="hidden" name="card_number" id="card_number">
            <input type="hidden" name="card_type" id="card_type">
            <input type="hidden" name="card_cvn" id="card_cvn">
            <input type="hidden" name="card_expiry_date" id="card_expiry_date"><br>

        </div>


        <table>
            <tbody>
            <tr>
                <td> <input type="submit" value="<spring:message code="label.pay" text="default text" />" style="display: block;margin : 0 auto;" /></td>
                <td><input type="button" name="cancel" value="<spring:message code="label.cancel" text="default text" />" onclick="window.location='/apip-forwarder/paymentCancel.html'" /></td>
            </tr>
            </tbody>
        </table>
        </form>
    </div>

    <%--Right Container--%>
    <div class="right_column">
        <span class="input_label"><spring:message code="label.totAmt" text="default text" /></span><div class="lpadded"><b> $<%=vendorMap.get("merchant_defined_data5")%></b></div>
    </div>
    <div id="footer" class="copyright">Copyright 2016, ACN. All rights reserved. ACN: 1000 Progress Pl NC 28025</div>
</div>



</html>
