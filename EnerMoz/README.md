# EnerMoz

EnerMoz e uma plataforma academica em Java para monitoria e analise de consumo de energia electrica, pensada para a realidade mocambicana. O sistema permite cadastrar qualquer aparelho electrico manualmente, calcular consumo, estimar factura, emitir alertas inteligentes, sugerir poupanca, gerar relatorios, ordenar ranking de consumo e simular reducoes na factura.

## Tecnologias

- Java 17
- Programacao Orientada a Objectos
- ArrayList
- Packages Java
- Persistencia em ficheiros TXT
- HTML, CSS e JavaScript puro
- VS Code

## Funcionalidades

- Dashboard com utilizador, quantidade de aparelhos, consumo total, factura estimada e score energetico.
- Cadastro, edicao, remocao e listagem de aparelhos.
- Potencia estimada automaticamente quando o utilizador informa `0`.
- Relatorio energetico com consumo, factura, classificacao e ranking.
- Alertas para consumo elevado e aparelhos com grande gasto energetico.
- Sugestoes como reduzir horas de utilizacao, substituir equipamento ineficiente e desligar standby.
- Ranking energetico ordenado pelo consumo mensal.
- Simulador de poupanca mensal e anual.
- Historico com data, consumo e factura.
- Interface web simples em `web/`, sem frameworks.

## Arquitectura

```text
src/
  main/
  controller/
  model/
    dispositivos/
    energia/
    utilizador/
  services/
  repositories/
  view/
  utils/
  exceptions/
  data/
web/
```

A arquitectura separa responsabilidades:

- `model`: entidades e regras basicas do dominio.
- `services`: calculos, relatorios, alertas, sugestoes, score e simulacoes.
- `repositories`: leitura e escrita dos ficheiros TXT.
- `controller`: orquestracao entre view, services, repositories e model.
- `view`: menu de consola.
- `web`: prototipo visual em HTML/CSS/JS puro.

## Modelo energetico

O projecto evita classes especificas como `Televisao`, `Geleira` ou `Ventoinha`. Em vez disso, usa:

- `Dispositivo`: representa qualquer aparelho.
- `CategoriaEnergetica`: enum com categorias e potencia media.
- `PotenciaEstimator`: estima potencia por nome ou categoria quando necessario.

Campos obrigatorios:

- nome
- categoria
- horas de uso

Campos opcionais:

- potencia
- fabricante
- modelo
- localizacao
- observacoes
- eficiencia energetica



## Como executar no VS Code

1. Abra a pasta do projecto no VS Code.
2. Confirme que o Java 17 esta instalado:

```bash
java -version
```

3. Compile o projecto:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

No PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

4. Execute:

```bash
java -cp out main.Main
```

## Interface web

Abra o ficheiro `web/index.html` no navegador. A interface web usa `localStorage` para guardar os aparelhos no browser e serve como prototipo para futura integracao com a aplicacao Java.

## Persistencia

Os dados da aplicacao Java sao guardados em:

- `src/data/dispositivos.txt`
- `src/data/historico.txt`

A aplicacao carrega os dados automaticamente ao iniciar e guarda apos adicionar, editar ou remover aparelhos.

## UML

O ficheiro `UML.txt` contem o UML textual completo do projecto, incluindo packages, classes, interfaces, metodos principais, relacoes e conceitos de POO.

## Roadmap futuro

- Migrar a view de consola para Swing.
- Criar interface JavaFX com graficos.
- Expor os services via Spring Boot.
- Migrar persistencia TXT para MySQL ou PostgreSQL.
- Integrar a interface web com uma API Java.
- Criar aplicacao mobile para registo de consumo domestico.
- Adicionar perfis de tarifa EDM configuraveis.
- Exportar relatorios em PDF ou CSV.
