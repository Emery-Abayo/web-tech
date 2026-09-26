# Student Well-being System — REST API (Assignment 4)

Spring Boot rebuild of your JSF/Hibernate project. Same domain, same database
(`wellbeing_db`), same two proven entities (**Student**, **DisciplineRecord**)
with identical fields — plus **WellbeingRecord**, the module your original
README flagged as designed but not implemented, now built out as the third
CRUD entity the assignment requires.

## What changed from the JSF version, and why

| JSF/Hibernate | This version | Why |
|---|---|---|
| `StudentDAO`/`DisciplineRecordDAO` (manual `Session`/`Transaction`) | Spring Data `JpaRepository` | Same Hibernate underneath; Spring Data generates the CRUD boilerplate your DAOs hand-wrote |
| `StudentBean`/`DisciplineRecordBean` (`@ManagedBean @ViewScoped`) | `@RestController` | JSF beans back XHTML pages; a REST API needs controllers returning JSON |
| `FacesMessage` errors, `IncidentDateValidator` | `BusinessValidationException` → `409`, `@PastOrPresent`/`@NotBlank` bean validation → `400` | No Faces context in a REST API; HTTP status codes carry the same meaning |
| `javax.persistence.*` | `jakarta.persistence.*` | Spring Boot 3.x moved to the Jakarta namespace |
| `java.util.Date` | `java.time.LocalDate` | Modern date API; same semantics |

The **unique student number** rule and **incident date can't be future** rule
are carried over exactly. **WellbeingRecord** adds one new rule of its own
(below) since it didn't exist before.

## Run it

```bash
mvn spring-boot:run
```
Uses the same Postgres DB as your `hibernate.cfg.xml` (`wellbeing_db`,
`postgres`/`1234`, port 5432) — create it once with `CREATE DATABASE wellbeing_db;`
if you haven't already. No Postgres handy? Swap in the commented H2 block in
`application.properties` for zero-setup local testing.

Base URL: `http://localhost:8080`

## Endpoints

**Student** — `/api/students`: `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`
```json
{ "studentNumber": "S-1001", "firstName": "Alice", "lastName": "Uwase", "gender": "F", "className": "P6", "email": "alice@example.com" }
```

**DisciplineRecord** — `/api/discipline-records`: `GET`, `GET /{id}`, `GET /student/{studentId}`,
`GET /student/{studentId}/score` (ported from `DisciplineRecordBean.scoreFor()`),
`POST`, `PUT /{id}`, `DELETE /{id}`
```json
{
  "studentId": 1,
  "incidentDate": "2026-09-18",
  "fault": "Late to class",
  "description": "Arrived 20 minutes late without a note",
  "punishment": "Verbal warning",
  "points": 5,
  "status": "Open"
}
```

**WellbeingRecord** — `/api/wellbeing-records`: `GET`, `GET /{id}`, `GET /student/{studentId}`,
`POST`, `PUT /{id}`, `DELETE /{id}`
```json
{
  "studentId": 1,
  "checkDate": "2026-09-18",
  "concernType": "Emotional",
  "notes": "Seemed withdrawn during group work",
  "riskLevel": "Medium"
}
```
`riskLevel` is free text in the entity but the app treats `"High"` specially (see below).

## Business rules enforced

- **Student**: `studentNumber` must be unique (same as `StudentDAO.isStudentNumberDuplicate`);
  a student with existing discipline or wellbeing records can't be deleted.
- **DisciplineRecord**: `incidentDate` can't be in the future (same as
  `IncidentDateValidator`); new records default to `status: "Open"` if omitted
  (same as `DisciplineRecordBean.prepareNew()`).
- **WellbeingRecord** *(new)*: `checkDate` can't be in the future; `riskLevel: "High"`
  always forces `followUpRequired: true` in the response, regardless of what
  was sent — a high-risk concern can't be quietly logged without a flag.

## Postman testing flow

1. Create a student → note the returned `id`.
2. Log a discipline record for that student, then `GET /student/{id}/score` to
   confirm the points total.
3. Log a wellbeing record with `riskLevel: "High"` → confirm the response
   comes back with `followUpRequired: true` even though you didn't send it.
4. Try creating a second student with the same `studentNumber` → expect `409`.
5. Try deleting a student who has records → expect `409`.

Export the Postman collection (or screenshots) into the assignment
documentation alongside the GitHub link and video link.
