# OpenAPI Opcional

## Para que sirve

Este bloque permite que la libreria aporte una configuracion base de OpenAPI cuando el proyecto consumidor quiera usarla.

No intenta imponer Swagger ni OpenAPI a todos los proyectos. Por eso:
- esta desactivado por defecto
- la dependencia va en `provided`
- solo se activa por propiedad

## Activacion

```yml
backend-toolkit:
  openapi:
    enabled: true
    title: "EcoRastro User Roles API"
    description: "API de gestion de usuarios, roles y grupos"
    version: "v1"
```

## Que crea

Cuando esta activo, registra un bean `OpenAPI` base con:
- `title`
- `description`
- `version`

## Ejemplo de uso

```java
@SpringBootApplication
public class UserRolesApplication {
}
```

Con la propiedad activada, el proyecto ya dispone de una configuracion OpenAPI minima comun.

## Que pasa si el consumidor no usa OpenAPI

No pasa nada.

Si el proyecto no activa `backend-toolkit.openapi.enabled`, este bloque no se levanta.

## Que pasa si el consumidor ya usa su propia configuracion

La idea es que este bloque sirva como base simple. Si el consumidor ya tiene una configuracion propia mas rica, puede:
- no activar esta funcionalidad
- o sustituirla con su propia definicion

## Resumen

Este bloque existe para ahorrar configuracion repetida de OpenAPI, pero sin imponerla a quien no la necesite.
