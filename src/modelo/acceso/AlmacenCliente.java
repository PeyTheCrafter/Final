package modelo.acceso;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 
 * @author Pablo
 *
 * @param <T>
 * @param <K>
 */
public class AlmacenCliente<T, K> {
	private String pathIndice;
	private String pathDatos;
	private TreeMap<K, Integer> indice;
	private DAO<T> dao;

	public AlmacenCliente(String ruta) {
		super();
		this.pathIndice = ruta + "/clientes.index";
		this.pathDatos = ruta + "/clientes.data";
		comprobarExistente(ruta);
		assert validate();
		this.indice = new TreeMap<>();
		dao = new DAO<>();
	}

	private void comprobarExistente(String path) {
		File ruta = new File(path);
		File datos = new File(this.pathDatos);
		File indices = new File(this.pathIndice);
		if (!ruta.exists()) {
			ruta.mkdirs();
		}
		if (!datos.exists()) {
			try {
				datos.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!indices.exists()) {
			try {
				indices.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private boolean validate() {
		return this.pathIndice != null && this.pathDatos != null;
	}

	public T obtener(K k) {
		leerIndice();
		if (indice == null) {
			indice = new TreeMap<>();
			dao.grabar(pathIndice, (T) indice);
		}
		T retorno = null;
		Integer posicion = indice.get(k);
		if (posicion != null) {
			retorno = (T) dao.leer(pathDatos, posicion);
		}
		return retorno;
	}

	/**
	 * Almacen el elemnto de clase T con Clave K, hay que pasarla
	 * 
	 * @param t
	 *            el objeto a grabar
	 * @param k
	 *            la propiedad clave o indice del objeto t
	 * @return true si ha almacenado y false en caso contrario
	 */
	public boolean grabar(T t, K k) {
		boolean retorno = false;
		Integer value = indice.size();
		if (indice.put(k, value) == null) {
			// si se almacena bien en el archivo de datos
			if (dao.grabar(pathDatos, t, true)) {
				retorno = true;
				dao.grabar(pathIndice, (T) indice);
			} else {
				//Si no se graba bien actualizamos el indice con la version grabada
				leerIndice();
			}
		}
		return retorno;
	}

	private void leerIndice() {
		indice = (TreeMap<K, Integer>) dao.leer(pathIndice);
	}
	
	public boolean borrar(K k){
		leerIndice();
		boolean retorno=false;
		if(indice.containsKey(k)){
			Integer posicion=indice.remove(k);
			if(posicion!=null){
				retorno=dao.borrarElemtento(pathDatos,posicion);
				if(!retorno){
					leerIndice();
				}
			}
		}
		return retorno;
	}

	/**
	 * devuelve el indice del paquete
	 * 
	 * @return
	 */
	public Object getIndice() {
		return (TreeMap) new DAO<>().leer(pathIndice);
	}

	/**
	 * comprueba si existe el paquete con la ruta dada
	 * 
	 * @param ruta
	 * @return
	 */
	private boolean comprobarExiste(String ruta) {
		File archivo = new File(ruta);
		return archivo.exists();
	}
}
