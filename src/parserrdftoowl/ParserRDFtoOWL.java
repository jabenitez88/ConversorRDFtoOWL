/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parserrdftoowl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Jose
 */
public class ParserRDFtoOWL {

    /**
     * @param args the command line arguments
     * Nuestra clase ParserRDFtoOWL no posee constructor, es una clase intermedia que contiene un método que nos va a ayudar
     * a convertir nuestro fichero en una versión de XML, en nuestra propia versión de XML.
     * Recibe por parámetro los siguientes elementos:
     * - String fichOrigen : Cadena con el nombre del fichero inicial que vamos a convertir ( ruta completa del fichero )
     * 
     * - String fichDestino : Cadena con el nombre y la ruta completa del fichero de destino ( fichero convertido )
     * 
     * - int numDePropiedades : Número entero que contiene el número total de propiedades de nuestro objeto principal 
     *                          ( del cual crearemos sus instancias , por ejemplo el objeto Ficha que contiene 
     *                          propiedades como Tipo, Nombre, Formato.... etc )
     * 
     * - String cBuscar : Cadena que delimita cada objeto instanciado en nuestro fichero de Origen, me explico : 
     *                      - En RDF Dublin core, la cadena a buscar sería "rdf:Description" , ya que los objetos instanciados
     *                        se encuentran delimitados por <rdf:Description> y </rdf:Description> 
     * 
     * - String cIden : Cadena de la que vamos a obtener los identificadores de nuestros objetos instanciados :
     *                      - En el caso de nuestro fichero en formato RDF Dublin Core, el texto que se encuentra 
     *                        entre las etiquetas <dc:identifier> y </dc:identifier> es lo que usaremos como identificador
     *                        con lo cual cIden debería ser "dc:identifier" sin comillas.
     * 
     * - String nIden : Cadena que delimita nuestros nuevos objetos creados, en nuestro caso, queremos objetos de tipo
     *                  Ficha, con lo cual la cadena a enviar sería "Ficha" sin comillas. Y los objetos que se van a crear
     *                  Son del tipo <Ficha about="Identificador unico de cada objeto"><Propiedades></Propiedades></Ficha>
     * 
     * - String[] props : Array de cadenas que almacena cada propiedad actual en el fichero Origen :
     *                  - En nuestro ejemplo, dc:type, dc:format, dc:publisher, dc:relation, dc:date son algunas de las 
     *                    propiedades de cada objeto rdf:Description.
     * 
     * - String[] propsNuevas: Array de cadenas que almacena cada propiedad nueva del fichero de destino :
     *                  - En nuestro caso, de dc:type la propiedad nueva es Tipo, de dc:format la propiedad nueva es Formato...
     * 
     * - JFrame frame : Simplemente le pasamos la ventana de nuestra aplicación para que pueda crear diálogos de información o de error.
     * 
     * - boolean debugger : Es un booleano que maneja los mensajes en consola, si es true, mostrará mensajes en consola, si es false, no.
     */
    
