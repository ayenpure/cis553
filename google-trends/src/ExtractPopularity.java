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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class GeoMapData {
	String geoCode;
	String geoName;
	double value;
	String[] formattedValue;
	int maxValueIndex;
	Boolean[] hasData;

	@Override
	public String toString() {
		return geoName + " ( " + geoCode + " ) : " + value;
	}
}

public class ExtractPopularity {

	private static final String Command = "node";

	private static final String WorkDirectory = "/home/abhishek/repositories/cis553/google-trends/";

	private static final String Regional = WorkDirectory + "regional.js";

	private static final String OverTime = WorkDirectory + "trial.js";

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Incorrect number of arguments, usage : java <trend> <brand>" );
			System.exit(1);
		}
		String brand = args[0];
		String json = null;
		Process process = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		String[] RegionalCommand = { Command, Regional, brand };

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(RegionalCommand);
			process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			setUpStreamGobbler(inputStream, new PrintStream(output, true, "UTF-8"));

			InputStream errorStream = process.getErrorStream();
			setUpStreamGobbler(errorStream, System.err);

			System.out.println("never returns");
			process.waitFor();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		json = new String(output.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("Finished execution the program");
		System.out.println("String : " + json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		List<GeoMapData> geoDataList = new ArrayList<GeoMapData>();
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
		WriteCSV(geoDataList);
	}

	private static void WriteCSV(List<GeoMapData> geoDataList) {
		try {
			File file = new File(WorkDirectory + "dataframe.csv");
			FileWriter writer = new FileWriter(file);
			BufferedWriter buffWriter = new BufferedWriter(writer);
			// Write Header
			buffWriter.write("region, value" + "\n");
			// Write Data
			for (GeoMapData data : geoDataList) {
				System.out.println("Writing  : " + data.geoName + ", " + data.value);
				buffWriter.write(data.geoName + ", " + data.value + "\n");
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
