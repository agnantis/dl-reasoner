package uom.dl.utils;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.TList;

public class TListVisualizer {
	private static final Logger log = LoggerFactory
			.getLogger(TListVisualizer.class);

	/*
	public TListVisualizer(TList<T> tree) {
		this.list = tree;
	}*/

	public static <T extends Assertion> String toDotFormat(TList<T> list, boolean isSatisfiable) {
		StringBuffer s = new StringBuffer();
		s.append("digraph G {\n");
		s.append("\tlabelloc = t;\n");
		s.append("\tlabel=\"The concept IS" + (isSatisfiable ? " NOT" : "") + " satisfiable\";\n");
		s.append(toDotFormatInner(list));
		s.append("}\n");
		System.out.println(s);
		return s.toString();
	}

	//prefix = "0" -> root
	//children of root -> 1.0, 1.1...
	private static <T extends Assertion> String toDotFormatInner(TList<T> list) {
		StringBuffer s = new StringBuffer();
		int counter = 0;
		
		TList<T> current = list;
		String cName = current.getValue().toString() + "_" + counter;
		s.append("\t \"" + cName + "\" [label=\"" + current.getValue() + "\"];\n");
		while (current.hasNext()) {
			Assertion nextValue = current.getNext().getValue();
			++counter;
			String nextName = nextValue.toString() + "_" + counter;
			s.append("\t \"" + nextName + "\" [label=\"" + nextValue + "\"];\n");
			s.append("\t \"" + cName + "\" -> ");
			s.append("\"" + nextName + "\";\n");
			current = current.getNext();
			cName = nextName;
		}
		s.append("\t \"" + cName + "\"");
		if (current.isCurrentExpandable()) {
			s.append(";\n");
		} else {
			s.append(" [style=filled];\n");
		}
			
		return s.toString();
	}

	public static <T extends Assertion> boolean showGraph(TList<T> list, boolean isSatisfiable) {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		String dotFile = "graph.dot";
		String pngFile = "graph.png";
		Path inputPath = Paths.get(tmpFolder, dotFile);
		Path outputPath = Paths.get(tmpFolder, pngFile);
		boolean fileCreated = saveGraph(list, inputPath, isSatisfiable);
		if (fileCreated) {
			try {
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec("dot -Tpng " + inputPath.toString() + " -o " + outputPath.toString());
				p.waitFor();
				if (p.exitValue() == 0){
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(outputPath.toFile());
					} else {
						log.error("Can't open with default application. File: " + outputPath.toString());
					}
					//gnome specific
					//p = rt.exec("gnome-open " + outputPath.toString());
					//p.waitFor();
				}
			} catch (Exception exc) {
				log.error("Exception: " + exc);
				log.error("Please install 'graphviz' program in order to be able to view or save the graph");
				log.error("Download 'graphviz' from here: http://www.graphviz.org/Download..php");
			}
		}
		return true;
	}
	
	public static <T extends Assertion> boolean showGraph(List<TList<T>> list, boolean isSatisfiable) {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		String dotFile = "graph.dot";
		String pngFile = "graph.png";
		Path inputPath = Paths.get(tmpFolder, dotFile);
		Path outputPath = Paths.get(tmpFolder, pngFile);
		boolean fileCreated = saveGraph(list, inputPath, isSatisfiable);
		if (fileCreated) {
			try {
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec("dot -Tpng " + inputPath.toString() + " -o " + outputPath.toString());
				p.waitFor();
				if (p.exitValue() == 0){
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(outputPath.toFile());
					} else {
						log.error("Can't open with default application. File: " + outputPath.toString());
					}
					//gnome specific
					//p = rt.exec("gnome-open " + outputPath.toString());
					//p.waitFor();
				}
			} catch (Exception exc) {
				log.error("Exception: " + exc);
				log.error("Please install 'graphviz' program in order to be able to view or save the graph");
				log.error("Download 'graphviz' from here: http://www.graphviz.org/Download..php");
			}
		}
		return true;
	}

	public static <T extends Assertion> boolean saveGraph(TList<T> list, Path path, boolean isSatisfiable) {
		String graph = toDotFormat(list, isSatisfiable);
		// cat tableaux.dot | dot -Tpng > graph.png
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				Charset.defaultCharset())) {
			writer.write(graph, 0, graph.length());
		} catch (IOException x) {
			log.error("IOException: %s%n", x);
			log.error("Please install 'dot' program in order to be able to view or save the graph");
			return false;
		}
		return true;
	}
	
	public static <T extends Assertion> boolean saveGraph(List<TList<T>> lists, Path path, boolean isSatisfiable) {
		String graph = toDotFormat(lists, isSatisfiable);
		// cat tableaux.dot | dot -Tpng > graph.png
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				Charset.defaultCharset())) {
			writer.write(graph, 0, graph.length());
		} catch (IOException x) {
			log.error("IOException: %s%n", x);
			log.error("Please install 'dot' program in order to be able to view or save the graph");
			return false;
		}
		return true;
	}
	
	public static <T extends Assertion> String toDotFormat(List<TList<T>> models, boolean isSatisfiable) {
		StringBuffer s = new StringBuffer();
		for (TList<T> model : models)
			s.append(toDotFormatInner(model));			
		
		String satLabel = isSatisfiable ? "IS" : "IS NOT";
		String[] lines = s.toString().split("\n");
		Set<String> uniqueLines = new HashSet<>(Arrays.asList(lines));
		s = new StringBuffer();
		s.append("digraph G_all {\n");
		s.append("\tlabelloc = t;\n");
		s.append("\tlabel=\"The concept " + satLabel  + " satisfiable\\n\\n\";\n");
		for (String line : uniqueLines)
			s.append(line + "\n");
		
		s.append("}\n");
		System.out.println(s);
		return s.toString();		
	}
}
