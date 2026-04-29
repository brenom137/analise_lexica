package compilador.sintatico;

import compilador.lexico.ClasseToken;
import compilador.lexico.Lexico;
import compilador.lexico.Token;

public class Sintatico {

    private Lexico lexico;
    private Token token;

    public Sintatico(String nomeArquivo) {
        lexico = new Lexico(nomeArquivo);
        token = lexico.getNextToken();
    }

    private void erro(String mensagem) {
        throw new RuntimeException("Erro sintatico [" + token.getLinha() + ":" + token.getColuna() + "]: " + mensagem);
    }

    private String nomeLegivel(ClasseToken classe) {
        switch (classe) {
            case Identificador:
                return "um identificador";
            case Inteiro:
                return "um numero inteiro";
            case PalavraReservada:
                return "uma palavra reservada";
            case Mais:
                return "'+'";
            case Menos:
                return "'-'";
            case Divisao:
                return "'/'";
            case Multiplicacao:
                return "'*'";
            case Igualdade:
                return "'='";
            case Atribuicao:
                return "':='";
            case Maior:
                return "'>'";
            case MaiorIgual:
                return "'>='";
            case Menor:
                return "'<'";
            case MenorIgual:
                return "'<='";
            case Diferente:
                return "'<>'";
            case DoisPontos:
                return ":'";
            case PontoVirgula:
                return "';'";
            case Virgula:
                return "','";
            case AbreParenteses:
                return "'('";
            case FechaParenteses:
                return "')'";
            case Ponto:
                return "'.'";
            case String:
                return "uma string";
            case EOF:
                return "fim do arquivo (EOF)";
            default:
                return classe.toString();
        }
    }

    private String descricaoToken() {
        if (token.getClasse() == ClasseToken.PalavraReservada) {
            return "'" + token.getValor().getTexto() + "'";
        } else if (token.getClasse() == ClasseToken.Identificador) {
            return "identificador '" + token.getValor().getTexto() + "'";
        } else if (token.getClasse() == ClasseToken.Inteiro) {
            return "número " + token.getValor().getInteiro();
        } else if (token.getClasse() == ClasseToken.String) {
            return "string '" + token.getValor().getTexto() + "'";
        } else if (token.getClasse() == ClasseToken.EOF) {
            return "fim do arquivo (EOF)";
        } else {
            return nomeLegivel(token.getClasse());
        }
    }

    private boolean ehPalavraReservada(String palavra) {
        return token.getClasse() == ClasseToken.PalavraReservada
                && token.getValor().getTexto().equals(palavra);
    }

    private void consumir(ClasseToken classeEsperada) {
        if (token.getClasse() == classeEsperada) {
            token = lexico.getNextToken();
        } else {
            erro("esperado " + nomeLegivel(classeEsperada) + ", mas encontrou " + descricaoToken());
        }
    }

    private void consumirPalavraReservada(String palavra) {
        if (ehPalavraReservada(palavra)) {
            token = lexico.getNextToken();
        } else {
            erro("esperado '" + palavra + "', mas encontrou " + descricaoToken());
        }
    }

    public void programa() {
        consumirPalavraReservada("program");
        consumir(ClasseToken.Identificador);
        consumir(ClasseToken.PontoVirgula);
        corpo();
        consumir(ClasseToken.Ponto);
        System.out.println("Analise sintatica concluida com sucesso!");
    }

    private void corpo() {
        declara();
        rotina();
        consumirPalavraReservada("begin");
        sentencas();
        consumirPalavraReservada("end");
    }

    private void declara() {
        if (ehPalavraReservada("var")) {
            consumirPalavraReservada("var");
            dvar();
            mais_dc();
        }
    }

    private void mais_dc() {
        consumir(ClasseToken.PontoVirgula);
        cont_dc();
    }

    private void cont_dc() {
        if (token.getClasse() == ClasseToken.Identificador) {
            dvar();
            mais_dc();
        }
    }

    private void dvar() {
        variaveis();
        consumir(ClasseToken.DoisPontos);
        tipo_var();
    }

    private void tipo_var() {
        consumirPalavraReservada("integer");
    }

    private void variaveis() {
        consumir(ClasseToken.Identificador);
        mais_var();
    }

    private void mais_var() {
        if (token.getClasse() == ClasseToken.Virgula) {
            consumir(ClasseToken.Virgula);
            variaveis();
        }
    }

    private void rotina() {
        if (ehPalavraReservada("procedure")) {
            procedimento();
        } else if (ehPalavraReservada("function")) {
            funcao();
        }
    }

    private void procedimento() {
        consumirPalavraReservada("procedure");
        consumir(ClasseToken.Identificador);
        parametros();
        consumir(ClasseToken.PontoVirgula);
        corpo();
        consumir(ClasseToken.PontoVirgula);
        rotina();
    }

