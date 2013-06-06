<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Stock Alerts</title>
<style>
	th, td {
		border: 1px solid black;
	}
</style>
</head>
<body>

<table>
<tr>
<th>Warehouse<th>Product</th><th>Qty</th><th>Threshold</th>
<tr>

<c:forEach var="alert" items="${ stockAlerts }">
	<tr>
		<td>${ alert.warehouse.name }</td>
		<td>${ alert.product.name }</td>
		<td>${ alert.stockLevel } </td>
		<td>${ alert.threshold }</td>
</c:forEach>

</table>

</body>
</html>