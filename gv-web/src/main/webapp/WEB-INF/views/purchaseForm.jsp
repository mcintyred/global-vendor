<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Purchase A Product</title>
<style>
	th, td {
		border: 1px solid black;
	}
</style>
</head>
<body>

	<fieldset>
		<legend>Purchase ${ product.name }</legend>

		<form method="post">
		
			Qty : <input name="qty"/> <input type="submit" value="Purchase"/>
			
		</form>
</fieldset>
</body>
</html>