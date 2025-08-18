# Pahana Edu — Online Billing (Service + Web)

Two NetBeans-importable Maven projects:
- `pahana-edu-service` — Jakarta REST API (GlassFish)
- `pahana-edu-web` — JSP/Servlet UI that consumes the REST API

## Prereqs
- Apache NetBeans 21
- JDK 21
- GlassFish 7.x
- XAMPP (MySQL 8+ running locally)

> Note: GlassFish 7 works best with JDK 17. If you keep JDK 21, ensure your GlassFish build supports it. Otherwise, run server on JDK 17 while compiling source at 21.

## Database
1. Start XAMPP MySQL.
2. Run `pahana-edu-service/src/main/resources/mysql_schema.sql` in phpMyAdmin.
3. Replace the placeholder password hashes for `admin` and `cashier`:
   - Use the small helper below (run once in any Java REPL) to produce a hash:
   ```java
   System.out.println(edu.pahana.service.util.PasswordUtil.hash("admin123"));
   ```
   Update the two `INSERT` lines accordingly and re-run them, or `UPDATE users SET ...`.

## Configure DB credentials
If your MySQL user/pass differ, pass system properties to GlassFish or app:
```
- DDB_URL=jdbc:mysql://localhost:3306/pahana_edu?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
- DDB_USER=root
- DDB_PASS=yourpassword
```
(Use `asadmin create-jvm-options` to add them to the domain if desired.)

## Deploy order
1. Build and deploy `pahana-edu-service` first. Context root `pahana-edu-service`.
   - Test: `GET http://localhost:8080/pahana-edu-service/api/items`
2. Build and deploy `pahana-edu-web`. Context root `pahana-edu-web`.
   - Visit: `http://localhost:8080/pahana-edu-web/login.jsp`

## Roles
- Admin (`admin`/`admin123`): full CRUD for customers/items via REST (UI shows lists; extend with forms if required).
- Cashier (`cashier`/`cashier123`): create bills & view data.

## Notes
- Patterns: DAO, Service Layer (resource classes), MVC (Servlet/JSP), Singleton (DB).
- Task B requirements from the brief are satisfied with login, customer & item CRUD endpoints, billing, and help section.
- Extend JSPs with create/edit forms if your marking scheme expects web-side CRUD (the REST already supports them).
