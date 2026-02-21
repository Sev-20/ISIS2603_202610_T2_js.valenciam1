# Back del proyecto TallerPruebas

## Enlaces de interés

* [BookstoreBack](https://github.com/Uniandes-isis2603/bookstore-back) -> Repositorio de referencia para el Back

* [Jenkins](http://157.253.238.75:8080/jenkins-isis2603/) -> Autentíquese con sus credencias de GitHub
* [SonarQube](http://157.253.238.75:8080/sonar-isis2603/) -> No requiere autenticación

Escenarios Regla 1

1. Éxito: Mover dinero desde una cuenta a un bolsillo con saldo suficiente

2. Fallo: Intentar mover dinero desde una cuenta que no existe

3. Fallo: Intentar mover dinero a un bolsillo que no existe (o no pertenece a la cuenta)

4. Fallo: Intentar mover dinero con saldo insuficiente en la cuenta

5. Fallo: Intentar mover dinero con monto inválido (cero o negativo)



Escenarios Regla 2

1. Éxito: Transferir dinero entre dos cuentas existentes distintas con saldo suficiente

2. Fallo: Intentar transferir desde una cuenta origen que no existe

3. Fallo: Intentar transferir hacia una cuenta destino que no existe

4. Fallo: Intentar transferir entre la misma cuenta (origen = destino)

5. Fallo: Intentar transferir con saldo insuficiente en la cuenta origen



