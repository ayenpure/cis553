package assignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ConditionalFPNode {
	Set<Integer> path = new HashSet<Integer>();
	Integer count = 0;

	public void addNode(Integer node) {
		path.add(node);
	}
}

class FPNode {

	public FPNode(int i) {
		this.item = i;
		this.count = 1;
		this.parent = null;
		this.next = null;
		children = new HashMap<Integer, FPNode>();
	}

	public void addList(List<Integer> txItems, Integer index) {
		if (index >= txItems.size())
			return;
		Integer key = txItems.get(index);
		if (children.containsKey(key)) {
			FPNode child = children.get(key);
			if (child.item != key) {
				System.out.println("Something went horribly wrong!");
				System.exit(1);
			}
			child.count++;
			child.addList(txItems, ++index);
		} else {
			FPNode child = new FPNode(key);
			child.parent = this;
			children.put(key, child);
			if (NodeStore.containsKey(key)) {
				FPNode previous = NodeStore.get(key);
				while (previous.next != null)
					previous = previous.next;
				previous.next = child;
			} else {
				NodeStore.put(key, child);
			}
		}
	}

	public static void findAssociationRules(List<Integer> forAssociationMining, Integer supportCount) {
		HashMap<Integer, List<ConditionalFPNode>> itemToPaths = new HashMap<Integer, List<ConditionalFPNode>>();
		for (Integer item : forAssociationMining) {
			FPNode node = NodeStore.get(item);
			List<ConditionalFPNode> paths = new ArrayList<ConditionalFPNode>();
			do {
				FPNode currentNode = node.parent;
				ConditionalFPNode path = new ConditionalFPNode();
				while (currentNode.item != -1) {
					path.addNode(currentNode.item);
					currentNode = currentNode.parent;
				}
				path.count = node.count;
				if (!path.path.isEmpty())
					paths.add(path);
				node = node.next;
			} while (node != null);
			itemToPaths.put(item, paths);
		}

		/* For each item compare the paths for association rules */
		HashMap<Integer, ConditionalFPNode> conditionalBase = new HashMap<Integer, ConditionalFPNode>();
		for (Entry<Integer, List<ConditionalFPNode>> entry : itemToPaths.entrySet()) {
			Integer item = entry.getKey();
			List<ConditionalFPNode> paths = entry.getValue();
			Collections.sort(paths, new Comparator<ConditionalFPNode>() {
				@Override
				public int compare(ConditionalFPNode o1, ConditionalFPNode o2) {
					return o2.path.size() - o1.path.size();
				}
			});
			Set<Integer> association = new HashSet<Integer>();
			if (paths.isEmpty())
				continue;
			association.addAll(paths.get(0).path);
			int occurrence = paths.get(0).count;
			for (int i = 1; i < paths.size(); i++) {
				association.retainAll(paths.get(i).path);
				occurrence += paths.get(i).count;
			}
			if (association.size() == 0 /*|| occurrence < supportCount*/)
				continue;
			ConditionalFPNode node = new ConditionalFPNode();
			node.path = association;
			node.count = occurrence;
			conditionalBase.put(item, node);
		}
		System.out.println("\n\n *** Condiitonal Pattern Base ***");
		if (conditionalBase.isEmpty())
			System.out.println("--- Could not generate the conditional base with the given support ---");
		for (Entry<Integer, ConditionalFPNode> entry : conditionalBase.entrySet()) {
			System.out.print(entry.getKey() + " : ");
			entry.getValue().path.forEach(s -> System.out.print(s + " "));
			System.out.println();
		}
	}

	private int item, count;
	private FPNode next, parent;
	private Map<Integer, FPNode> children;
	private static Map<Integer, FPNode> NodeStore = new HashMap<Integer, FPNode>();
}

public class Association {

	private static final String filepath = "/home/abhishek/Downloads/FoodMart.csv";
	private static final Double support = 0.001;
	private static final Double confidence = 0.7;

	public static void main(String[] args) {
		File input = new File(filepath);
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(input);
		} catch (FileNotFoundException e) {
			System.err.println("Error occured while reading the file");
			System.exit(1);
		}
		BufferedReader reader = new BufferedReader(fileReader);

		List<List<Integer>> transactions;
		transactions = new ArrayList<List<Integer>>();

		List<String> items = new ArrayList<String>();

		try {
			String line;
			boolean first = true;
			while ((line = reader.readLine()) != null) {
				if (first) {
					first = false;
					items.addAll(Arrays.asList(line.split(",")));
					continue;
				}
				String[] split = line.split(",");
				List<Integer> itemset = Arrays.asList(split).parallelStream().map(s -> Integer.parseInt(s))
						.collect(Collectors.toList());
				transactions.add(itemset);
			}
		} catch (IOException e) {
			System.err.println("Error occured while reading the file");
			System.exit(1);
		}

		Integer numberOfItems = items.size();
		Integer numberOfTransactions = transactions.size();
		Integer supportCount = (int) (numberOfTransactions * support);

		List<Integer> counts = IntStream.range(0, numberOfItems)
				.mapToObj(i -> transactions.stream().mapToInt(list -> list.get(i)).sum()).collect(Collectors.toList());

		List<Integer> frequentItems = IntStream.range(0, numberOfItems).filter(i -> counts.get(i) >= supportCount)
				.mapToObj(i -> new Integer(i)).collect(Collectors.toList());

		frequentItems.sort(Comparator.comparing(index -> counts.get(index)));
		Collections.reverse(frequentItems);
		System.out.println("*** Number of identified frequent items is " + frequentItems.size() + " ***");

		for (Integer item : frequentItems) {
			System.out.println("Item " + item + " : " + items.get(item) + ", with " + counts.get(item) + " entries");
		}

		/* Generate sorted lists for transactions based on the above frequent items */
		List<List<Integer>> frequentTxItems = transactions.parallelStream().map(transaction -> frequentItems.stream()
				.filter(index -> transaction.get(index) == 1).collect(Collectors.toList()))
				.collect(Collectors.toList());
		int i = 1;

		/*
		 * Slowest part of the algorithm Building the FP-Tree
		 */
		FPNode root = new FPNode(-1);
		for (List<Integer> txItems : frequentTxItems) {
			if (txItems.size() == 0)
				continue;
			root.addList(txItems, 0);
		}

		/*
		 * Here root will have the complete FP tree
		 */
		List<Integer> forAssociationMining = (List<Integer>) ((ArrayList<Integer>) frequentItems).clone();
		Collections.reverse(forAssociationMining);
		FPNode.findAssociationRules(forAssociationMining, (int) (supportCount));
	}
}
