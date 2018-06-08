import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

class GeoMapData {
	String geoCode;
	String geoName;
	double value;
	String[] formattedValue;
	int maxValueIndex;
	Boolean[] hasData;

	@Override
	public String toString() {
		return geoName + ", " + value;
	}
}

class OverTimeData {

	private final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;

	String geoName;
	Map<Date, Double> values = new HashMap<Date, Double>();

	public double calculateMean() {
		Collection<Double> values2 = values.values();
		OptionalDouble average = values2.parallelStream().mapToDouble(a -> a).average();
		double toReturn = average.getAsDouble();
		System.out.println("Average : " + toReturn);
		return toReturn;
	}

	public double calculateMax() {
		Collection<Double> values2 = values.values();
		OptionalDouble max = values2.parallelStream().mapToDouble(a -> a).max();
		double toReturn = max.getAsDouble();
		System.out.println("Max : " + toReturn);
		return toReturn;
	}

	public double calculateForEvent() {
		List<Date> dateList = new ArrayList<Date>(values.keySet());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		double toReturn = 0;
		try {
			Date eventDate = format.parse(ExtractPopularity.EventDate);
			List<Date> closeDates = dateList.parallelStream()
					.filter(d -> Math.abs(d.getTime() - eventDate.getTime()) < MILLIS_PER_DAY)
					.collect(Collectors.toList());
			for (Date date : closeDates) {
				toReturn += values.get(date);
			}
			toReturn /= closeDates.size();
		} catch (ParseException e) {
			return calculateMean();
		}
		return toReturn;
	}

	public void addValue(Date date, Double value) {
		values.put(date, value);
	}

	@Override
	public String toString() {
		System.out.println("Processing : " + geoName);
		return geoName + ", " + (calculateForEvent() - calculateMean());
	}
}

public class ExtractPopularity {

	public static final String EventDate = "2018-02-04";

	public static final String StartDate = "2017-08-01";

	public static final String EndDate = "2018-02-28";

	private static final String Command = "node";

	private static final String WorkDirectory = "/home/abhishek/repositories/cis553/google-trends/";

	private static final String Regional = WorkDirectory + "regional.js";

	private static final String OverTime = WorkDirectory + "overtime.js";

	private static final Map<String, String> States;
	static {
		States = new HashMap<String, String>();
		States.put("Alabama", "US-AL");
		States.put("Alaska", "US-AK");
		States.put("Arizona", "US-AZ");
		States.put("Arkansas", "US-AR");
		States.put("California", "US-CA");
		States.put("Colorado", "US-CO");
		States.put("Connecticut", "US-CT");
		States.put("Delaware", "US-DE");
		States.put("Florida", "US-FL");
		States.put("Georgia", "US-GA");
		States.put("Hawaii", "US-HI");
		States.put("Idaho", "US-ID");
		States.put("Illinois", "US-IL");
		States.put("Indiana", "US-IN");
		States.put("Iowa", "US-IA");
		States.put("Kansas", "US-KS");
		States.put("Kentucky", "US-KY");
		States.put("Louisiana", "US-LA");
		States.put("Maine", "US-ME");
		States.put("Maryland", "US-MD");
		States.put("Massachusetts", "US-MA");
		States.put("Michigan", "US-MI");
		States.put("Minnesota", "US-MN");
		States.put("Mississippi", "US-MS");
		States.put("Missouri", "US-MO");
		States.put("Montana", "US-MT");
		States.put("Nebraska", "US-NE");
		States.put("Nevada", "US-NV");
		States.put("New Hampshire", "US-NH");
		States.put("New Jersey", "US-NJ");
		States.put("New Mexico", "US-NM");
		States.put("New York", "US-NY");
		States.put("North Carolina", "US-NC");
		States.put("North Dakota", "US-ND");
		States.put("Ohio", "US-OH");
		States.put("Oklahoma", "US-OK");
		States.put("Oregon", "US-OR");
		States.put("Pennsylvania", "US-PA");
		States.put("Rhode Island", "US-RI");
		States.put("South Carolina", "US-SC");
		States.put("South Dakota", "US-SD");
		States.put("Tennessee", "US-TN");
		States.put("Texas", "US-TX");
		States.put("Utah", "US-UT");
		States.put("Vermont", "US-VT");
		States.put("Virginia", "US-VA");
		States.put("Washington", "US-WA");
		States.put("West Virginia", "US-WV");
		States.put("Wisconsin", "US-WI");
		States.put("Wyoming", "US-WY");
	}

