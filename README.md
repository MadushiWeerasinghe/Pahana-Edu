# Pahana Edu — Online Billing (Service + Web)

Two NetBeans-importable Maven projects:
- `pahana-edu-service` — Jakarta REST API (GlassFish)
- `pahana-edu-web` — JSP/Servlet UI that consumes the REST API

## Prereqs
- Apache NetBeans 21
- JDK 21
- GlassFish 7.x
- XAMPP (MySQL 8+ running locally)


## Database
1. Start XAMPP MySQL.
2. Run `pahana-edu-service/src/main/resources/mysql_schema.sql` in phpMyAdmin.
3. Replace the placeholder password hashes for `admin` and `cashier`:
   

## Deploy order
1. Build and deploy `pahana-edu-service` first. Context root `pahana-edu-service`.
   - Test: `GET http://localhost:8080/pahana-edu-service/api/items`
2. Build and deploy `pahana-edu-web`. Context root `pahana-edu-web`.
   - Visit: `http://localhost:8080/pahana-edu-web/login.jsp`

## Roles
- Admin (`admin`/`admin123`): full CRUD for customers/items via REST (UI shows lists; extend with forms if required).
- Cashier (`cashier`/`cashier123`): create bills & view data.

