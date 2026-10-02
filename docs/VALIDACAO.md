# Registro de validação

Validação executada antes do empacotamento:

- `assembleDebug`: aprovado; APK de desenvolvimento gerado.
- `testDebugUnitTest`: 10 testes aprovados, sem falhas.
- `lintDebug`: aprovado, sem erros bloqueadores.
- `assembleDebugAndroidTest`: aprovado; APK de testes instrumentados gerado.

Os testes instrumentados foram compilados, mas a execução completa no emulador foi interrompida a pedido do usuário. Para executá-los em um aparelho ou emulador conectado:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```
