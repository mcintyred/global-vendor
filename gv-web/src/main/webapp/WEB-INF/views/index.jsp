<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
<head>
<title>Global Vendor Distributor Homepage</title>
<link href="./resources/styles.css" type="text/css" rel="stylesheet"/>
</head>
<body>

	<h1>Distributor Home Page</h1>
	<div class="widget">
		<h2>Product Catalog</h2>

		<table>
			<tr>
				<th colspan="3">Product</th>
				<th>Total Stock</th>
			</tr>
			
			<tr>
				<th>Id</th><th>Name</th><th>Description</th><th></th>
			</tr>

			<c:forEach var="product" items="${ products }">
				<tr>
					<td>${ product.id }</td>
					<td>${ product.name }</td>
					<td>${ product.description }</td>
					
					<td>${ stockLevels.get(product) + 0}</td>
					<td>
						<a href="./product/${ product.id }/purchase.html">Purchase</a>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>
