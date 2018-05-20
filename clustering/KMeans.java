import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

class Element {
	double x;
	double y;

	Element(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return new String("(" + this.x + ", " + this.y + ")");	
	}
	
	private static Map<Integer, Element> centroids = null;

	protected double calculateDistance(Element e) {
		return (double) Math.sqrt((Math.pow(this.x - e.x, 2)) + (Math.pow(this.y - e.y, 2)));
	}

	public static void setCentroids(Map<Integer, Element> centroids) {
		Element.centroids = centroids;
	}

	public Integer getCentroid() {
		if (centroids == null)
			throw new RuntimeException("Controids not provided for clustering");
		double distance = Double.MAX_VALUE;
		Integer centroid = -1;
		for (Entry<Integer, Element> entry : centroids.entrySet()) {
			double currentDistance = calculateDistance(entry.getValue());
			if (currentDistance < distance) {
				distance = currentDistance;
				centroid = entry.getKey();
			}
		}
		return centroid;
	}
}

public class KMeans {

	public KMeans(){
		elements = new ArrayList<Element>();
		centroids = new HashMap<Integer, Element>();
		clusters = new HashMap<Integer, List<Element>>();
	}
	
	Map<Integer, Element> centroids;
	List<Element> elements;
	Map<Integer, List<Element>> clusters;

	public List<Element> getElements() {
		return elements;
	}

	public void populateElements() {
		elements.add(new Element(3, 10));
		elements.add(new Element(4, 6));
		elements.add(new Element(9, 5));
		elements.add(new Element(2, 8));
		elements.add(new Element(8, 5));
		elements.add(new Element(6, 6));
		elements.add(new Element(3, 3));
		elements.add(new Element(5, 7));
		elements.add(new Element(6, 8));
	}

	private void printCentroids() {
		System.out.println("Centroids");
		for(Entry<Integer, Element> entry : centroids.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue().toString());
		}
	}

	private void printClusters() {
		System.out.println("Clusters");
		for(Entry<Integer, List<Element>> cluster : clusters.entrySet())
		{
			System.out.println(cluster.getKey());
			for(Element element : cluster.getValue()) {
				System.out.println(element.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		KMeans kmeans = new KMeans();
		kmeans.populateElements();
		kmeans.centroids.put(1, kmeans.elements.get(0));
		kmeans.centroids.put(2, kmeans.elements.get(3));
		kmeans.centroids.put(3, kmeans.elements.get(6));
		for (int i = 0; i < 10; i++) {
			
			System.out.println("***********Before***********");
			kmeans.printCentroids();
			
			Element.setCentroids(kmeans.centroids);
			
			kmeans.clusters = kmeans.getElements().stream().collect(Collectors.groupingBy(e -> e.getCentroid()));
			
			kmeans.printClusters();
			
			for (Entry<Integer, List<Element>> entry : kmeans.clusters.entrySet()) {
				Integer key = entry.getKey();
				Element value = entry.getValue().stream().reduce(new Element(0, 0), new BinaryOperator<Element>() {
					public Element apply(Element arg0, Element arg1) {
						return new Element(arg0.x + arg1.x, arg0.y + arg1.y);
					}
				});
				Integer numElements = entry.getValue().size();
				value.x /= numElements;
				value.y /= numElements;
				kmeans.centroids.put(key, value);
			}
			System.out.println("***********After***********");
			kmeans.printCentroids();
		}
	}
}
