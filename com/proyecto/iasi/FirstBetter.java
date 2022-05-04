package com.proyecto.iasi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FirstBetter {

	private List<String> camino;

	// Listado de cerrados y abiertos
	private List<Board> abiertos;
	private List<Board> cerrados;

	private boolean esFin;

	public FirstBetter() {
		super();
		this.camino = new ArrayList<String>();
		this.abiertos = new ArrayList<Board>();
		this.cerrados = new ArrayList<Board>();
		this.esFin = false;
	}

	public void buscarSolucion(Board tab) {
		System.out.println("\n******************************************************");
		System.out.println("Buscando solucion aplicando algoritmo de PRIMERO MEJOR");

		long inicio = System.currentTimeMillis();

		bucaSolucion(tab);

		if (this.esFin) {
			System.out.println("Solucion Encontrada");
		} else {
			System.out.println("No se ha encontrado solucion");
		}

		long finT = System.currentTimeMillis();
		double tiempo = (double) (finT - inicio);
		System.out.println("Tiempo transcurrido: " + tiempo + " ms");

		System.out.println("\nFin - PRIMERO MEJOR");
		System.out.println("******************************************************\n");
	}

	/*
	 * Vamos a utilizar un esquema voraz para la resolucion del algoritmo Primero
	 * Mejor
	 */
	private void bucaSolucion(Board tab) {
		// Aniadimos el estado inicial del tablero a la lista de abiertos
		abiertos.add(tab);

		Board actual = tab.copyBoard();

		while (!actual.isEnd() && !this.abiertos.isEmpty()) {
			// Eliminamos el primer (y mejor) nodo de los abiertos
			abiertos.remove(0);

			// Insertamos el nodo actual en la lista de cerrados
			cerrados.add(actual);

			// Generamos los nodos sucesores al nodo actual
			List<Board> hijos = generarSucesores(actual);

			// Tratamos los repetidos
			hijos = tratarRepetidos(hijos);

			// Insertamos los hijos en la lista de abiertos
			for (int i = 0; i < hijos.size(); i++) {
				abiertos.add(hijos.get(i));
			}

			// Ordenamos la lista de abiertos
			abiertos.sort(new Comparator<Board>() {
				@Override
				public int compare(Board o1, Board o2) {
					if (o1.getActualHeuristic() < o2.getActualHeuristic())
						return 1;
					else if (o1.getActualHeuristic() > o2.getActualHeuristic())
						return -1;
					else
						return 0;
				}
			});

			// Actualizamos el nodo actual al primero que tenemos en la lista de abiertos
			if (!abiertos.isEmpty())
				actual = abiertos.get(0);
		}

		if (actual.isEnd()) {
			// Informamos la variable de la clase para indicar que se ha llegado al fin
			this.esFin = true;

			// Mostramos el camino recorrido
			System.out.println("\nCamino recorrido: ");

			// Cargamos los movimientos generados en orden inverso en una lista
			Board aux = actual.getPrevBoard();
			while (aux != null) {
				if (!aux.getMov().isEmpty() || !aux.getMov().isBlank())
					camino.add(aux.getMov());
				aux = aux.getPrevBoard();
			}

			// Recorremos la lista en orden inverso
			for (int i = camino.size() - 1; i != 0; i--) {
				System.out.println("- " + camino.get(i));
			}

			System.out.println("\nEstado final del tablero:");
			System.out.println("\n" + actual);
		}
	}

	private List<Board> tratarRepetidos(List<Board> hijos) {
		// Si alguno de los hijos generados ya estan en la lista de abiertos o cerrados
		// se descartara
		for (int j = 0; j < abiertos.size(); j++) {
			for (int i = 0; i < hijos.size(); i++) {
				if (hijos.get(i).equals(abiertos.get(j)))
					hijos.remove(i);
			}
		}

		for (int j = 0; j < cerrados.size(); j++) {
			for (int i = 0; i < hijos.size(); i++) {
				if (hijos.get(i).equals(cerrados.get(j)))
					hijos.remove(i);
			}
		}

		return hijos;
	}

	private List<Board> generarSucesores(Board actual) {
		List<Board> hijos = new ArrayList<Board>();

		List<String> pos = actual.getAvaibleMovFB();

		Board aux = null;

		// Para cada movimiento posible creamos un nuevo nodo o Tablero donde realizamos
		// el movimiento
		for (int i = 0; i < pos.size(); i++) {
			aux = actual.copyBoard();
			aux.doMovement(pos.get(i));
			hijos.add(aux);
		}

		return hijos;
	}

	public List<String> getCamino() {
		return camino;
	}

	public void setCamino(List<String> camino) {
		this.camino = camino;
	}

	public List<Board> getAbiertos() {
		return abiertos;
	}

	public void setAbiertos(List<Board> abiertos) {
		this.abiertos = abiertos;
	}

	public List<Board> getCerrados() {
		return cerrados;
	}

	public void setCerrados(List<Board> cerrados) {
		this.cerrados = cerrados;
	}

	public boolean isEsFin() {
		return esFin;
	}

	public void setEsFin(boolean esFin) {
		this.esFin = esFin;
	}

}
