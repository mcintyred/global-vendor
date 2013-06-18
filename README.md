<h1>Global Vendor</h1>

<p>
A small project to demonstrate the use of a variety of Pivotal technologies.
</p>

<ul>
<li>gv-api  - Defines the service interfaces and POJOs common to all projects</li>
<li>gv-core - Implementations of core services</li>
<li>gv-products-jpa - An implementation of the ProductService using Spring Data JPA for persistence.</li>
<li>gv-warehouse-jpa - An implementation of the WarehouseService using Spring Data JPA for persistence.</li>
<li>gv-warehouse-node - Uses Spring Integration to create a standalone WarehouseService which communicates with the core using messaging over RabbitMQ.</li>
<li>gv-web - The main web application used to interact with the various WarehouseService implementations. Also provides a SOAP service.</li>
</ul>
