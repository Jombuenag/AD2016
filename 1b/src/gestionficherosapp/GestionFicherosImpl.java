package gestionficherosapp;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import gestionficheros.FormatoVistas;
import gestionficheros.GestionFicheros;
import gestionficheros.GestionFicherosException;
import gestionficheros.TipoOrden;

public class GestionFicherosImpl implements GestionFicheros {
	private File carpetaDeTrabajo = null;
	private Object[][] contenido;
	private int filas = 0;
	private int columnas = 3;
	private FormatoVistas formatoVistas = FormatoVistas.NOMBRES;
	private TipoOrden ordenado = TipoOrden.DESORDENADO;

	public GestionFicherosImpl() {
		carpetaDeTrabajo = File.listRoots()[0];
		try {
			actualiza();
		} catch (GestionFicherosException e) {
			e.printStackTrace();
		}
	}

	private void actualiza()throws GestionFicherosException{
		String[] ficheros = carpetaDeTrabajo.list(); // obtener los nombres
		// calcular el número de filas necesario
		filas = ficheros.length / columnas;
		if (filas * columnas < ficheros.length) {
			filas++; // si hay resto necesitamos una fila más
		}
		// dimensionar la matriz contenido según los resultados
		contenido = new String[filas][columnas];
		// Rellenar contenido con los nombres obtenidos
		for (int i = 0; i < columnas; i++) {
			for (int j = 0; j < filas; j++) {
				int ind = j * columnas + i;
				if (ind < ficheros.length) {
					contenido[j][i] = ficheros[ind];
				} else {
					contenido[j][i] = "";
				}
			}
		}
	}

