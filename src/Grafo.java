
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author straby
 */
public class Grafo {

    private List<Vertice> vertices;
    private List<Aresta> arestas;
    private boolean orientado;
    private String id;

    public Grafo() {
        this.vertices = new ArrayList<Vertice>();
        this.arestas = new ArrayList<Aresta>();
        this.orientado = false;

    }

    public Grafo(boolean orientado) {
        this.vertices = new ArrayList<Vertice>();
        this.arestas = new ArrayList<Aresta>();
        this.orientado = orientado;
    }

    public boolean isOrientado() {
        return orientado;
    }

    public void setOrientado(boolean orientado) {
        this.orientado = orientado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertice> vertices) {
        this.vertices = vertices;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }

    public void setArestas(List<Aresta> arestas) {
        this.arestas = arestas;
    }

    /* METODOS DE MANIPULACAO DO GRAFO */
    public Vertice addVertice(String id) {
        Vertice v = new Vertice(id);
        this.vertices.add(v);
        return v;
    }

    public void removerVertice(Vertice v) {
        for (int i = 0; i < this.getArestas().size(); i++) {

            if (v.getId().equals(this.getArestas().get(i).getDestino().getId())
                    || v.getId().equals(this.getArestas().get(i).getOrigem().getId())) {

                this.removerAresta(this.getArestas().get(i));
                i--;
            }
        }
        this.vertices.remove(v);
    }

    public Aresta addAresta(Vertice origem, Vertice destino) {
        if (this.isOrientado()) {
            Aresta e = new Aresta(origem, destino);
            origem.addAdj(e);
            destino.addAdj(e);
            this.arestas.add(e);
            return e;
        } else {
            Aresta e = new Aresta(origem, destino);
            origem.addAdj(e);
            this.arestas.add(e);
            return e;
        }
    }

    public void removerAresta(Aresta aresta) {
        this.arestas.remove(aresta);
    }

    @Override
    public String toString() {
        String r = "";

        for (Vertice u : this.vertices) {
            r += u.getId() + " -> ";

            for (Aresta e : u.getAdj()) {
                Vertice v = e.getDestino();
                r += v.getId() + ", ";
            }
            r += "\n";
        }
        return r;
    }

    public int[][] getMatrizIncidencia() {
        int linha = this.getVertices().size();
        int coluna = this.getArestas().size();
        int[][] matrizIncidencia = new int[linha][coluna];
        int i, j;
        for (i = 0; i < linha; i++) {
            for (j = 0; j < coluna; j++) {
                if (this.isOrientado()) {

                    if (this.getVertices().get(i).getId().equals(this.getArestas().get(j).getOrigem().getId()) && this.getVertices().get(i).getId().equals(this.getArestas().get(j).getDestino().getId())) {
                        matrizIncidencia[i][j] = 2;
                    } else {
                        if (this.getVertices().get(i).getId().equals(this.getArestas().get(j).getOrigem().getId())) {
                            matrizIncidencia[i][j] = 1;
                        }
                        if (this.getVertices().get(i).getId().equals(this.getArestas().get(j).getDestino().getId())) {
                            matrizIncidencia[i][j] = -1;
                        }
                    }
                } else {
                    if (this.getVertices().get(i).getId().equals(this.getArestas().get(j).getOrigem().getId()) && this.getVertices().get(i).getId().equals(this.getArestas().get(j).getDestino().getId())) {
                        matrizIncidencia[i][j] = 2;
                    } else if ((this.getVertices().get(i).getId().equals(this.getArestas().get(j).getOrigem().getId()))
                            || (this.getVertices().get(i).getId().equals(this.getArestas().get(j).getDestino().getId()))) {
                        matrizIncidencia[i][j] = 1;

                    }
                }
            }
        }

        return matrizIncidencia;
    }