	private static String ExecuteProcess(String[] command) {
		System.out.print("Executing with : ");
		for (String arg : command) {
			System.out.print(arg + " ");
		}
		System.out.print("\n");
		String json = null;
		Process process = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ProcessBuilder processBuilder;
			processBuilder = new ProcessBuilder(command);
			process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			setUpStreamGobbler(inputStream, new PrintStream(output, true, "UTF-8"));

			InputStream errorStream = process.getErrorStream();
			setUpStreamGobbler(errorStream, System.err);

			process.waitFor();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		json = new String(output.toByteArray(), StandardCharsets.UTF_8);
		return json;
	}

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Incorrect number of arguments, usage : java <analysis> <brand>");
			System.exit(1);
		}
		int analysis = Integer.parseInt(args[0]);
		String brand = args[1];

		List<String> RegionalCommand = new ArrayList<String>(Arrays.asList(Command, Regional, brand));
		// Analysis done 12 month time
		List<String> OvertimeCommand = new ArrayList<String>(Arrays.asList(Command, OverTime, brand));

		if (analysis == 1) {
			String json = ExecuteProcess(RegionalCommand.toArray(new String[RegionalCommand.size()]));
			List<GeoMapData> geoDataList = new ArrayList<GeoMapData>();
			ParseForGeoData(geoDataList, json);
			WriteCSV(geoDataList);
		} else {
			List<OverTimeData> overTimeDataList = new ArrayList<OverTimeData>();
			for (String state : States.keySet()) {
				List<String> specificCommand = new ArrayList<String>();
				specificCommand.addAll(OvertimeCommand);
				specificCommand.add(States.get(state));
				// TODO : start and end dates
				specificCommand.add(StartDate);
				specificCommand.add(EndDate);
				specificCommand.add(States.get(state));
				String json = ExecuteProcess(specificCommand.toArray(new String[specificCommand.size()]));
				ParseForOverTimeData(state, overTimeDataList, json);
			}
			WriteCSV(overTimeDataList);
		}
	}

	private static void ParseForOverTimeData(String region, List<OverTimeData> overTimeDataList, String json) {
		SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
		JsonParser parser = new JsonParser();
		try {
			JsonElement element = parser.parse(json);
			if (element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				JsonArray overTimeArray = ((JsonObject) object.get("default")).get("timelineData").getAsJsonArray();
				OverTimeData data = new OverTimeData();
				for (JsonElement overTimeElement : overTimeArray) {
					if (!overTimeElement.isJsonObject())
						return;
					JsonObject overTimeObject = overTimeElement.getAsJsonObject();
					data.geoName = region.toLowerCase();
					String dateString = overTimeObject.get("formattedTime").getAsString();
					Date date = null;
					try {
						date = format.parse(dateString);
					} catch (ParseException e) {
					}
					double value = overTimeObject.get("value").getAsDouble();
					data.addValue(date, value);
				}
				overTimeDataList.add(data);
			}
		} catch (JsonSyntaxException e) {
			System.err.println("Error occured : " + e.getMessage());
			System.err.println(json);
		}
	}

	private static void ParseForGeoData(List<GeoMapData> geoDataList, String json) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			JsonArray geoMapArray = ((JsonObject) object.get("default")).get("geoMapData").getAsJsonArray();
			for (JsonElement geoMapElement : geoMapArray) {
				if (!geoMapElement.isJsonObject())
					return;
				JsonObject geoMapObject = geoMapElement.getAsJsonObject();
				GeoMapData data = new GeoMapData();
				data.geoCode = geoMapObject.get("geoCode").getAsString();
				data.geoName = geoMapObject.get("geoName").getAsString().toLowerCase();
				data.value = geoMapObject.get("value").getAsDouble();
				geoDataList.add(data);
			}
		}
	}

	private static <T> void WriteCSV(List<T> dataList) {
		try {
			File file = new File(WorkDirectory + "dataframe.csv");
			FileWriter writer = new FileWriter(file);
			BufferedWriter buffWriter = new BufferedWriter(writer);
			// Write Header
			buffWriter.write("region, value" + "\n");
			// Write Data
			for (T data : dataList) {
				buffWriter.write(data.toString() + "\n");
			}
			buffWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setUpStreamGobbler(final InputStream is, final PrintStream ps) {
		final InputStreamReader streamReader = new InputStreamReader(is);
		new Thread(new Runnable() {
			public void run() {
				BufferedReader br = new BufferedReader(streamReader);
				String line = null;
				try {
					while ((line = br.readLine()) != null) {
						ps.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
