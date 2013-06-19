<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
<head>
<title>Global Vendor Admin Homepage</title>
<link href="./resources/styles.css" type="text/css" rel="stylesheet"/>
</head>
<body>


	<h1>Administration Page</h1>
	
	<div class="widget">
		<h2>Warehouses</h2>
		
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
					<td>			
						<input type="submit" value="Add"/>
					</td>
					<td><form:input path="name" value="${ name }"/></td>
					<td>
						<form:select path="serviceName">
							<form:options items="${ serviceNames }"/>
						</form:select>
					</td>
				</tr>
			
			</table>
		</form:form>
		
	</div>
	
	<div class="widget">
		<h2>Stock Levels</h2>

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
				</tr>
			</c:forEach>

		</table>

	</div>

	<div class="widget">
		<h2>Create a product</h2>

		<form method="post" action="./createProduct.html">
			<label>Name</label>
			<br/>
			<input type="text" value="${ product.name }" class="name"
						name="name" /><br/>
			<label>Description</label>
			<br/>
			<textarea name="description"></textarea><br/>
			<input type="button" value="Add"
						onclick="submit()" />
		</form>
	</div>

</body>
</html>
