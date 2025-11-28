import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BuddySimulador {

    static class ListaProgramas {
        char[] rotulos;
        int[] tamanhosKB;
        int qtd;
        int limite;

        ListaProgramas(int max) {
            limite = max;
            rotulos = new char[limite];
            tamanhosKB = new int[limite];
            qtd = 0;
        }

        void adicionar(char r, int kb) {
            if (qtd < limite) {
                rotulos[qtd] = r;
                tamanhosKB[qtd] = kb;
                qtd = qtd + 1;
            }
        }
    }

    static class AlocadorBuddy {

        int totalMemBytes;
        int menorBlocoBytes;
        int totalNos;


        int[] statusNo;
        char[] programaNo;
        int[] tamanhoProgKB;

        final int LIVRE = 0;
        final int DIVIDIDO = 1;
        final int USADO = 2;

        AlocadorBuddy() {
            totalMemBytes = 4 * 1024 * 1024;
            menorBlocoBytes = 1024;
            totalNos = 8191;

            statusNo = new int[totalNos];
            programaNo = new char[totalNos];
            tamanhoProgKB = new int[totalNos];

            int i = 0;
            while (i < totalNos) {
                statusNo[i] = LIVRE;
                programaNo[i] = '\0';
                tamanhoProgKB[i] = 0;
                i = i + 1;
            }
        }

        int calculaBloco(int tamanhoBytes) {
            int b = menorBlocoBytes;

            while (b < tamanhoBytes && b < totalMemBytes) {
                b = b * 2;
            }

            if (b < tamanhoBytes) {
                return -1;
            }
            return b;
        }

        boolean alocar(char rotulo, int tamanhoKB) {
            int tamanhoBytes = tamanhoKB * 1024;
            int blocoNecessario = calculaBloco(tamanhoBytes);

            if (blocoNecessario == -1) {
                return false;
            }

            int indice = alocarRec(0, totalMemBytes, 0, blocoNecessario);

            if (indice == -1) {
                return false;
            }

            statusNo[indice] = USADO;
            programaNo[indice] = rotulo;
            tamanhoProgKB[indice] = tamanhoKB;

            return true;
        }

        int alocarRec(int indice, int tamanhoBloco, int offset, int pedido) {
            if (indice >= totalNos) {
                return -1;
            }

            int st = statusNo[indice];

            if (st == USADO) {
                return -1;
            }

            if (tamanhoBloco < pedido) {
                return -1;
            }

            if (st == LIVRE && tamanhoBloco == pedido) {
                return indice;
            }

            if (st == LIVRE && tamanhoBloco > pedido) {
                statusNo[indice] = DIVIDIDO;
                int filhoEsq = indice * 2 + 1;
                int filhoDir = indice * 2 + 2;

                if (filhoEsq < totalNos) {
                    statusNo[filhoEsq] = LIVRE;
                }
                if (filhoDir < totalNos) {
                    statusNo[filhoDir] = LIVRE;
                }
            }

            if (statusNo[indice] == DIVIDIDO) {
                int metade = tamanhoBloco / 2;
                int filhoEsq = indice * 2 + 1;
                int filhoDir = indice * 2 + 2;

                int r = alocarRec(filhoEsq, metade, offset, pedido);
                if (r != -1) {
                    return r;
                }

                r = alocarRec(filhoDir, metade, offset + metade, pedido);
                return r;
            }

            return -1;
        }

        boolean liberar(char rotulo) {
            int i = 0;
            int achou = -1;

            while (i < totalNos) {
                if (statusNo[i] == USADO && programaNo[i] == rotulo) {
                    achou = i;
                    break;
                }
                i = i + 1;
            }

            if (achou == -1) {
                return false;
            }

            statusNo[achou] = LIVRE;
            programaNo[achou] = '\0';
            tamanhoProgKB[achou] = 0;

            int atual = achou;

            while (atual != 0) {
                int pai = (atual - 1) / 2;
                int filhoEsq = pai * 2 + 1;
                int filhoDir = pai * 2 + 2;

                if (filhoEsq >= totalNos || filhoDir >= totalNos) {
                    break;
                }

                if (statusNo[filhoEsq] == LIVRE &&
                        statusNo[filhoDir] == LIVRE &&
                        statusNo[pai] == DIVIDIDO) {

                    statusNo[pai] = LIVRE;
                    statusNo[filhoEsq] = LIVRE;
                    statusNo[filhoDir] = LIVRE;

                    atual = pai;
                } else {
                    break;
                }
            }

            return true;
        }

        void relatorioFinal() {
            System.out.println();
            System.out.println("PROGRAMAS ALOCADOS:");
            imprimirAlocados(0, totalMemBytes, 0);

            int[] livreTotal = new int[1];
            int[] qtdLivres = new int[1];
            livreTotal[0] = 0;
            qtdLivres[0] = 0;

            System.out.println();
            System.out.println("BLOCOS LIVRES:");
            imprimirLivres(0, totalMemBytes, 0, livreTotal, qtdLivres);

            System.out.println();
            System.out.println("Espaco livre total (bytes): " + livreTotal[0]);
            System.out.println("Quantidade de blocos livres: " + qtdLivres[0]);
        }

        void imprimirAlocados(int indice, int tamanhoBloco, int offset) {
            if (indice >= totalNos) {
                return;
            }

            int st = statusNo[indice];

            if (st == USADO) {
                char r = programaNo[indice];
                int progKB = tamanhoProgKB[indice];
                int blocoKB = tamanhoBloco / 1024;

                System.out.println("Programa " + r +
                        " | tamanho: " + progKB + " KB" +
                        " | bloco: " + blocoKB + " KB" +
                        " | offset: " + offset + " bytes");
            } else if (st == DIVIDIDO) {
                int metade = tamanhoBloco / 2;
                int filhoEsq = indice * 2 + 1;
                int filhoDir = indice * 2 + 2;

                imprimirAlocados(filhoEsq, metade, offset);
                imprimirAlocados(filhoDir, metade, offset + metade);
            }
        }

        void imprimirLivres(int indice, int tamanhoBloco, int offset, int[] livreTotal, int[] qtdLivres) {
            if (indice >= totalNos) {
                return;
            }

            int st = statusNo[indice];

            if (st == LIVRE) {
                int blocoKB = tamanhoBloco / 1024;
                System.out.println("Livre | bloco: " + blocoKB + " KB" +
                        " | offset: " + offset + " bytes");
                livreTotal[0] = livreTotal[0] + tamanhoBloco;
                qtdLivres[0] = qtdLivres[0] + 1;
            } else if (st == DIVIDIDO) {
                int metade = tamanhoBloco / 2;
                int filhoEsq = indice * 2 + 1;
                int filhoDir = indice * 2 + 2;

                imprimirLivres(filhoEsq, metade, offset, livreTotal, qtdLivres);
                imprimirLivres(filhoDir, metade, offset + metade, livreTotal, qtdLivres);
            }
        }
    }

    static ListaProgramas lerProgramas(String nomeArquivo) {
        ListaProgramas lista = new ListaProgramas(100);
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(nomeArquivo));
            String linha;

            while (true) {
                linha = br.readLine();
                if (linha == null) {
                    break;
                }

                linha = linha.trim();
                if (linha.equals("")) {
                    continue;
                }

                String[] partes = linha.split(" ");
                if (partes.length >= 2) {
                    char r = partes[0].charAt(0);
                    int kb = Integer.parseInt(partes[1]);
                    lista.adicionar(r, kb);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e2) {

                }
            }
        }

        return lista;
    }

    public static void main(String[] args) {
        String arquivo = "C:\\\\Users\\\\Caetanogp123\\\\OneDrive\\\\Desktop\\\\PROJETOS INTELLIJ\\\\ATV_Extra_PSCF\\\\ATV_Extra_PSCF\\\\src\\\\programas.txt";

        ListaProgramas lista = lerProgramas(arquivo);
        AlocadorBuddy aloc = new AlocadorBuddy();

        int i = 0;
        while (i < lista.qtd) {
            char r = lista.rotulos[i];
            int kb = lista.tamanhosKB[i];

            boolean ok = aloc.alocar(r, kb);
            if (!ok) {
                System.out.println("Nao foi possivel alocar o programa " + r +
                        " (" + kb + " KB)");
            }
            i = i + 1;
        }


        aloc.relatorioFinal();
    }
}