	@Override
	public void arriba() {
		if (carpetaDeTrabajo.getParentFile() != null) {
			carpetaDeTrabajo = carpetaDeTrabajo.getParentFile();
			try {
				actualiza();
			} catch (GestionFicherosException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void creaCarpeta(String arg0) throws GestionFicherosException {
		File file = new File(carpetaDeTrabajo,arg0);
		if(!file.exists()){
			if(file.mkdir()){
				try{
				file.createNewFile();
				file.canRead();
				file.canWrite();
				file.canExecute();
				}catch(IOException e){
					e.printStackTrace();
				}
				System.out.println("Se ha creado la carpeta");
			}else{
				throw new GestionFicherosException("Error. No se ha podido "
						+ "crear la carpeta " + file.getAbsolutePath());
			}
			actualiza();
		}
		if(file.exists()){
			throw new GestionFicherosException("Error, la carpeta ya existe "
					+ file.getAbsolutePath());
		}
		actualiza();
		//que se pueda escribir -> lanzará una excepción
		//que no exista -> lanzará una excepción
		//crear la carpeta -> lanzará una excepción
	}

	@Override
	public void creaFichero(String arg0) throws GestionFicherosException {
		File file = new File(carpetaDeTrabajo, arg0);
		if(!file.exists()){
			if(!file.mkdir()){
				try{
				file.createNewFile();
				actualiza();
				file.canRead();
				file.canWrite();
				file.canExecute();
				}catch(IOException e){
					System.out.println("No se pudo crear por falta de permisos");
					e.printStackTrace();
				}
				System.out.println("Se ha creado el archivo");
			}
		}
		actualiza();
	}

	@Override
	public void elimina(String arg0) throws GestionFicherosException {
		File file = new File(carpetaDeTrabajo, arg0);
		file.delete();
		actualiza();
	}

	@Override
	public void entraA(String arg0) throws GestionFicherosException {
		File file = new File(carpetaDeTrabajo, arg0);
		// se controla que el nombre corresponda a una carpeta existente
		if (!file.isDirectory()) {
			throw new GestionFicherosException("Error. Se ha encontrado "
					+ file.getAbsolutePath()
					+ " pero se esperaba un directorio");
		}
		if(!file.canExecute()){
			throw new GestionFicherosException("Alerta. No se puede acceder a "
					+ file.getAbsolutePath() + ". No hay permiso");
		}
		// se controla que se tengan permisos para leer carpeta
		if (!file.canRead()) {
			throw new GestionFicherosException("Alerta. No se puede acceder a "
					+ file.getAbsolutePath() + ". No hay permiso");
		}
		// nueva asignación de la carpeta de trabajo
		carpetaDeTrabajo = file;
		// se requiere actualizar contenido
		actualiza();

	}

	@Override
	public int getColumnas() {
		return columnas;
	}

	@Override
	public Object[][] getContenido() {
		return contenido;
	}

	@Override
	public String getDireccionCarpeta() {
		return carpetaDeTrabajo.getAbsolutePath();
	}

	@Override
	public String getEspacioDisponibleCarpetaTrabajo() {
		carpetaDeTrabajo.getAbsolutePath();
		return null;
	}

	@Override
	public String getEspacioTotalCarpetaTrabajo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFilas() {
		return filas;
	}

	@Override
	public FormatoVistas getFormatoContenido() {
		return formatoVistas;
	}

	@Override
	public String getInformacion(String arg0) throws GestionFicherosException {
		
		StringBuilder strBuilder=new StringBuilder();
		File file = new File(carpetaDeTrabajo,arg0);
		String ubicacion = new String();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		DecimalFormat dF = new DecimalFormat("#0.000000");
		//Controlar que existe. Si no, se lanzará una excepción
		//Controlar que haya permisos de lectura. Si no, se lanzará una excepción
		
		//Título
		strBuilder.append("INFORMACIÓN DEL SISTEMA");
		strBuilder.append("\n\n");
		
		//Nombre
		strBuilder.append("Nombre: ");
		strBuilder.append(arg0);
		strBuilder.append("\n");
		
		//Tipo: fichero o directorio
		strBuilder.append("Tipo: ");
		if(file.isDirectory()){
			strBuilder.append(" Directorio");
		}else{
			strBuilder.append(" Archivo");
		}
		strBuilder.append("\n");
		
		//Ubicación
		strBuilder.append("Ubicación : ");
		try {
			file.getCanonicalPath();
			ubicacion = file.toString();
			strBuilder.append(" " + ubicacion);
			strBuilder.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Fecha de última modificación
		strBuilder.append("Ultima modificacíon: ");
		strBuilder.append(sdf.format(file.lastModified()));
		strBuilder.append("\n");
		//Si es un fichero oculto o no
		strBuilder.append("Oculto: ");
		if(!file.isHidden()){
			strBuilder.append("NO");
		}else{
			strBuilder.append("SI");
		}
		strBuilder.append("\n\n");
		//Si es directorio: Espacio libre, espacio disponible, espacio total
		//bytes
		strBuilder.append("INFORMACIÓN SECUNDARIA");
		strBuilder.append("\n\n");
		if(file.isDirectory()){
			strBuilder.append("Espacio Libre: ");
			strBuilder.append(file.getFreeSpace() /1024 /1024 + " Mb" + "\n");
			strBuilder.append("Espacio Disponible: ");	
			strBuilder.append(file.getUsableSpace() /1024 /1024 + " Mb" + "\n");
			strBuilder.append("Espacio Total: ");
			strBuilder.append(file.getTotalSpace() /1024 /1024 + " Mb" + "\n");
			strBuilder.append("\n");
		}
		if(!file.isDirectory()){
			double bytes = file.length();
			strBuilder.append("Tamaño del archivo: ");
			strBuilder.append(dF.format(bytes /1024 /1024) +" Mb" + "\n");
		}

		if(file.isDirectory()){
			strBuilder.append("Número de archivos: ");
			File[] files = file.listFiles();
			strBuilder.append(files.length);
			strBuilder.append("\n");
		}
		
		return strBuilder.toString();
	}
	
	@Override
	public boolean getMostrarOcultos() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNombreCarpeta() {
		return carpetaDeTrabajo.getName();
	}

	@Override
	public TipoOrden getOrdenado() {
		return ordenado;
	}

	@Override
	public String[] getTituloColumnas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getUltimaModificacion(String arg0)
			throws GestionFicherosException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String nomRaiz(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numRaices() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void renombra(String arg0, String arg1) throws GestionFicherosException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sePuedeEjecutar(String arg0) throws GestionFicherosException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sePuedeEscribir(String arg0) throws GestionFicherosException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sePuedeLeer(String arg0) throws GestionFicherosException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setColumnas(int arg0) {
		columnas = arg0;

	}

	@Override
	public void setDirCarpeta(String arg0) throws GestionFicherosException {
		File file = new File(arg0);

		// se controla que la dirección exista y sea directorio
		if (!file.isDirectory()) {
			throw new GestionFicherosException("Error. Se esperaba "
					+ "un directorio, pero " + file.getAbsolutePath()
					+ " no es un directorio.");
		}

		// se controla que haya permisos para leer carpeta
		if (!file.canRead()) {
			throw new GestionFicherosException(
					"Alerta. No se puede acceder a  " + file.getAbsolutePath()
							+ ". No hay permisos");
		}

		// actualizar la carpeta de trabajo
		carpetaDeTrabajo = file;

		// actualizar el contenido
		actualiza();

	}

	@Override
	public void setFormatoContenido(FormatoVistas arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMostrarOcultos(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOrdenado(TipoOrden arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSePuedeEjecutar(String arg0, boolean arg1)
			throws GestionFicherosException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSePuedeEscribir(String arg0, boolean arg1)
			throws GestionFicherosException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSePuedeLeer(String arg0, boolean arg1)
			throws GestionFicherosException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUltimaModificacion(String arg0, long arg1)
			throws GestionFicherosException {
		// TODO Auto-generated method stub

	}

}




