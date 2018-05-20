import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class Constants {
	public static final String CORE = "CORE";
	public static final String BORDER = "BORDER";
	public static final String NOISE = "NOISE";
	public static final String UNASSIGNED = "UNASSIGNED";
}

class Point {
	double x;
	double y;
	String label = Constants.UNASSIGNED;
	boolean processed = false;
	List<Point> neighborhood = new ArrayList<Point>();
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		String point = "{" + x + ", " + y + "} : " + label;
		return point;
	}
}

public class DBScan {

	private static final int MAX_NEIGHBOR = 3;
	private static final double DISTANCE = 3; 
	
	public static void populateData(List<Point> points) {
		points.add(new Point(3, 10));
		points.add(new Point(4, 6));
		points.add(new Point(9, 5));
		points.add(new Point(2, 8));
		points.add(new Point(8, 5));
		points.add(new Point(6, 6));
		points.add(new Point(3, 3));
		points.add(new Point(5, 7));
		points.add(new Point(6, 8));
	}
	
	public static double findDistance(Point point1, Point point2)
	{
		return Math.sqrt((Math.pow(point1.x - point2.x, 2)) + (Math.pow(point1.y - point2.y, 2)));
	}
	
	public static void labelData(List<Point> points) {
		//Pass 1, find neighborhood
		for(int i = 0; i < points.size(); i++)
		{
			Point currentPoint = points.get(i);
			for(int j = 0; j < points.size(); j++) {
				if(j == i)
					continue;
				Point consider = points.get(j);
				double distance = findDistance(currentPoint, consider);
				if(distance <= DISTANCE)
					currentPoint.neighborhood.add(consider);
			}
		}
		//Pass 2, label points
		for(int i = 0; i < points.size(); i++)
		{
			Point point = points.get(i);
			int neighborhoodSize = point.neighborhood.size();
			if(neighborhoodSize >= MAX_NEIGHBOR)
				point.label = Constants.CORE;
			else
			{
				point.label = Constants.NOISE;
				if(neighborhoodSize == 0)
					continue;
				else {
					for(Point neighbor : point.neighborhood)
					{
						if(neighbor.neighborhood.size() >= MAX_NEIGHBOR)
							point.label = Constants.BORDER;
					}
				}
			}
				
		}
	}
	
	private static void printPoints(List<Point> points) {
		for(Point point : points) {
			System.out.println(point);
		}
	}
	
	private static void extractCore(List<Point> points, List<Point> corePoints) {
		for(Point point : points)
		{
			if(point.label.equals(Constants.CORE))
				corePoints.add(point);
		}
	}
	
	private static void ProcessCluster(Point corePoint, Set<Point> cluster, List<Point> corePoints) {
		cluster.add(corePoint);
		List<Point> future = new ArrayList<Point>();
		for(Point point : corePoint.neighborhood) {
			if(!point.label.equals(Constants.CORE))
			{
				cluster.add(point);
			} else {
				corePoints.remove(point);
				future.add(point);
			}
		}
		corePoint.processed = true;
		for(Point point : future) {
			if(!point.processed)
				ProcessCluster(point, cluster, corePoints);
		}
	}
	
	public static void main(String[] args) {
		List<Point> points = new ArrayList<Point>();
		populateData(points);
		labelData(points);
		System.out.println("*************Labelled Data*************");
		printPoints(points);
		List<Point> corePoints = new ArrayList<Point>();
		extractCore(points, corePoints);
		List<Set<Point>> clusters = new ArrayList<Set<Point>>();
		while(!corePoints.isEmpty()) {
			Set<Point> cluster = new HashSet<Point>();
			Point corePoint = corePoints.remove(0);
			ProcessCluster(corePoint, cluster, corePoints);
			clusters.add(cluster);
		}
		System.out.println("*************Printing Clusters*************");
		int clusterid = 1;
		for(Set<Point> cluster : clusters) {
			System.out.println(clusterid);
			Iterator<Point> setIterator = cluster.iterator();
	        while(setIterator.hasNext()){
	            System.out.println(setIterator.next());
	        }
		}
			
	}
}