    private void funcao() {
        consumirPalavraReservada("function");
        consumir(ClasseToken.Identificador);
        parametros();
        consumir(ClasseToken.DoisPontos);
        tipo_funcao();
        consumir(ClasseToken.PontoVirgula);
        corpo();
        consumir(ClasseToken.PontoVirgula);
        rotina();
    }

    private void parametros() {
        if (token.getClasse() == ClasseToken.AbreParenteses) {
            consumir(ClasseToken.AbreParenteses);
            lista_parametros();
            consumir(ClasseToken.FechaParenteses);
        }
    }

    private void lista_parametros() {
        lista_id();
        consumir(ClasseToken.DoisPontos);
        tipo_var();
        cont_lista_par();
    }

    private void cont_lista_par() {
        if (token.getClasse() == ClasseToken.PontoVirgula) {
            consumir(ClasseToken.PontoVirgula);
            lista_parametros();
        }
    }

    private void lista_id() {
        consumir(ClasseToken.Identificador);
        cont_lista_id();
    }

    private void cont_lista_id() {
        if (token.getClasse() == ClasseToken.Virgula) {
            consumir(ClasseToken.Virgula);
            lista_id();
        }
    }

    private void tipo_funcao() {
        consumirPalavraReservada("integer");
    }

    private void sentencas() {
        comando();
        mais_sentencas();
    }

    private void mais_sentencas() {
        consumir(ClasseToken.PontoVirgula);
        cont_sentencas();
    }

    private void cont_sentencas() {
        if (ehInicioComando()) {
            sentencas();
        }
    }

    private boolean ehInicioComando() {
        if (token.getClasse() == ClasseToken.Identificador)
            return true;
        if (token.getClasse() == ClasseToken.PalavraReservada) {
            String palavra = token.getValor().getTexto();
            return palavra.equals("read") || palavra.equals("write") || palavra.equals("writeln")
                    || palavra.equals("for") || palavra.equals("repeat") || palavra.equals("while")
                    || palavra.equals("if");
        }
        return false;
    }

    private void comando() {
        if (ehPalavraReservada("read")) {
            consumirPalavraReservada("read");
            consumir(ClasseToken.AbreParenteses);
            var_read();
            consumir(ClasseToken.FechaParenteses);

        } else if (ehPalavraReservada("write")) {
            consumirPalavraReservada("write");
            consumir(ClasseToken.AbreParenteses);
            exp_write();
            consumir(ClasseToken.FechaParenteses);

        } else if (ehPalavraReservada("writeln")) {
            consumirPalavraReservada("writeln");
            consumir(ClasseToken.AbreParenteses);
            exp_write();
            consumir(ClasseToken.FechaParenteses);

        } else if (ehPalavraReservada("for")) {
            consumirPalavraReservada("for");
            consumir(ClasseToken.Identificador);
            consumir(ClasseToken.Atribuicao);
            expressao(false);
            consumirPalavraReservada("to");
            expressao(false);
            consumirPalavraReservada("do");
            consumirPalavraReservada("begin");
            sentencas();
            consumirPalavraReservada("end");

        } else if (ehPalavraReservada("repeat")) {
            consumirPalavraReservada("repeat");
            sentencas();
            consumirPalavraReservada("until");
            consumir(ClasseToken.AbreParenteses);
            expressao_logica();
            consumir(ClasseToken.FechaParenteses);

        } else if (ehPalavraReservada("while")) {
            consumirPalavraReservada("while");
            consumir(ClasseToken.AbreParenteses);
            expressao_logica();
            consumir(ClasseToken.FechaParenteses);
            consumirPalavraReservada("do");
            consumirPalavraReservada("begin");
            sentencas();
            consumirPalavraReservada("end");

        } else if (ehPalavraReservada("if")) {
            consumirPalavraReservada("if");
            consumir(ClasseToken.AbreParenteses);
            expressao_logica();
            consumir(ClasseToken.FechaParenteses);
            consumirPalavraReservada("then");
            consumirPalavraReservada("begin");
            sentencas();
            consumirPalavraReservada("end");
            pfalsa();

        } else if (token.getClasse() == ClasseToken.Identificador) {
            consumir(ClasseToken.Identificador);

            if (token.getClasse() == ClasseToken.Atribuicao) {
                consumir(ClasseToken.Atribuicao);
                expressao();
            } else if (token.getClasse() == ClasseToken.AbreParenteses) {
                argumentos();
            } else {
                erro("esperado ':=' após o identificador, mas encontrou " + descricaoToken());
            }

        } else {
            erro("esperado um comando (read, write, writeln, for, repeat, while, if, atribuição ou chamada de procedimento), mas encontrou "
                    + descricaoToken());
        }
    }

    private void var_read() {
        consumir(ClasseToken.Identificador);
        while (token.getClasse() == ClasseToken.Virgula) {
            consumir(ClasseToken.Virgula);
            consumir(ClasseToken.Identificador);
        }
    }

