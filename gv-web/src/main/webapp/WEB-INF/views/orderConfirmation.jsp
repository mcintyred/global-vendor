<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Order Confirmation</title>
<style>
	th, td {
		border: 1px solid black;
	}
</style>
</head>
<body>

<c:if test="${ confirmation.partial }">
	<h1>Partial Order Confirmation</h1>
</c:if>
<c:if test="${ !confirmation.partial }">
	<h1>Order Confirmation</h1>
</c:if>

<table>
<tr>
<th>Product</th><th>Qty</th><th>Shipping</th><th>From</th>
<tr>

<c:forEach var="shipment" items="${ confirmation.shipments }">
	<c:forEach var="line" items="${ shipment.lines }">
		<tr><td>${ line.product.name }</td><td>${ line.qty }</td><td>${ line.shipDate }</td><td>${ shipment.warehouse.name }</td></tr>
	</c:forEach>
</c:forEach>

</table>

</body>
</html>