package thirdParty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import modelo.Edge;
import modelo.Grafo;
import modelo.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class GeradorXml {

    public static void gerarXmlGrafo(String nomeGrafo, Grafo grafo) throws IOException {
        try (FileWriter arquivo = new FileWriter(nomeGrafo + ".xml")) {
            PrintWriter gravarArquivo = new PrintWriter(arquivo);

            gravarArquivo.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            gravarArquivo.printf("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \n"
                    + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                    + "    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
                    + "     http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");

            gravarArquivo.printf("  <graph id='" + grafo.getId() + "' edgedefault='" + grafo.getEdgedefault() + "'>\n");

            for (Node no : grafo.getNodes()) {
                if (no.getLabel() != null) {
                    gravarArquivo.printf("      <node id='" + no.getId() + "'>\n");
                    gravarArquivo.printf("          <data key= 'd0'>" + no.getLabel() + "</data>\n");
                    gravarArquivo.printf("      </node>\n");
                } else {
                    gravarArquivo.printf("      <node id='" + no.getId() + "'/>\n");
                }
            }

            grafo.getEdges().forEach((edge) -> {
                if (edge.getPeso() != 0) {
                    gravarArquivo.printf("      <edge id='" + edge.getId() + "' source='" + edge.getOrigem().getId() + "' target='" + edge.getDestino().getId() + "'>\n");
                    gravarArquivo.printf("          <data key= 'd1'>" + edge.getPeso() + "</data>\n");
                    gravarArquivo.printf("      </edge>\n");

                } else {
                    gravarArquivo.printf("      <edge id='" + edge.getId() + "' source='" + edge.getOrigem().getId() + "' target='" + edge.getDestino().getId() + "'/>\n");
                }
            });
            gravarArquivo.printf(
                    "  </graph>\n");
            gravarArquivo.printf(
                    "</graphml>");
        } catch (IOException ex) {
            System.out.println("Erro ao gerar XML");
            System.out.println(ex);
        }
    }

    public static Grafo lerXmlGrafo(String nomeArquivo) {
        Grafo grafo = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            File arquivo = new File(nomeArquivo + ".xml");

            if (!arquivo.exists()) {
                return null;
            }

            Document doc = builder.parse(nomeArquivo + ".xml");

            NodeList listaGrafo = doc.getElementsByTagName("graph");
            NodeList listaNode = doc.getElementsByTagName("node");
            NodeList listaEdge = doc.getElementsByTagName("edge");
            NodeList listaData = doc.getElementsByTagName("data");

            int tamanhoListaNode = listaNode.getLength();
            int tamanhoListaEdge = listaEdge.getLength();
            int tamanhoListaData = listaData.getLength();

            org.w3c.dom.Node noGrafo = listaGrafo.item(0);

            if (noGrafo.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element elementoGrafo = (Element) noGrafo;
                String id = elementoGrafo.getAttribute("id");
                String edgeDefault = elementoGrafo.getAttribute("edgedefault");

                grafo = new Grafo(id, edgeDefault);
            }

            ArrayList<org.w3c.dom.Node> listaDataEdge = new ArrayList<>();
            ArrayList<org.w3c.dom.Node> listaDataNode = new ArrayList<>();
            for (int i = 0; i < tamanhoListaData; i++) {
                org.w3c.dom.Node dataNode = listaData.item(i);
                if (dataNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elemento = (Element) dataNode;
                    if (elemento.getAttribute("key").equals("d0")) {
                        listaDataNode.add(dataNode);
                    } else if (elemento.getAttribute("key").equals("d1")) {
                        listaDataEdge.add(dataNode);
                    }
                }
            }

            for (int i = 0; i < tamanhoListaNode; i++) {
                org.w3c.dom.Node noNode = listaNode.item(i);
                Node no = null;

                if (noNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementoNode = (Element) noNode;
                    String id = elementoNode.getAttribute("id");

                    if (!listaDataNode.isEmpty()) {
                        String label = listaDataNode.get(i).getTextContent();
                        no = new Node(id, label);
                        System.out.println("Valor nó: " + no.getLabel());
                    } else {
                        no = new Node(id);
                    }
                    grafo.addNode(no);
                }
            }

            for (int i = 0; i < tamanhoListaEdge; i++) {
                org.w3c.dom.Node noEdge = listaEdge.item(i);
                Edge aresta = null;

                if (noEdge.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementoEdge = (Element) noEdge;
                    String id = elementoEdge.getAttribute("id");
                    String source = elementoEdge.getAttribute("source");
                    String target = elementoEdge.getAttribute("target");

                    if (!listaDataEdge.isEmpty()) {
                        String peso = listaDataEdge.get(i).getTextContent();
                        aresta = new Edge(id, grafo.buscaNode(source), grafo.buscaNode(target), peso);
                        System.out.println("Peso da aresta: " + aresta.getPeso());

                    } else {
                        aresta = new Edge(id, grafo.buscaNode(source), grafo.buscaNode(target));
                    }
                    grafo.addEdge(aresta);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
        }
        return grafo;
    }
}
