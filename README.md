# Alocador de Memória Buddy Binário em Java

Trabalho da disciplina de Sistemas Operacionais / PSCF: implementação de um alocador de memória do tipo **buddy binário** para uma memória principal de **4 MB (4 * 1024 * 1024 bytes)**.

O programa lê de um arquivo TXT uma lista de programas (rótulo + tamanho em KB), faz a alocação dinâmica seguindo o esquema buddy (blocos de tamanho potência de 2) e, ao final, mostra um relatório da memória.

---

## Arquivos do projeto

- `BuddySimulador.java`  
  Código do simulador com:
  - leitura do arquivo de entrada,
  - alocador buddy,
  - impressão do relatório final.

- `programas.txt`  
  Arquivo de exemplo com 20 programas (rótulo + tamanho em KB).

---

## Como compilar e executar

### 1) Compilação

No terminal, dentro da pasta onde está o arquivo `BuddySimulador.java`:

```bash
javac BuddySimulador.java
