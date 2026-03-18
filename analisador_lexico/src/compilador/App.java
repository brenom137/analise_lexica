package compilador;

import compilador.lexico.ClasseToken;
import compilador.lexico.Lexico;
import compilador.lexico.Token;

public class App {
    public static void main(String[] args) {
        Lexico lexico = new Lexico("arquivo_pascal_teste.pas");
        Token token = lexico.getNextToken();

        while (token.getClasse() != ClasseToken.EOF) {
            System.out.println(token);
            token = lexico.getNextToken();
        }
        System.out.println(token);
    }
}
