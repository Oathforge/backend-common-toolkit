# Cifrado y Conversiones

## Objetivo

Este bloque ofrece soporte para cifrar y descifrar valores de forma centralizada, especialmente en escenarios donde ciertos atributos deben almacenarse protegidos en base de datos.

## Componentes

- `EncryptionUtil`

## Responsabilidad

- Derivar claves de cifrado.
- Cifrar y descifrar cadenas.

## Caso de uso tipico

Persistir campos sensibles como secretos, tokens o datos que no conviene almacenar en claro.

## Ejemplo directo con utilidades

```java
String encrypted = EncryptionUtil.encrypt("my-secret-token", "base-key");
String plain = EncryptionUtil.decrypt(encrypted, "base-key");
```

## Ejemplo de configuracion

```yml
backend-toolkit:
  security:
    encryption:
      key: ${APP_ENCRYPTION_KEY}
```

## Consideraciones

- La clave de cifrado debe inyectarse desde configuracion segura.
- La libreria aporta el mecanismo, pero no sustituye una politica de gestion de secretos.
- Si usas JPA y quieres cifrado transparente con `AttributeConverter`, utiliza el módulo `backend-common-toolkit-jpa`.
