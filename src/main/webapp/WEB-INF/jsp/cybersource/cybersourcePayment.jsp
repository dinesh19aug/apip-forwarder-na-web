<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%@ include file="security.jsp" %>

<%
    Map params = (Map) request.getAttribute("gatewayParameterMap");
    HashMap<String,String> vendorMap = new HashMap<String,String>();
    String redirectUrl = "", securityKey = "";

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
%>
<body onload="document.forms[0].submit();">
<form action="<%=redirectUrl%>" method="post" target="_parent"/>

<% for (String param : vendorMap.keySet()) {
    out.print("<input type=\"hidden\" id=\"" + param + "\" name=\"" + param + "\" value=\"" + vendorMap.get(param) + "\"/>\n");
}
    out.print("<input type=\"hidden\" id=\"signature\" name=\"signature\" value=\"" + sign(vendorMap, securityKey) + "\"/>\n");
%>
</form>
</body>
</html>

