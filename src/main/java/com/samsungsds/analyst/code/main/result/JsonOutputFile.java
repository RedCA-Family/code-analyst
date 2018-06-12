package com.samsungsds.analyst.code.main.result;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.samsungsds.analyst.code.findbugs.FindBugsResult;
import com.samsungsds.analyst.code.jdepend.JDependResult;
import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.pmd.ComplexityResult;
import com.samsungsds.analyst.code.pmd.PmdResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.sonar.SonarJavaResult;
import com.samsungsds.analyst.code.sonar.WebResourceResult;
import com.samsungsds.analyst.code.unusedcode.UnusedCodeResult;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

public class JsonOutputFile extends AbstractOutputFile {
	private static final Logger LOGGER = LogManager.getLogger(JsonOutputFile.class);

	private MeasuredResult result;

	@Override
	protected void open(MeasuredResult result) {
		this.result = result;
	}

	@Override
	protected void writeSeparator() {
		// no-op
	}

	@Override
	protected void writeAcyclicDependencies(List<String> acyclicDependencyList) {
		// no-op
	}

	@Override
	protected void writeWebResource(List<WebResourceResult> webResourceList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-webresource.json";

			writeListToJson(webResourceList, "webResourceList", jsonFile);
		}
	}

	@Override
	protected void writeFindBugs(List<FindBugsResult> findBugsList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-findbugs.json";

			writeListToJson(findBugsList, "findBugsList", jsonFile);
		}
	}

	@Override
	protected void writeFindSecBugs(List<FindBugsResult> findSecBugsList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-findsecbugs.json";

			writeListToJson(findSecBugsList, "findSecBugsList", jsonFile);
		}
	}

	@Override
	protected void writePmd(List<PmdResult> pmdList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-pmd.json";

			writeListToJson(pmdList, "pmdList", jsonFile);
		}
	}

	@Override
	protected void writeSonarJava(List<SonarJavaResult> sonarJavaList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-sonarjava.json";

			writeListToJson(sonarJavaList, "sonarJavaList", jsonFile);
		}
	}

	@Override
	protected void writeComplexity(List<ComplexityResult> complexityList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-complexity.json";

			writeListToJson(complexityList, "complexityList", jsonFile);
		}
	}

	@Override
	protected void writeDuplication(List<DuplicationResult> dulicationList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-duplication.json";

			writeListToJson(dulicationList, "duplicationList", jsonFile);
		}
	}

	@Override
	protected void writeUnusedCode(List<UnusedCodeResult> unusedCodeList) {
		if (result.isSeperatedOutput()) {
			String jsonFile = IOAndFileUtils.getFilenameWithoutExt(result.getOutputFile()) + "-unusedCode.json";

			writeListToJson(unusedCodeList, "unusedList", jsonFile);
		}

	}

	@Override
	protected void writeSummary(MeasuredResult result) {
		// no-op
	}

	@Override
	protected void writeProjectInfo(CliParser cli, MeasuredResult result) {
		// no-op
	}

	@Override
	protected void writeFilePathList(List<String> filePathList) {
		// no-op
	}

	@Override
	protected void close(PrintWriter writer) {
		if (result.isSeperatedOutput()) {
			result.clearSeperatedList();
		}

		final GsonBuilder builder = new GsonBuilder();

		Type jdependListType = new TypeToken<List<JDependResult>>() {
		}.getType();
		JsonSerializer<List<JDependResult>> serializer = new JsonSerializer<List<JDependResult>>() {
			@Override
			public JsonElement serialize(List<JDependResult> src, Type typeOfSrc, JsonSerializationContext context) {
				JsonArray json = new JsonArray();

				for (JDependResult jdepend : src) {
					JsonElement element = new JsonPrimitive(jdepend.getAcyclicDependencies());
					json.add(element);
				}

				return json;
			}
		};

		builder.registerTypeAdapter(jdependListType, serializer);

		builder.excludeFieldsWithoutExposeAnnotation();
		final Gson gson = builder.create();

		String json = gson.toJson(result);

		writer.print(json);
	}

	private void writeListToJson(List<?> list, String name, String jsonFile) {
		final GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithoutExposeAnnotation();
		final Gson gson = builder.create();

		try (PrintWriter jsonWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile))))) {

			// String json = gson.toJson(list);
			JsonArray jsonArray = gson.toJsonTree(list).getAsJsonArray();
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(name, jsonArray);

			jsonWriter.print(jsonObject);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Result seperated file saved : {}", jsonFile);
	}

}
