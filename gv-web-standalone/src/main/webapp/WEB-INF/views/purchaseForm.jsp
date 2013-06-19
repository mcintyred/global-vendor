<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Purchase A Product</title>
<link href="<%= request.getContextPath() %>/resources/styles.css" type="text/css" rel="stylesheet"/>
</head>
<body>

	<div class="widget">
		<h2>Purchase ${ product.name }</h2>

		<form method="post">

			Qty : <input name="qty" /> <input type="submit" value="Purchase" />

		</form>
	
		<a class="ret" href="<%= request.getContextPath() %>/index.html">Return to homepage</a>
	
	</div>
	
</body>
</html>