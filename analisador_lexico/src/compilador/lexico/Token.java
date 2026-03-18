package compilador.lexico;

public class Token {

    private ClasseToken tipoToken;
    private ValorToken detalhe;
    private int numeroLinha;
    private int numeroColuna;
    
    public Token(int linha, int coluna) {
        this.numeroLinha = linha;
        this.numeroColuna = coluna;
    }

    public ClasseToken getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(ClasseToken tipoToken) {
        this.tipoToken = tipoToken;
    }

    public ValorToken getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(ValorToken detalhe) {
        this.detalhe = detalhe;
    }

    public int getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(int numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public int getNumeroColuna() {
        return numeroColuna;
    }

    public void setNumeroColuna(int numeroColuna) {
        this.numeroColuna = numeroColuna;
    }

    public ClasseToken getClasse() {
        return getTipoToken();
    }

    public void setClasse(ClasseToken classe) {
        setTipoToken(classe);
    }

    public ValorToken getValor() {
        return getDetalhe();
    }

    public void setValor(ValorToken valor) {
        setDetalhe(valor);
    }

    public int getLinha() {
        return getNumeroLinha();
    }

    public void setLinha(int linha) {
        setNumeroLinha(linha);
    }

    public int getColuna() {
        return getNumeroColuna();
    }

    public void setColuna(int coluna) {
        setNumeroColuna(coluna);
    }

    @Override
    public String toString() {
        return "Token [linha=" + numeroLinha + ", coluna=" + numeroColuna + ", classe=" + tipoToken + ((detalhe != null) ? ", valor=" + detalhe : "") + "]";
    }

    
}
