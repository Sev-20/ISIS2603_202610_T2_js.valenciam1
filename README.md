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



## Regla 1 — Mover dinero de la Cuenta a un Bolsillo

| ID | Escenario | Acción | Resultado esperado |
|---|---|---|---|
| R1-01 | Éxito: mover dinero a bolsillo | `moveMoneyToPocket(123, 10, 300)` | Cuenta baja saldo y bolsillo sube saldo |
| R1-02 | Falla: cuenta inexistente | `moveMoneyToPocket(999, 10, 100)` | `EntityNotFoundException` |
| R1-03 | Falla: bolsillo inexistente / no pertenece | `moveMoneyToPocket(123, 999, 100)` | `EntityNotFoundException` |
| R1-04 | Falla: saldo insuficiente | `moveMoneyToPocket(123, 10, 100)` | `BusinessLogicException` |
| R1-05 | Falla: monto inválido (`<= 0`) | `moveMoneyToPocket(123, 10, 0)` | `BusinessLogicException` |


### R1-01 — Éxito: mover dinero a bolsillo
- **Given**
  - Existe la cuenta `#123` en estado `ACTIVA` con saldo `1000`
  - Existe el bolsillo `#10` asociado a la cuenta `#123` con saldo `200`
- **When**
  - Se ejecuta `moveMoneyToPocket(123, 10, 300)`
- **Then**
  - La cuenta `#123` queda con saldo `700`
  - El bolsillo `#10` queda con saldo `500`
  - Se persisten ambos cambios

### R1-02 — Falla: cuenta inexistente
- **Given**
  - No existe la cuenta `#999`
- **When**
  - Se ejecuta `moveMoneyToPocket(999, 10, 100)`
- **Then**
  - Se lanza `EntityNotFoundException`
  - No se guarda nada en BD

### R1-03 — Falla: bolsillo inexistente / no pertenece
- **Given**
  - Existe la cuenta `#123` con saldo `1000`
  - El bolsillo `#999` no existe (o pertenece a otra cuenta)
- **When**
  - Se ejecuta `moveMoneyToPocket(123, 999, 100)`
- **Then**
  - Se lanza `EntityNotFoundException`
  - No cambia ningún saldo

### R1-04 — Falla: saldo insuficiente
- **Given**
  - Existe la cuenta `#123` con saldo `50`
  - Existe el bolsillo `#10` asociado a la cuenta `#123` con saldo `200`
- **When**
  - Se ejecuta `moveMoneyToPocket(123, 10, 100)`
- **Then**
  - Se lanza `BusinessLogicException` (`Saldo insuficiente`)
  - No se guarda nada (cuenta y bolsillo conservan sus saldos)

### R1-05 — Falla: monto inválido (`<= 0`)
- **Given**
  - Existe la cuenta `#123` con saldo `1000`
  - Existe el bolsillo `#10` asociado con saldo `200`
- **When**
  - Se ejecuta `moveMoneyToPocket(123, 10, 0)` o `moveMoneyToPocket(123, 10, -50)`
- **Then**
  - Se lanza `BusinessLogicException` (`El monto debe ser mayor que cero`)
  - No cambia ningún saldo




## Regla 2 — Mover dinero entre dos cuentas

| ID | Escenario | Acción | Resultado esperado |
|---|---|---|---|
| R2-01 | Éxito: transferencia entre cuentas | `transferBetweenAccounts(101, 202, 300)` | Origen baja saldo y destino sube saldo |
| R2-02 | Falla: cuenta origen inexistente | `transferBetweenAccounts(999, 202, 100)` | `EntityNotFoundException` |
| R2-03 | Falla: cuenta destino inexistente | `transferBetweenAccounts(101, 999, 100)` | `EntityNotFoundException` |
| R2-04 | Falla: origen = destino | `transferBetweenAccounts(101, 101, 100)` | `BusinessLogicException` |
| R2-05 | Falla: saldo insuficiente en origen | `transferBetweenAccounts(101, 202, 100)` | `BusinessLogicException` |


### R2-01 — Éxito: transferencia entre cuentas
- **Given**
  - Existe cuenta origen `#101` con saldo `1000`
  - Existe cuenta destino `#202` con saldo `400`
  - Son cuentas diferentes
- **When**
  - Se ejecuta `transferBetweenAccounts(101, 202, 300)`
- **Then**
  - La cuenta origen `#101` queda con saldo `700`
  - La cuenta destino `#202` queda con saldo `700`
  - Se persisten ambos cambios de forma atómica

### R2-02 — Falla: cuenta origen inexistente
- **Given**
  - No existe la cuenta origen `#999`
  - Existe la cuenta destino `#202`
- **When**
  - Se ejecuta `transferBetweenAccounts(999, 202, 100)`
- **Then**
  - Se lanza `EntityNotFoundException`
  - No cambia el saldo de la cuenta destino

### R2-03 — Falla: cuenta destino inexistente
- **Given**
  - Existe la cuenta origen `#101` con saldo `1000`
  - No existe la cuenta destino `#999`
- **When**
  - Se ejecuta `transferBetweenAccounts(101, 999, 100)`
- **Then**
  - Se lanza `EntityNotFoundException`
  - No cambia el saldo de la cuenta origen

### R2-04 — Falla: origen = destino
- **Given**
  - Existe la cuenta `#101` con saldo `1000`
- **When**
  - Se ejecuta `transferBetweenAccounts(101, 101, 100)`
- **Then**
  - Se lanza `BusinessLogicException`
  - No cambia el saldo de la cuenta

### R2-05 — Falla: saldo insuficiente en origen
- **Given**
  - Existe la cuenta origen `#101` con saldo `80`
  - Existe la cuenta destino `#202` con saldo `400`
- **When**
  - Se ejecuta `transferBetweenAccounts(101, 202, 100)`
- **Then**
  - Se lanza `BusinessLogicException` (`Saldo insuficiente`)
  - No se guarda nada (atomicidad)
