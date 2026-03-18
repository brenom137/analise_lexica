package compilador.lexico;

public class ValorToken {

    private int numeroInteiro;
    private String textoLiteral;

    public ValorToken(int inteiro) {
        this.numeroInteiro = inteiro;
    }

    public ValorToken(String texto) {
        this.textoLiteral = texto;
    }

    public int getNumeroInteiro() {
        return numeroInteiro;
    }

    public void setNumeroInteiro(int numeroInteiro) {
        this.numeroInteiro = numeroInteiro;
    }

    public String getTextoLiteral() {
        return textoLiteral;
    }
    
    public void setTextoLiteral(String textoLiteral) {
        this.textoLiteral = textoLiteral;
    }

    public int getInteiro() {
        return getNumeroInteiro();
    }

    public void setInteiro(int inteiro) {
        setNumeroInteiro(inteiro);
    }

    public String getTexto() {
        return getTextoLiteral();
    }
    
    public void setTexto(String texto) {
        setTextoLiteral(texto);
    }

    @Override
    public String toString() {
        return "ValorToken [" + ((textoLiteral != null) ? "texto=" + textoLiteral : "inteiro=" + numeroInteiro) + "]";
    }
    
}
