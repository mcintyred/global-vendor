<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
<head>
<title>Enter Stock Level</title>
<style>
	th, td {
		border: 1px solid black;
	}
</style>
</head>
<body>

	<fieldset>
		<legend>Stock Levels For ${ product.name }</legend>
		
		<form:form method="post" modelAttribute="stockForm">
		
			<table>
				<tr><th>Warehouse</th><th>Stock</th></tr>
		
				<c:forEach var="warehouse" items="${ warehouseMap.values() }">
				<tr>
					<td>${ warehouse.name }</td>
					<td><form:input path="stockLevels[${warehouse.id }]" value="${ stockLevels[warehouse.id] }"/></td>
				</tr>
								
				</c:forEach>
			
			</table>
			
			<input type="submit" value="Save"/>
		</form:form>
		
	</fieldset>

	<a class="ret" href="<%= request.getContextPath() %>/admin.html">Return to admin page</a>

</body>
</html>
