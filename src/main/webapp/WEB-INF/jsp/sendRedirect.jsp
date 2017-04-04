<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%
   Map params = (Map) request.getAttribute("redirectMap");
   HashMap<String,String> redirectMap = new HashMap<String,String>();
   String redirectUrl = "";
   for (Object param : params.keySet()) {
       if (params.get(param) != null) {
            String value = params.get(param).toString();
            if (param.toString().equals("redirectUrl")) {
                redirectUrl = value;
            } else {
                redirectMap.put(param.toString(), value);
            }
       }
    } 
%>
<body onload="document.forms[0].submit();">
<form action="<%=redirectUrl%>" method="post" target="_parent"/>
    <% for (String param : redirectMap.keySet()) {
            out.print("<input type=\"hidden\" id=\"" + param + "\" name=\"" + param + "\" value=\"" + redirectMap.get(param) + "\"/>\n");
        }
    %>
</form>
</body>
</html>