    private void exp_write() {
        if (token.getClasse() == ClasseToken.Identificador || token.getClasse() == ClasseToken.Inteiro
                || token.getClasse() == ClasseToken.String) {
            if (token.getClasse() == ClasseToken.Identificador)
                consumir(ClasseToken.Identificador);
            else if (token.getClasse() == ClasseToken.Inteiro)
                consumir(ClasseToken.Inteiro);
            else {
                consumir(ClasseToken.String);
            }
        }
    }

    private void argumentos() {
        if (token.getClasse() == ClasseToken.AbreParenteses) {
            consumir(ClasseToken.AbreParenteses);
            if (token.getClasse() != ClasseToken.FechaParenteses) {
                expressao();
                while (token.getClasse() == ClasseToken.Virgula) {
                    consumir(ClasseToken.Virgula);
                    expressao();
                }
            }
            consumir(ClasseToken.FechaParenteses);
        }
    }

    private boolean ehOperadorRelacional() {
        return token.getClasse() == ClasseToken.Igualdade || token.getClasse() == ClasseToken.Maior
                || token.getClasse() == ClasseToken.MaiorIgual || token.getClasse() == ClasseToken.Menor
                || token.getClasse() == ClasseToken.MenorIgual || token.getClasse() == ClasseToken.Diferente;
    }

    private boolean ehInicioExpressaoAritmetica() {
        return token.getClasse() == ClasseToken.Identificador || token.getClasse() == ClasseToken.Inteiro
                || token.getClasse() == ClasseToken.AbreParenteses;
    }

    private boolean ehInicioExpressaoLogica() {
        return ehPalavra("not") || ehPalavra("true") || ehPalavra("false") || ehInicioExpressaoAritmetica();
    }

    private boolean ehPalavra(String palavra) {
        return token.getClasse() == ClasseToken.PalavraReservada && token.getValor().getTexto().equals(palavra);
    }

    private void fator_logico() {
        if (ehPalavra("not")) {
            consumirPalavraReservada("not");
            fator_logico();
        } else if (token.getClasse() == ClasseToken.AbreParenteses) {
            consumir(ClasseToken.AbreParenteses);
            expressao_logica();
            consumir(ClasseToken.FechaParenteses);
        } else if (ehPalavra("true")) {
            consumirPalavraReservada("true");
        } else if (ehPalavra("false")) {
            consumirPalavraReservada("false");
        } else {
            relacional();
        }
    }

    private void relacional() {
        expressao(false);

        if (!ehOperadorRelacional()) {
            erro("esperado um operador relacional (=, >, >=, <, <= ou <>) após a expressão, mas encontrou "
                    + descricaoToken());
        }

        consumir(token.getClasse());
        expressao(false);
    }

    private void termo_logico() {
        fator_logico();
        while (ehPalavra("and")) {
            consumirPalavraReservada("and");
            fator_logico();
        }
    }

    private void expressao_logica() {
        termo_logico();
        while (ehPalavra("or")) {
            consumirPalavraReservada("or");
            termo_logico();
        }

        if (ehInicioExpressaoLogica()) {
            erro("faltou um operador logico (and ou or) entre expressoes, mas encontrou " + descricaoToken());
        }
    }

    private void expressao() {
        expressao(true);
    }

    private void expressao(boolean verificarFaltaOperador) {
        termo();
        while (token.getClasse() == ClasseToken.Mais || token.getClasse() == ClasseToken.Menos) {
            if (token.getClasse() == ClasseToken.Mais)
                consumir(ClasseToken.Mais);
            else
                consumir(ClasseToken.Menos);
            termo();
        }

        if (verificarFaltaOperador && ehInicioExpressaoAritmetica()) {
            erro("faltou um operador aritmetico (+, -, * ou /) entre termos, mas encontrou " + descricaoToken());
        }
    }

    private void termo() {
        fator();
        while (token.getClasse() == ClasseToken.Multiplicacao || token.getClasse() == ClasseToken.Divisao) {
            if (token.getClasse() == ClasseToken.Multiplicacao)
                consumir(ClasseToken.Multiplicacao);
            else
                consumir(ClasseToken.Divisao);
            fator();
        }
    }

    private void fator() {
        if (token.getClasse() == ClasseToken.Identificador) {
            consumir(ClasseToken.Identificador);
        } else if (token.getClasse() == ClasseToken.Inteiro) {
            consumir(ClasseToken.Inteiro);
        } else if (token.getClasse() == ClasseToken.AbreParenteses) {
            consumir(ClasseToken.AbreParenteses);
            expressao();
            consumir(ClasseToken.FechaParenteses);
        } else {
            erro("esperado um fator (identificador, inteiro ou '('), mas encontrou " + descricaoToken());
        }
    }

    private void pfalsa() {
        if (ehPalavraReservada("else")) {
            consumirPalavraReservada("else");
            consumirPalavraReservada("begin");
            sentencas();
            consumirPalavraReservada("end");
        }
    }

}
