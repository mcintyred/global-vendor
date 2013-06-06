<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
<head>
<title>Global Vendor Admin Homepage</title>
<style>
	th, td {
		border: 1px solid black;
	}
</style>
</head>
<body>


	<fieldset>
		<legend>Warehouses</legend>
		
		<form:form method="post" modelAttribute="warehouseForm" action="createWarehouse.html">
		
			<table>
				<tr><th>Id</th><th>Name</th><th>Type</th></tr>
		
				<c:forEach var="warehouse" items="${ warehouses }">
				<tr>
					<td>${ warehouse.id }</td>
					<td>${ warehouse.name }</td>
					<td>${ warehouse.serviceBindingName }</td>
				</tr>		
				</c:forEach>
				
				<tr>
					<td></td>
					<td><form:input path="name" value="${ name }"/></td>
					<td>
						<form:select path="serviceName">
							<form:options items="${ serviceNames }"/>
						</form:select>
					</td>
				</tr>
			
			</table>
			
			<input type="submit" value="Add"/>
		</form:form>
		
	</fieldset>
	
	<fieldset>
		<legend>Stock Levels</legend>

		<table>
			<tr>
				<th colspan="3">Product</th>
				<th colspan="${ warehouses.size() }" }>Warehouse Stock</th>
			</tr>
			
			<tr>
			<th>Id</th><th>Name</th><th>Description</th>

			<c:forEach var="warehouse" items="${ warehouses }">
					<td>${ warehouse.name }</td>
			</c:forEach>
			</tr>

			<c:forEach var="product" items="${ products }">
				<tr>
					<td>${ product.id }</td>
					<td>${ product.name }</td>
					<td>${ product.description }</td>
					
					<c:forEach var="warehouse" items="${ warehouses }">
						<td>${ stockLevels.get(product).get(warehouse.id) + 0}</td>
					</c:forEach>
					
					<td>
						<a href="./product/${ product.id }/stock.html">Set Stock Levels</a>
					</td>
					<td>
						<a href="./product/${ product.id }/purchase.html">Purchase</a>
					</td>
				</tr>
			</c:forEach>

		</table>

	</fieldset>

	<form method="post" action="./createProduct.html">
		<fieldset>
			<legend>Create a product</legend>

			<label>Name</label>
			<br/>
			<input type="text" value="${ product.name }"
						name="name" /><br/>
			<label>Description</label>
			<br/>
			<textarea name="description"></textarea><br/>
			<input type="button" value="Add"
						onclick="submit()" />
		</fieldset>
	</form>



</body>
</html>
