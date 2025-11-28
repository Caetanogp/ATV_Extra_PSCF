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


javac BuddySimulador.java
Isso vai gerar o arquivo BuddySimulador.class.

2) Execução
Ainda na mesma pasta:
java BuddySimulador

No código, o nome/caminho do arquivo de entrada é controlado pela variável:

String arquivo = "C:\\Users\\Caetanogp123\\OneDrive\\Desktop\\PROJETOS INTELLIJ\\ATV_Extra_PSCF\\ATV_Extra_PSCF\\src\\programas.txt";

Para rodar em outra máquina ou outro diretório, é só alterar essa String para o caminho correto do programas.txt.

Se quiser deixar mais genérico, dá para trocar para:

String arquivo = "programas.txt";
e colocar o programas.txt na mesma pasta de execução do programa.

Formato do arquivo de entrada (programas.txt)
Cada linha do arquivo representa um programa:

<ROTULO> <TAMANHO_EM_KB>
<ROTULO>: um caractere (A, B, C, ...).

<TAMANHO_EM_KB>: um inteiro representando o tamanho em KB (entre 1 KB e 2048 KB).

Exemplo (arquivo com 20 programas):

A 512
B 1024
C 256
D 300
E 1800
F 1
G 2048
H 1000
I 150
J 450
K 700
L 128
M 1300
N 600
O 2047
P 1023
Q 2046
R 512
S 1025
T 999

O programa foi feito para funcionar com qualquer arquivo no mesmo padrão: rótulo + tamanho em KB, um por linha.

Decisões de projeto
Representação da memória
Memória total: 4 * 1024 * 1024 bytes (4 MB).

Menor bloco: 1024 bytes (1 KB).

A memória é modelada como uma árvore binária implícita em vetores:



int[] statusNo;     // 0 = livre, 1 = dividido, 2 = usado
char[] programaNo;  // rótulo do programa alocado naquele nó
int[] tamanhoProgKB;// tamanho real do programa (KB)
Índices dos nós:

Raiz da árvore: índice 0 → bloco de 4 MB.

Filho esquerdo: 2 * i + 1

Filho direito: 2 * i + 2

O número total de nós (totalNos = 8191) corresponde à árvore completa até chegar em blocos de 1 KB.

Estratégia de alocação
Para cada programa:

O tamanho em KB é convertido para bytes.

É calculado o menor bloco de potência de 2 que comporta aquele tamanho (1 KB, 2 KB, 4 KB, ..., até no máximo 4 MB).

A alocação é feita por uma função recursiva que percorre a árvore:

se o nó está livre e o bloco tem tamanho exato do pedido, retorna aquele índice;

se o nó está livre e o bloco é maior que o necessário, o nó é marcado como dividido e o bloco é repartido em dois “buddies”;

a busca sempre tenta primeiro o filho esquerdo e depois o filho direito, funcionando como uma estratégia parecida com first fit.

Se não existirem blocos suficientes para atender o pedido (por exemplo, memória cheia ou muito fragmentada), o programa não é alocado e uma mensagem é exibida no console.

Liberação e fusão de buddies
Há um método liberar(char rotulo) que:

procura na árvore um nó marcado como USADO com aquele rótulo;

marca esse nó como LIVRE;

sobe na árvore tentando juntar o bloco com o seu “buddy”:

se os dois filhos do mesmo pai estão livres e o pai está dividido, os dois são fundidos em um bloco maior e o pai passa a ser livre;

esse processo continua enquanto for possível, restaurando blocos maiores e reduzindo a fragmentação.

Relatório final
Ao final da execução, o programa mostra:

Programas alocados:

- rótulo do programa
- tamanho real em KB
- tamanho do bloco alocado (potência de 2 em KB)
- posição na memória (offset em bytes)

Blocos livres:

- tamanho do bloco em KB
- offset em bytes

Resumo:

- espaço livre total em bytes
- quantidade de blocos livres (fragmentos)

Assim é possível conferir se a soma dos blocos alocados + livres = 4 MB e visualizar a fragmentação da memória.

Vídeo (YouTube)

(colocar aqui o link do YouTube)
