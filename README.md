# bd_basic (updated)

Proyecto Java Maven que demuestra operaciones simples sobre la tabla `actor` de la base de datos `sakila`.

Principales mejoras aplicadas:
- Actualizado a Java 21
- HikariCP como pool de conexiones
- SLF4J + Logback para logging
- Lectura de credenciales desde variables de entorno (DB_URL, DB_USER, DB_PASS) o properties

Ejecución local (PowerShell):

```powershell
# export environment variables (Windows PowerShell)
$env:DB_URL = "jdbc:mysql://localhost:3306/sakila?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER = "root"
$env:DB_PASS = "yourPasswordHere"

mvn clean compile
mvn exec:java -Dexec.mainClass="com.ucc.Main"
```

Notas de seguridad:
- No incluyas credenciales en el repositorio. Usa variables de entorno o un vault.

Uso de `.env` durante desarrollo

Hay un archivo de ejemplo `.env.example` en la raíz del proyecto. Copia ese archivo a `.env` y rellena las credenciales locales. `.env` está en `.gitignore` y no se subirá al repositorio.

PowerShell: para cargar las variables del `.env` en la sesión actual puedes ejecutar:

```powershell
Get-Content .env | ForEach-Object {
	if ($_ -and -not ($_ -match '^\s*#')) {
		$parts = $_ -split '=', 2
		if ($parts.Length -eq 2) { $name = $parts[0].Trim(); $value = $parts[1].Trim(); $env:$name = $value }
	}
}

mvn clean compile
mvn exec:java -Dexec.mainClass="com.ucc.Main"
```

También incluí una pequeña utilidad `EnvLoader` que, en tiempo de ejecución, intentará cargar `.env` y establecer propiedades de sistema (`db.url`, `db.user`, `db.pass`) como fallback si no existen variables de entorno reales. Esto facilita el desarrollo local; en producción sigue siendo obligatorio usar variables de entorno o un vault.
