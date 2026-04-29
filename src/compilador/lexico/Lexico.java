package compilador.lexico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Lexico {

    private String nomeArquivo;
    private BufferedReader br;
    private char caractere;
        private static final List<String> palavrasReservadas = Arrays.asList(
            "const", "type", "var", "begin", "end", "while", "do", "for", "downto", "if", "then", "else", "case",
            "of", "array", "function", "procedure", "label", "record", "exit", "break", "continue", "and", "or",
            "not", "true", "false", "integer", "program", "write", "writeln", "read", "repeat", "until", "to");
    private int linha;
    private int coluna;
    private static final char EOF_CHAR = (char) 65535;

    public Lexico(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        linha = 1;
        coluna = 1;
        String caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        try {
            br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
            caractere = (char) br.read();
        } catch (IOException ex) {
            System.out.println("Erro abrindo o arquivo " + nomeArquivo);
            System.out.println("Caminho do arquivo: " + caminhoArquivo);
        }
    }

    private void avanca() throws IOException {
        caractere = (char) br.read();
        coluna++;
    }

    private void avancaComNewline() throws IOException {
        if (caractere == '\n') {
            linha++;
            coluna = 1;
            caractere = (char) br.read();
        } else {
            avanca();
        }
    }

    public Token getNextToken() {
        StringBuilder lexema;
        Token token;

        try {
            while (caractere != EOF_CHAR) {
                lexema = new StringBuilder();
                token = new Token(linha, coluna);

                if (Character.isDigit(caractere)) {
                    while (Character.isDigit(caractere)) {
                        lexema.append(caractere);
                        avanca();
                    }
                    token.setClasse(ClasseToken.Inteiro);
                    token.setValor(new ValorToken(Integer.parseInt(lexema.toString())));
                    return token;
                } else if (Character.isAlphabetic(caractere)) {
                    while (Character.isAlphabetic(caractere) || Character.isDigit(caractere)) {
                        lexema.append(caractere);
                        avanca();
                    }
                    if (palavrasReservadas.contains(lexema.toString().toLowerCase())) {
                        token.setClasse(ClasseToken.PalavraReservada);
                    } else {
                        token.setClasse(ClasseToken.Identificador);
                    }
                    token.setValor(new ValorToken(lexema.toString().toLowerCase()));
                    return token;
                } else if (caractere == ' ' || caractere == '\t' || caractere == '\r') {
                    avanca();
                } else if (caractere == '\n') {
                    linha++;
                    coluna = 1;
                    caractere = (char) br.read();
                } else if (caractere == '+') {
                    avanca();
                    token.setClasse(ClasseToken.Mais);
                    return token;
                } else if (caractere == '-') {
                    avanca();
                    token.setClasse(ClasseToken.Menos);
                    return token;
                } else if (caractere == '/') {
                    avanca();
                    if (caractere == '/') {
                        while (caractere != '\n' && caractere != EOF_CHAR) {
                            avanca();
                        }
                    } else {
                        token.setClasse(ClasseToken.Divisao);
                        return token;
                    }
                } else if (caractere == '=') {
                    avanca();
                    token.setClasse(ClasseToken.Igualdade);
                    return token;
                } else if (caractere == ':') {
                    avanca();
                    if (caractere == '=') {
                        avanca();
                        token.setClasse(ClasseToken.Atribuicao);
                    } else {
                        token.setClasse(ClasseToken.DoisPontos);
                    }
                    return token;
                } else if (caractere == '>') {
                    avanca();
                    if (caractere == '=') {
                        avanca();
                        token.setClasse(ClasseToken.MaiorIgual);
                    } else {
                        token.setClasse(ClasseToken.Maior);
                    }
                    return token;
                } else if (caractere == '<') {
                    avanca();
                    if (caractere == '=') {
                        avanca();
                        token.setClasse(ClasseToken.MenorIgual);
                    } else if (caractere == '>') {
                        avanca();
                        token.setClasse(ClasseToken.Diferente);
                    } else {
                        token.setClasse(ClasseToken.Menor);
                    }
                    return token;
                } else if (caractere == ';') {
                    avanca();
                    token.setClasse(ClasseToken.PontoVirgula);
                    return token;
                } else if (caractere == ',') {
                    avanca();
                    token.setClasse(ClasseToken.Virgula);
                    return token;
                } else if (caractere == '(') {
                    avanca();
                    if (caractere == '*') {
                        avanca();
                        while (true) {
                            if (caractere == EOF_CHAR) {
                                System.out.println("Erro lexico faltou fechar comentario (*\nLinha: " + linha
                                        + "\nColuna: " + coluna);
                                System.exit(1);
                            }
                            if (caractere == '*') {
                                avanca();
                                if (caractere == ')') {
                                    avanca();
                                    break;
                                }
                            } else {
                                avancaComNewline();
                            }
                        }
                    } else {
                        token.setClasse(ClasseToken.AbreParenteses);
                        return token;
                    }
                } else if (caractere == ')') {
                    avanca();
                    token.setClasse(ClasseToken.FechaParenteses);
                    return token;
                } else if (caractere == '.') {
                    avanca();
                    token.setClasse(ClasseToken.Ponto);
                    return token;
                } else if (caractere == '*') {
                    avanca();
                    token.setClasse(ClasseToken.Multiplicacao);
                    return token;
                } else if (caractere == '{') {
                    avanca();
                    while (caractere != '}') {
                        if (caractere == EOF_CHAR) {
                            System.out.println(
                                    "Erro lexico faltou fechar comentario }\nLinha: " + linha + "\nColuna: " + coluna);
                            System.exit(1);
                        }
                        avancaComNewline();
                    }
                    avanca();
                } else if (caractere == '\'') {
                    avanca();
                    while (caractere != '\'') {
                        lexema.append(caractere);
                        avanca();

                        if (caractere == 65535) {
                            System.out.println("Faltou fechar a string '\nLinha: " + linha + "\nColuna: " + coluna);
                            System.exit(1);
                        } else if (caractere == '\r') {
                            caractere = (char) br.read();
                            if (caractere == '\n') {
                                System.out.println("Faltou fechar a string '\nLinha: " + linha + "\nColuna: " + coluna);
                                System.exit(1);
                            }
                        } else if (caractere == '\n') {
                            System.out.println("Faltou fechar a string '\nLinha: " + linha + "\nColuna: " + coluna);
                            System.exit(1);
                        } else if (caractere == '\'') {
                            avanca();
                            token.setClasse(ClasseToken.String);
                            token.setValor(new ValorToken(lexema.toString()));
                            return token;
                        }
                    }
                } else {
                    avanca();
                }
            }
            token = new Token(linha, coluna);
            token.setClasse(ClasseToken.EOF);
            return token;
        } catch (IOException e) {
            System.err.println("Nao foi possivel ler do arquivo: " + nomeArquivo);
        }
        return null;
    }
}
