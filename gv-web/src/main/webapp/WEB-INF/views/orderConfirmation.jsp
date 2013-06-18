<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Order Confirmation</title>
<link href="<%= request.getContextPath() %>/resources/styles.css" type="text/css" rel="stylesheet"/>
</head>
<body>

	<div class="widget">

	<h2>
	<c:if test="${ confirmation.partial }">
		Partial Order Confirmation
	</c:if>
	<c:if test="${ !confirmation.partial }">
		Order Confirmation
	</c:if>
	</h2>
	
	
	<table>
		<tr>
			<th>Product</th>
			<th>Qty</th>
			<th>Shipping</th>
			<th>From</th>
		<tr>

			<c:forEach var="shipment" items="${ confirmation.shipments }">
				<c:forEach var="line" items="${ shipment.lines }">
					<tr>
						<td>${ line.product.name }</td>
						<td>${ line.qty }</td>
						<td>${ line.shipDate }</td>
						<td>${ shipment.warehouse.name }</td>
					</tr>
				</c:forEach>
			</c:forEach>
	</table>

	<a class="ret" href="<%= request.getContextPath() %>/index.html">Return to homepage</a>
	</div>
</body>
</html>