package com.proyecto.iasi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RotaL {

	private Board tab;

	private int tamX;
	private int tamY;

	private FirstBetter pMejor;
	private Astar a;

	public RotaL() {
		cargaTablero();
		this.pMejor = new FirstBetter();
		this.a = new Astar();
	}

	private int calculoH(Casilla casilla) {
		int xOrig = casilla.getCoordX();
		int yOrig = casilla.getCoordY();

		int xDest = this.tab.getObjetive()[this.tab.getObjetiveVertex()].getCoordX();
		int yDest = this.tab.getObjetive()[this.tab.getObjetiveVertex()].getCoordY();

		// Calculamos la distancia entre los dos puntos
		int cat1 = xOrig - xDest;
		int cat2 = yOrig - yDest;
		int hip = (int) Math.sqrt(cat1 * cat1 + cat2 * cat2);

		return (this.tamX - hip) * 7;
	}

	private void cargaHeuristica1() {
		// Recorrera el tablero e ira indicando un valor heuristico h' a cada una de las
		// casillas que no sean muros.
		// Cuanto mas cerca este una casilla de la Pieza Objetivo mayor sera su h'
		int valorH = 0;
		int cont = 0;
		for (int i = 0; i < this.tamX; i++) {
			for (int j = 0; j < this.tamY; j++) {

				if (this.tab.getBox(i, j).getTipo() != 1) {
					valorH = calculoH(this.tab.getBox(i, j));

					cont = 0;
					// Si tenemos algun vecino del tipo 1 se decrementa el valor de H
					if (this.tab.getBox(i - 1, j).getTipo() == 1) {
						valorH -= 5;
						cont++;
					}
					if (this.tab.getBox(i + 1, j).getTipo() == 1) {
						valorH -= 5;
						cont++;
					}
					if (this.tab.getBox(i, j - 1).getTipo() == 1) {
						valorH -= 5;
						cont++;
					}
					if (this.tab.getBox(i, j + 1).getTipo() == 1) {
						valorH -= 5;
						cont++;
					}
					if (cont == 0)
						valorH += 7;
					if (this.tab.getBox(i, j + 1).getTipo() == 3)
						valorH = 99;

					this.tab.getBox(i, j).setH(valorH);
				}

			}
		}

		// La heuristica del vertice del objetivo es la mayor
//		this.tab.getCasilla(this.tab.getObjetivo()[this.tab.getVerticeObjetivo()].getCoordX(), this.tab.getObjetivo()[this.tab.getVerticeObjetivo()].getCoordY()).setH(99);

		mostrarHeuristica();
	}

	private void cargaHeuristica2() {
		// Recorrera el tablero e ira indicando un valor heuristico h' a cada una de las
		// casillas que no sean muros.
		// Cuanto mas cerca este una casilla de la Pieza Objetivo mayor sera su h'
		int valorH = 0;
		for (int i = 0; i < this.tamX; i++) {
			for (int j = 0; j < this.tamY; j++) {

				if (this.tab.getBox(i, j).getTipo() == 0 || this.tab.getBox(i, j).getTipo() == 2) {
					valorH = calculoH(this.tab.getBox(i, j));

					// Sumamos 2 al valor de H por cada casilla libre que tenga a su alrededor
					if (i > 2) {
						if (this.tab.getBox(i - 1, j).getTipo() != 1)
							valorH += 3;
						else
							valorH -= 1;
					}

					if (i < tamX - 2) {
						if (this.tab.getBox(i + 1, j).getTipo() != 1)
							valorH += 3;
						else
							valorH -= 1;
					}

					if (j > 2) {
						if (this.tab.getBox(i, j - 1).getTipo() != 1)
							valorH += 3;
						else
							valorH -= 1;
					}

					if (j < tamY - 2) {
						if (this.tab.getBox(i, j + 1).getTipo() != 1)
							valorH += 3;
						else
							valorH -= 1;
					}

					this.tab.getBox(i, j).setH(valorH);
				}

			}
		}
		mostrarHeuristica();
	}

	private void cargaHeuristica3() {
		int valorH = 0;
		for (int i = 0; i < this.tamX; i++) {
			for (int j = 0; j < this.tamY; j++) {

				if (this.tab.getBox(i, j).getTipo() == 0 || this.tab.getBox(i, j).getTipo() == 2) {
					valorH = (int) Math.floor(Math.random() * 70 + 1);
					this.tab.getBox(i, j).setH(valorH);
				}

			}
		}
		mostrarHeuristica();
	}

	private void mostrarHeuristica() {
		System.out.println("\nMapa de Heuristica Generado\n");
		for (int i = 0; i < this.tamX; i++) {
			for (int j = 0; j < this.tamY; j++) {
				if (this.tab.getBox(i, j).getH() < 10)
					System.out.print("  " + this.tab.getBox(i, j).getH());
				else
					System.out.print(" " + this.tab.getBox(i, j).getH());
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public void cargaTablero() {
		System.out.println("Se carga el tablero con el fichero de entrada");
		this.tamX = 10;
		this.tamY = 10;
		// this.tab = new Tablero(tamX, tamY);

		// AMP1
		List<Casilla> casi = new ArrayList<Casilla>();

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			archivo = new File("ROTAL2.txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			int x = 0;
			int y = 0;
			int z = 0;

			// Lectura del fichero
			String linea;
			while ((linea = br.readLine()) != null) {

				System.out.println(linea);

				// En la variable linea tendremos cada una de las lineas
				// En partes tendremos cada una de las casillas de la linea
				String[] partes = linea.split(",");
				z = partes.length;
				for (int i = 0; i < partes.length; i++) {
					// this.tab.setCasilla(x, y, Integer.parseInt(partes[i]));
					casi.add(new Casilla(x, y, Integer.parseInt(partes[i])));
					y++;
				}
				y = 0;
				x++;
			}

			// AMP1
			this.tab = new Board(x, z);
			for (int i = 0; i < casi.size(); i++) {
				this.tab.setBox(casi.get(i).getCoordX(), casi.get(i).getCoordY(), casi.get(i).getTipo());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("");
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		// Cargamos la Pieza en el tablero
		this.tab.findPiece();
		this.tab.findObjetive();
	}

	public void comenzar() {
		System.out.println("Comienza la busqueda de posibles soluciones");

		cargaHeuristica1();

		Board tab1 = tab.copyBoard();
		
		this.a.findSolution(tab);
		this.pMejor.buscarSolucion(tab1);

	}

	public static void main(String[] args) {
		RotaL rotaL = new RotaL();
		rotaL.comenzar();
	}

}
