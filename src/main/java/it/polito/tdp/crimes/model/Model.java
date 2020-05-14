package it.polito.tdp.crimes.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;
	private List<String> best;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<String> getAllCategories(){
		return dao.getAllCategories();
	}
	
	public List<Month> getAllMonths(){
		List<Month> l = dao.getAllMonths();
		Collections.sort(l);
		return l;
	}
	
	public void creaGrafo(Month mese, String categoria) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		List<Adiacenza> adiacenze = dao.getAdiacenze(categoria, mese.getValue());
		for(Adiacenza a: adiacenze) {
			if(!this.grafo.containsVertex(a.getV1()))
				this.grafo.addVertex(a.getV1());
			if(!this.grafo.containsVertex(a.getV2()))
				this.grafo.addVertex(a.getV2());
			if(this.grafo.getEdge(a.getV1(), a.getV2()) == null)
				Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		
		System.out.println(String.format("Grafo creato con %d vertici e %d archi", grafo.vertexSet().size(), grafo.edgeSet().size()));
	}
	
	public List<Arco> getArchi(){
		List<Arco> archi = new ArrayList<>();
		double pesoMedio = 0.0;
		for(DefaultWeightedEdge e: grafo.edgeSet()) {
			pesoMedio = grafo.getEdgeWeight(e);
		}
		pesoMedio = pesoMedio/grafo.edgeSet().size();
		for(DefaultWeightedEdge e: grafo.edgeSet())
			if(grafo.getEdgeWeight(e) > pesoMedio)
				archi.add(new Arco(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
		Collections.sort(archi);
		return archi;		
	}
	
	public List<String> trovaPercorso(String sorgente, String destinazione){
		List<String> parziale = new ArrayList<>();
		best = new ArrayList<>();
		parziale.add(sorgente);
		trovaRicorsivo(destinazione, parziale, 0);
		return best;
	}

	private void trovaRicorsivo(String destinazione, List<String> parziale, int i) {
		// caso terminale ---> quando l'ultimo vertice inserito in parziale è uguale alla destinazione
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size() > best.size())
				best = new ArrayList<>(parziale);
			return;
		}
		
		// scorro i vicini dell'ultimo vertice inserito in parziale
		for(String vicino: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			// cammino aciclico ---> controllo che il vertice non sia già in parziale
			if(!parziale.contains(vicino)) {
				// provo ad aggiungerlo
				parziale.add(vicino);
				// continuo la ricorsione
				trovaRicorsivo(destinazione, parziale, i+1);
				// faccio backtracking
				parziale.remove(parziale.get(parziale.size()-1));
			}
		}
	}
		
}
