package uom.dl.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.NotConcept;
import uom.dl.reasoner.TTree;

public class TreeVisualizer {
	private static final Logger log = LoggerFactory
			.getLogger(TreeVisualizer.class);

	private TTree tree;

	public TreeVisualizer(TTree tree) {
		this.tree = tree;
	}

	public String toDotFormat() {
		return toDotFormat("");
	}

	//prefix = "0" -> root
	//children of root -> 1.0, 1.1...
	private String toDotFormat(String prefix) {
		boolean isRoot = (prefix.isEmpty() || "0".equals(prefix));
		StringBuffer s = new StringBuffer();
		if (isRoot) {
			s.append("digraph G {\n");
			prefix = "0";
		}
		//label accordingly
		s.append("\t \"" + prefix + ":" + tree.getValue() + "\"");
		if (tree.isExpandable()) {
			s.append(" [label=\"" + tree.getValue() + "\"];\n");
		} else {
			s.append(" [style=filled, label=\"" + tree.getValue() + "\"];\n");
		}
		if (!tree.getChildren().isEmpty()) {
			int counter = 0;
			for (TTree child : tree.getChildren()) {
				s.append("\t \"" + prefix + ":" + tree.getValue() + "\" -> ");
				String pref = prefix + "." + counter;
				s.append("\"" + pref + ":" + child.getValue() + "\"\n");
				s.append(new TreeVisualizer(child).toDotFormat(pref));
				++counter;
			}
		}
		if (isRoot) {
			s.append("}\n");
		}
		return s.toString();
	}

	public boolean showGraph() {
		String pathName = "/tmp/graph.dot";
		Path path = Paths.get(pathName);
		boolean fileCreated = saveGraph(path);
		if (fileCreated) {
			try {
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec("dot -Tpng " + pathName + " -o /tmp/graph.png");
				p.waitFor();
				if (p.exitValue() == 0){
					p = rt.exec("gnome-open /tmp/graph.png");
					p.waitFor();
				}
			} catch (Exception exc) {
				log.error("Exception: " + exc);
				log.error("Please install 'dot' program in order to be able to view or save the graph");
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
