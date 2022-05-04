package com.proyecto.iasi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Astar {

	private List<String> path;

	// Listado de cerrados y abiertos
	private List<Board> open;
	private List<Board> close;

	private boolean isSolution;

	public Astar() {
		super();
		this.path = new ArrayList<String>();
		this.open = new ArrayList<Board>();
		this.close = new ArrayList<Board>();
		this.isSolution = false;
	}

	public void findSolution(Board tab) {
		System.out.println("\n******************************************************");
		System.out.println("Buscando solucion aplicando algoritmo de A star");

		long start = System.currentTimeMillis();

		searchSolution(tab);

		if (this.isSolution) {
			System.out.println("Solucion Encontrada");
		} else {
			System.out.println("No se ha encontrado solucion");
		}

		long end = System.currentTimeMillis();
		double time = (double) (end - start);
		System.out.println("Tiempo transcurrido: " + time + " ms");

		System.out.println("\nFin - A star");
		System.out.println("******************************************************\n");
	}

	/*
	 * Vamos a utilizar un esquema voraz para la resolucion del algoritmo A star
	 */
	private void searchSolution(Board tab) {
		// Aniadimos el estado inicial del tablero a la lista de abiertos
		open.add(tab);

		Board actual = tab.copyBoard();

		while (!actual.isEnd() && !this.open.isEmpty()) {
			// Eliminamos el primer (y mejor) nodo de los abiertos
			open.remove(0);

			// Insertamos el nodo actual en la lista de cerrados
			close.add(actual);

			// Generamos los nodos sucesores al nodo actual
			List<Board> childs = createSuccessors(actual);

			// Tratamos los repetidos
			childs = isRepeated(childs);

			// Insertamos los hijos en la lista de abiertos
			for (int i = 0; i < childs.size(); i++) {
				open.add(childs.get(i));
			}

			// Ordenamos la lista de abiertos
			open.sort(new Comparator<Board>() {
				@Override
				public int compare(Board o1, Board o2) {
					if ((o1.getActualHeuristic() - o1.getG()) < (o2.getActualHeuristic() - o2.getG()))
						return 1;
					else if ((o1.getActualHeuristic() - o1.getG()) > (o2.getActualHeuristic() - o2.getG()))
						return -1;
					else
						return 0;
				}
			});

			// Actualizamos el nodo actual al primero que tenemos en la lista de abiertos
			if (!open.isEmpty())
				actual = open.get(0);
		}

		if (actual.isEnd()) {
			// Informamos la variable de la clase para indicar que se ha llegado al fin
			this.isSolution = true;

			// Mostramos el camino recorrido
			System.out.println("\nCamino recorrido: ");

			// Cargamos los movimientos generados en orden inverso en una lista
			Board aux = actual.getPrevBoard();
			while (aux != null) {
				if (!aux.getMov().isEmpty() || !aux.getMov().isBlank())
					path.add(aux.getMov());
				aux = aux.getPrevBoard();
			}

			// Recorremos la lista en orden inverso
			for (int i = path.size() - 1; i != 0; i--) {
				System.out.println("- " + path.get(i));
			}

			System.out.println("\nEstado final del tablero:");
			System.out.println("\n" + actual);
		}
	}

	private List<Board> isRepeated(List<Board> childs) {
		// Si alguno de los hijos generados ya estan en la lista de abiertos o cerrados
		// se descartara
		for (int j = 0; j < close.size(); j++) {
			for (int i = 0; i < childs.size(); i++) {
				if (childs.get(i).equals(close.get(j)))
					childs.remove(i);
			}
		}

		for (int j = 0; j < close.size(); j++) {
			for (int i = 0; i < childs.size(); i++) {
				if (childs.get(i).equals(close.get(j)))
					childs.remove(i);
			}
		}

		return childs;
	}

	private List<Board> createSuccessors(Board actual) {
		List<Board> hijos = new ArrayList<Board>();

		List<String> pos = actual.getAvaibleMovFB();

		Board aux = null;

		// Para cada movimiento posible creamos un nuevo nodo o Tablero donde realizamos
		// el movimiento
		for (int i = 0; i < pos.size(); i++) {
			aux = actual.copyBoard();
			aux.doMovement(pos.get(i));
			aux.setG(actual.getG() + 5);
			hijos.add(aux);
		}

		return hijos;
	}

	public List<String> getPath() {
		return path;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}

	public List<Board> getOpen() {
		return open;
	}

	public void setOpen(List<Board> open) {
		this.open = open;
	}

	public List<Board> getClose() {
		return close;
	}

	public void setClose(List<Board> close) {
		this.close = close;
	}

	public boolean isEnd() {
		return isSolution;
	}

	public void setIsSolution(boolean isSolution) {
		this.isSolution = isSolution;
	}

}