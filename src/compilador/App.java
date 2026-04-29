package compilador;

import compilador.sintatico.Sintatico;

public class App {
    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : "arquivo_pascal_teste.pas";
        try {
            Sintatico sintatico = new Sintatico(arquivo);
            sintatico.programa();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        // Lexico lexico = new Lexico("arquivo_pascal_teste.pas");
        // Token token = lexico.getNextToken();
        //
        // while (token.getClasse() != ClasseToken.EOF) {
        // System.out.println(token);
        // token = lexico.getNextToken();
        // }
        // System.out.println(token);
    }
}