    public int[][] getMatrizAdjacencia() {

        int n = this.getVertices().size();
        int m = this.getArestas().size();
        int[][] matrizAdjacencia = new int[n][n];
        int i, j, k;

        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                boolean teste = this.isAdjacentes(this.getVertices().get(i), this.getVertices().get(j));
                if (teste) {
                    for (k = 0; k < m; k++) {
                        for (k = 0; k < m; k++) {
                            if (this.isOrientado()) {
                                if (this.arestas.get(k).getOrigem().getId().equals(this.getVertices().get(i).getId())
                                        && this.arestas.get(k).getDestino().getId().equals(this.getVertices().get(j).getId())) {
                                    matrizAdjacencia[i][j] = 1;
                                }
                            } else {
                                matrizAdjacencia[i][j] = 1;
                            }
                        }
                    }
                }
            }
        }
        return matrizAdjacencia;
    }

    public void imprimeMatrizAdj() {
        System.out.println(getMatrizAdjacencia());
    }

    /*   V[G] é o conjunto de vértices(v) que formam o Grafo G. d[v] é o vetor de distâncias de s até cada v. 
    Admitindo-se a pior estimativa possível, o caminho infinito. π[v] identifica o vértice de onde se origina 
    uma conexão até v de maneira a formar um caminho mínimo.
        
    2º passo: temos que usar o conjunto Q, cujos vértices ainda não contém o custo do menor caminho d[v] determinado.
        Q ← V[G]
        
    3º passo: realizamos uma série de relaxamentos das arestas, de acordo com o código:
        enquanto Q ≠ ø
         u ← extrair-mín(Q)                     //Q ← Q - {u}
         para cada v adjacente a u
              se d[v] > d[u] + w(u, v)          //relaxe (u, v)
                 então d[v] ← d[u] + w(u, v)
                       π[v] ← u
    
     */
 /* METODOS DE MANIPULACAO DE VERTICE */
    public void criarVertice(String id) {
        Vertice v = this.addVertice(id);

    }

    public String listarVertice() {
        String r = "";
        for (Vertice u : this.getVertices()) {
            r += "V: " + u.getId();
            r += "\n";
        }
        return r;
    }

    public Vertice buscaVertice(String id) {
        for (Vertice u : this.vertices) {
            if (u.getId() == id) {
                System.out.println("Mesmo nome " + id);
                return u;
            } else {
                return null;
            }
        }
        return null;
    }

    public void getOrdem() {
        System.out.println(" A ordem do grafo é: " + getVertices().size());
    }

    public void getIncidencia() {
        for (int i = 0; i <= getArestas().size() - 1; i++) {
            System.out.println("Os vertices: " + getArestas().get(i).getOrigem()
                    + " e " + getArestas().get(i).getDestino() + " são incidentes a aresta: "
                    + getArestas().get(i).getNome());
        }
    }

    public int grauVerticeTotal() {
        return this.grauVerticeRecepcao() + this.grauVerticeEmissao();
    }

    public int grauVerticeRecepcao() {
        int cont = 0;
        for (Vertice v : this.vertices) {
            cont = 0;
            for (int i = 0; i < arestas.size(); i++) {
                if (arestas.get(i).getDestino() == v) {
                    cont++;
                }
            }
            //System.out.println(v.getId() + " tem grau: " + cont);
        }
        return cont;
    }

    public int grauVerticeEmissao() {
        int cont = 0;
        for (Vertice v : this.vertices) {
            cont = 0;
            for (int i = 0; i < arestas.size(); i++) {
                if (arestas.get(i).getOrigem() == v) {
                    cont++;
                }
            }
            return cont;
        }
        return 0;
    }

    public void getFonte(int id) {

        for (Aresta arestas : arestas) {
            if (arestas.getDestino() == vertices.get(id)) {
                System.out.println("Não é Fonte");
            }
            System.out.println("É fonte");
        }

    }

    public void getSumidouro(int id) {
        for (Aresta arestas : arestas) {
            if (arestas.getDestino() == vertices.get(id)) {
                System.out.println("Não é Sumidouro");
            }
            System.out.println("É Sumidouro");
        }

    }
    public void caminho(int id1, int id2) {
        int cont = 0;
        Vertice vertices = getVertices().get(id1);
        Vertice verticeSecundario;
        for (int i = 0; i < arestas.size(); i++) {
            if (arestas.get(i).getOrigem() == vertices) {
                vertices = arestas.get(i).getDestino();
                verticeSecundario =
                if (getSumidouro(arestas.get(i).getOrigem()) == true) {
                    if (vertices == getVertices().get(id2)) {
                        cont++;
                        System.out.println("Existe caminho");
                    }
                } else {

                }
            }
        }
        if (cont < 1) {
            System.out.println("Não Existe caminho");
        }
    }
    
    public void cadeia(int id1, int id2) {
        int cont = 0;
        Vertice vertices = getVertices().get(id1);
        for (int i = 0; i < arestas.size(); i++) {
            if (arestas.get(i).getOrigem() == arestas) {
                arestas = (List<Aresta>) arestas.get(i).getDestino();
                if (vertices == getVertices().get(id2)) {
                    cont++;
                    System.out.println("Existe Cadeia");
                    break;
                }
            }
        }
        vertices = getVertices().get(id2);
        for (int i = 0; i < arestas.size(); i++) {
            if (arestas.get(i).getDestino() == arestas) {
                arestas = (List<Aresta>) arestas.get(i).getOrigem();
                if (arestas == getArestas().get(id2)) {
                    cont++;
                    System.out.println("Existe Cadeia");
                    break;
                }
            }
        }
        if (cont < 1) {
            System.out.println("Não Existe Cadeia");
        }
    }

    /* METODOS DE MANIPULAÇÃO DE ARESTA */
    public void criarAresta(int idOrigem, int idDestino) {
        if (this.getVertices().isEmpty()) {
            System.out.println("Nao existem vertices");
        } else {
            Aresta aresta = this.addAresta(this.getVertices().get(idOrigem),
                    this.getVertices().get(idDestino));
        }
    }

    public boolean isAdjacentes(Vertice v1, Vertice v2) {
        for (int i = 0; i < this.arestas.size(); i++) {
            if (this.arestas.get(i).getDestino().getId().equals(v1.getId())
                    && this.arestas.get(i).getOrigem().getId().equals(v2.getId())
                    || this.arestas.get(i).getOrigem().getId().equals(v1.getId())
                    && this.arestas.get(i).getDestino().getId().equals(v2.getId())) {

                return true;
            }
        }
        return false;
    }


    /* METODOS DE XML */
    public void gravarXML() {
        try {
            FileWriter arquivo = new FileWriter("grafo.xml");
            PrintWriter gravarArquivo = new PrintWriter(arquivo);

            gravarArquivo.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            gravarArquivo.printf("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \n"
                    + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                    + "    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
                    + "     http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");

            gravarArquivo.printf("  <graph id='1' edgedefault='direcao'>\n");

            for (Vertice v : this.getVertices()) {
                gravarArquivo.printf("      <node id='" + v.getId() + "'/>\n");
            }

            for (Aresta aresta : this.getArestas()) {
                gravarArquivo.printf("      <edge source='" + aresta.getOrigem().getId() + "' target='" + aresta.getDestino().getId() + "'/>\n");
            }

            gravarArquivo.printf("  </graph>\n");
            gravarArquivo.printf("</graphml>");

            arquivo.close();
        } catch (IOException ex) {
            System.out.println("Erro ao gerar XML!");
        }
    }

    public void lerXML(String caminhoArquivo) {
        try {
            BufferedReader arquivo = new BufferedReader(new FileReader(caminhoArquivo));

            while (arquivo.ready()) {
                System.out.println(arquivo.readLine());
            }
        } catch (IOException ex) {
            System.out.println("Erro ao ler XML!");
        }
    }
}
