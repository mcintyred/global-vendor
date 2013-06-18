Global Vendor
=====================

A small project to demonstrate the use of a variety of Pivotal technologies.

gv-api  - Defines the service interfaces and POJOs common to all projects
gv-core - Implementations of core services
gv-products-jpa - An implementation of the ProductService using Spring Data JPA for persistence.
gv-warehouse-jpa - An implementation of the WarehouseService using Spring Data JPA for persistence.
gv-warehouse-node - Uses Spring Integration to create a standalone WarehouseService which communicates with the core using messaging over RabbitMQ.
gv-web - The main web application used to interact with the various WarehouseService implementations. Also provides a SOAP service.
