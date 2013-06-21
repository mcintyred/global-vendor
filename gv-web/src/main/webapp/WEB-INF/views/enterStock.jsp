<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
<head>

<link href="<%= request.getContextPath() %>/resources/styles.css" type="text/css" rel="stylesheet"/>

<title>Enter Stock Level</title>
</head>
<body>

	
	<div class="widget">
		<h2>Stock Levels For ${ product.name }</h2>			
		<form:form method="post" modelAttribute="stockForm">
		
			<table>
				<tr><th>Warehouse</th><th>Stock</th></tr>
		
				<c:forEach var="warehouse" items="${ warehouseMap.values() }">
				<tr>
					<td>${ warehouse.name }</td>
					<td><form:input path="stockLevels[${warehouse.name }]" value="${ stockLevels[warehouse.name] }"/></td>
				</tr>
								
				</c:forEach>
			
			</table>
			<br/>
			<input type="submit" value="Save"/>
		</form:form>
		
	</div>

	<a class="ret" href="<%= request.getContextPath() %>/admin.html">Return to admin page</a>

</body>
</html>