    public void Parseador(String fichOrigen, String fichDestino, int numDePropiedades, 
            String cBuscar, String  cIden, String nIden, String[] props, String[] propNuevas,
            JFrame frame, boolean debugger){
        // TODO code application logic here
        
        // Instanciamos e inicializamos nuestro fichero de lectura, que será
        // nuestro fichero a convertir, con sus respectivos lectores.
        // Tenemos dos lectores porque necesitaremos acceder a distintas zonas del fichero
        // en un mismo recorrido, por ello declaramos dos lectores distintos, lector1 y lector2.
        File archivo = null;
        FileReader lector1 = null;
        FileReader lector2 = null;
        BufferedReader br = null;
       
        // Instanciamos e inicializamos nuestro fichero de escritura, que será
        // nuestro fichero de destino, donde se guardará nuestro XML con nuevo
        // formato.
        FileWriter fichero = null;
        PrintWriter pw = null;
       
        
        // Instanciamos las cadenas necesarias para poder realizar la conversión.
        String cadenaBuscamos = "<" + cBuscar + ">";
        String cadenaBuscamos2 = "</" + cBuscar + ">";
        String cadenaIdentificador = "<" + cIden + ">";
        String nuevoIdentificador = nIden;
        
        String[] propiedades = new String[50];
        String[] propiedadesNuevas = new String[50];
        
        //String[] propiedades = {"dc:type","dc:format","dc:creator","dc:subject","dc:relation","dc:coverage","dc:title","dc:description","dc:language","dc:date"};
        //String[] propiedadesNuevas = {"Tipo","Formato","Creador","Tematica","Referencias","Materia","Titulo","Notas","Idioma","Publicacion"};

        // Rellenamos nuestras propiedades con las que obtenemos como parámetro.
        for(int k=0;k<numDePropiedades;k++) {
            propiedades[k] = props[k];
            propiedadesNuevas[k] = propNuevas[k];
            if(debugger) System.out.println("Propiedad Vieja " + propiedades[k]);              
            if(debugger) System.out.println("Propiedad Nueva " + propiedadesNuevas[k]);
        }
        int lineaActual = -1;
        int lineaDef = 0;
        int lineaDef2 = 0;
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda.
            archivo = new File(fichOrigen);
            lector1 = new FileReader(archivo);

            
            

            LineNumberReader lr1 = new LineNumberReader(lector1);
            String s1 = null;
            
            fichero = new FileWriter(fichDestino);
            pw = new PrintWriter(fichero);

            // Comenzamos la lectura del fichero origen con el lector1
            while ((s1 = lr1.readLine()) != null) {

                /*
                 * Si encontramos la cadena que crea cada objeto, en nuestro
                 * ejemplo <rdf:Description>, guardamos en una variable
                 * el número de línea en el que nos encontramos, para poder
                 * comenzar una lectura con nuestro lector2 más adelante en esa
                 * línea.
                 * 
                 */
                if (s1.indexOf(cadenaBuscamos) != -1) {
                    lineaDef = lr1.getLineNumber();
                    if(debugger) System.out.println("Linea # " + lineaDef + " con valor " + s1);
                }
                
                 /*
                 * Después de encontrar <rdf:Description>, el siguiente paso es
                 * encontrar la cadena que identifica cada objeto, que en nuestro
                 * caso particular es dc:identifier. En caso de encontrarla
                 * el programa entra dentro de este control if, almacena
                 * lo que se encuentra dentro de <dc:identifier></dc:identifier>
                 * y comienza a funcionar el segundo lector, lector2.
                 * 
                 */
                if (s1.indexOf(cadenaIdentificador) != -1) {

                    String s2 = null;
                    int contador = 0;
                    lector2 = new FileReader(archivo);
                    LineNumberReader lr2 = new LineNumberReader(lector2);
                    
                    lr2.setLineNumber(lineaDef);
                    
                    /* Para obtener lo que tenemos dentro de <dc:identifier>
                    * y </dc:identifier> utilizamos la cadena contenidoEtiqueta1
                    * y sacamos una subcadena de la línea que estamos leyendo, 
                    * de forma que, con esto que hemos escrito conseguimos quitar
                    * de la línea leída las etiquetas, es decir:
                    * 
                    * - Si tenemos una línea con el siguiente contenido:
                    *   <dc:identifier> Identificador </dc:identifier>
                    * Este pequeño método, obtiene la palabra "Identificador"
                    * eliminando así la etiqueta de apertura y de cierre.
                    */
                    String contenidoEtiqueta1 = null;
                    int tamPropiedad1 = cadenaIdentificador.length();
                    int tamS1 = s1.length();
                    contenidoEtiqueta1 = s1.substring(tamPropiedad1, tamS1 - tamPropiedad1 - 1);
                    
                    /* Para situarnos con el lector2, en la línea que hemos 
                     * encontrado nuestro rdf:Description, utilizamos un bucle
                     * while, en el que le ponemos como freno lineaDef-1,
                     * ayudándonos de contador.
                     * 
                     */
                    while (((s2 = lr2.readLine()) != null) && (contador < lineaDef-1)){contador++;}
                    
                    if(debugger) System.out.println("<" + nuevoIdentificador + " rdf:about=\"" + contenidoEtiqueta1  + "\">");
                    pw.println("<" + nuevoIdentificador + " rdf:about=\"" + contenidoEtiqueta1  + "\">");
                    
                    //while (s1.indexOf(cadenaBuscamos2) == -1) { s1 = lr1.readLine(); }
                    //lineaDef2 = lr1.getLineNumber();
                     
                    if(debugger) System.out.println("LINEA ACTUAL: "+lineaDef);
                    
                    /*
                     * Una vez hemos situado el cursor en la parte del documento
                     * que nosotros queríamos ( justo en la siguiente línea de
                     * <rdf:Description> ), comenzamos a buscar las etiquetas 
                     * con las propiedades del fichero de Origen, y las convertimos
                     * en las nuevas etiquetas, es decir: 
                     * Si tenemos un ejemplo como el siguiente:
                     * <rdf:Description>
                        <dc:title>Ibiza (Baleares). Fortificaciones. Planos. 1597</dc:title>
                        <dc:relation>Referencias: Mapas, planos y dibujos (Años 1503-1805). Volumen I : p. 590</dc:relation>
                        <dc:coverage>España-Baleares (Comunidad Autónoma)-Ibiza</dc:coverage>
                        <dc:title>Diseño de una parte del Castillo de Ibiza [Material cartográfico]</dc:title>
                        <dc:description>AGS. Guerra y Marina, Legajos, 00481. Incluido en carta de don Alonso de Zanoguera al rey, de 22 de enero de 1597.</dc:description>
                        <dc:description>Tinta negra. Con rotulación</dc:description>
                        <dc:description>Manuscrito sobre papel.</dc:description>
                        <dc:type>Mapas</dc:type>
                        <dc:language>spa</dc:language>
                        <dc:date>1597</dc:date>
                        <dc:creator>Saura, Antonio (m. 1634?)</dc:creator>
                        <dc:identifier>http://www.mcu.es/ccbae/es/consulta/registro.cmd?id=183413</dc:identifier>
                        <dc:format>image/jpeg</dc:format>
                        </rdf:Description>
                     * 
                     * El cursor comenzaría en <dc:title>, encontraría esa etiqueta,
                     * obtendría el contenido de la etiqueta ( en este caso 
                     * "Ibiza (Baleares). Fortificaciones. Planos. 1597" ) y 
                     * crearía la nueva etiqueta <Titulo> en el fichero de 
                     * destino, con el contenido, de la siguiente forma:
                     * <Titulo>Ibiza (Baleares). Fortificaciones. Planos. 1597</Titulo>
                     * 
                     * y así sucesivamente con todas las propiedades.
                     * 
                     * Para poder hacer esto, con cada línea que leemos, recorremos
                     * con un bucle for el array de propiedades, buscando a qué
                     * propiedad antigua hace alusión cada línea y cuál es la nueva.
                     * 
                     */
                    while (((s2 = lr2.readLine()) != null) && (s2.indexOf(cadenaBuscamos2) == -1)) {
                        if(debugger) System.out.println("CONTENIDO S2: "+s2);
                        lineaActual = lr2.getLineNumber();

                        // Bucle que busca las propiedades antiguas y las sustituye por las nuevas, en cada línea que se lee
                        for (int i = 0; i < numDePropiedades; i++) {
                            
                            // Cuando encuentra de qué propiedad se trata, obtiene el contenido
                            // del fichero antiguo, y lo agrega con el nuevo formato.
                            if (s2.indexOf(propiedades[i]) != -1) {
                                String contenidoEtiqueta = null;
                                int tamPropiedad = propiedades[i].length();
                                int tamS2 = s2.length();
                                contenidoEtiqueta = s2.substring(tamPropiedad + 2, tamS2 - tamPropiedad - 3);
                                String contenidoEtiquetaUTF8 = null;
                                
                                if(debugger) System.out.println("<" + propiedadesNuevas[i] + ">" + contenidoEtiqueta + "</" + propiedadesNuevas[i] + ">");
                                contenidoEtiquetaUTF8 = new String(contenidoEtiqueta.getBytes("UTF8"));
                                pw.println("<" + propiedadesNuevas[i] + ">" + contenidoEtiquetaUTF8 + "</" + propiedadesNuevas[i] + ">");
                               


                            }
                        }
                    }
                    if(debugger) System.out.println("</" + nuevoIdentificador + ">");
                    // Al finalizar con el contenido del objeto, cierra la instanciación del objeto de esa clase que hemos creado.
                    pw.println("</" + nuevoIdentificador + ">");
                }
                
            }
            // Cuando finaliza con la lectura , cierra el fichero de lectura y de escritura.
            lr1.close();
            fichero.close();
            JOptionPane.showMessageDialog(frame,"El fichero se convirtió correctamente! :)");
        } catch (Exception e) {
            e.printStackTrace();
            if(lineaActual == -1){
                JOptionPane.showMessageDialog(frame,"El fichero que has introducido es ilegible o no existe! :(","Error, problema con el fichero de entrada",    JOptionPane.ERROR_MESSAGE);
            }else{
                
                JOptionPane.showMessageDialog(frame,"Error de formato encontrado en línea "+lineaDef,"Error, problema con el fichero de entrada",    JOptionPane.ERROR_MESSAGE);

            }
            
           } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != lector1) {
                    lector1.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
}
