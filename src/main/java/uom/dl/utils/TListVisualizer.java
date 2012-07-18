package uom.dl.utils;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.TTCCLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.TList;

public class TListVisualizer {
	private static final Random RANDOM = new Random();
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
		//String clashNodeID = "clash_" + new Object().hashCode();
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
		if (list.containsClash()) {
			String clashNodeID = "clash_" + cName;
			s.append("\t \"" + cName + "\" -> \"" + clashNodeID + "\";\n");
			s.append("\t \"" + clashNodeID + "\" [label=clash style=filled];\n");
			//s.append("\t clash [style=filled];\n");
		}
			
		return s.toString();
	}

	private static <T extends Assertion> String toDotFormatInner(TreeVisualizer list) {
		//String clashNodeID = "clash_" + new Object().hashCode();
		StringBuffer s = new StringBuffer();
		
		List<TreeVisualizer> frontier = new ArrayList<>();
		frontier.add(list);
		while (!frontier.isEmpty()) {
			list = frontier.remove(0);
			if (list.getValue() == null) { //clash
				continue;				
			}
			String pName = list.getValue().toString() + "_" + list.hashCode();
			s.append("\t \"" + pName + "\" [label=\"" + list.getValue().toString() + "\"];\n");
			List<TreeVisualizer> children = list.getChildren();
			frontier.addAll(list.getChildren());
			for (TreeVisualizer child : children) {
				String cName;
				if (child.getValue() == null) {
					//String pName = "clash_" + list.hashCode();
					cName = "clash_" + list.hashCode();
					s.append("\t \"" + cName + "\" [label=clash style=filled];\n");
				}
				else {
					cName = child.getValue().toString() + "_" + child.hashCode();
				}
				s.append("\t \"" + pName + "\" -> ");
				s.append("\"" + cName + "\";\n");				
			}
		}
//		if (list.containsClash()) {
//			String clashNodeID = "clash_" + cName;
//			s.append("\t \"" + cName + "\" -> \"" + clashNodeID + "\";\n");
//			s.append("\t \"" + clashNodeID + "\" [label=clash style=filled];\n");
//			//s.append("\t clash [style=filled];\n");
//		}
			
		return s.toString();
	}
	
	public static <T extends Assertion> boolean showGraph(TList<T> list, boolean isSatisfiable) {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		int id = RANDOM.nextInt();
		String dotFile = "graph" + id + ".dot";
		String pngFile = "graph" + id + ".png";
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
		//for (TList<T> model : models)
		//	s.append(toDotFormatInner(model));
		TreeVisualizer tree = TreeVisualizer.buildModelTree(models);
		s.append(toDotFormatInner(tree));
		
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
