package uom.dl.utils;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.TList;
import uom.dl.reasoner.TTree;

public class TListVisualizer<T extends Assertion> {
	private static final Logger log = LoggerFactory
			.getLogger(TListVisualizer.class);

	private TList<T> list;

	public TListVisualizer(TList<T> tree) {
		this.list = tree;
	}

	public String toDotFormat() {
		return toDotFormat(true);
	}

	//prefix = "0" -> root
	//children of root -> 1.0, 1.1...
	private String toDotFormat(boolean isRoot) {
		StringBuffer s = new StringBuffer();
		if (isRoot) {
			s.append("digraph G {\n");
		}
		
		TList<T> current = list;
		while (current.hasNext()) {
			s.append("\t \"" + current.getValue() + "\" -> ");
			s.append("\"" + current.getNext().getValue() + "\";\n");
			current = current.getNext();
		}
		s.append("\t \"" + current.getValue() + "\"");
		if (current.isCurrentExpandable()) {
			s.append(";\n");
		} else {
			s.append(" [style=filled];\n");
		}
			
		if (isRoot) {
			s.append("}\n");
		}
		System.out.println(s);
		return s.toString();
	}

	public boolean showGraph() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		String dotFile = "graph.dot";
		String pngFile = "graph.png";
		Path inputPath = Paths.get(tmpFolder, dotFile);
		Path outputPath = Paths.get(tmpFolder, pngFile);
		boolean fileCreated = saveGraph(inputPath);
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

	public boolean saveGraph(Path path) {
		String graph = toDotFormat();
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
}
