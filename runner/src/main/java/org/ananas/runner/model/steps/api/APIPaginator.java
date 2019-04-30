package org.ananas.runner.model.steps.api;


import org.ananas.runner.misc.HttpClient;
import org.ananas.runner.model.errors.DatumaniaException;
import org.ananas.runner.model.errors.ExceptionHandler;
import org.ananas.runner.model.schema.JsonAutodetect;
import org.ananas.runner.model.schema.SchemaBasedRowConverter;
import org.ananas.runner.model.steps.commons.ErrorHandler;
import org.ananas.runner.model.steps.commons.json.JsonStringBasedFlattenerReader;
import org.ananas.runner.model.steps.commons.paginate.AbstractPaginator;
import org.ananas.runner.model.steps.commons.paginate.Paginator;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.values.Row;
import org.apache.commons.lang3.tuple.MutablePair;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class APIPaginator extends AbstractPaginator implements Paginator {
	String id;
	APIStepConfig config;


	public APIPaginator(String id, APIStepConfig config) {
		super(id, null);
		this.id = id;
		this.config = config;
	}


	@Override
	public Iterable<Row> iterateRows(Integer page, Integer pageSize) {
		MutablePair<Schema, Iterable<Row>> rows;
		try {
			rows = handle(this.config);
		} catch (IOException e) {
			throw new DatumaniaException(MutablePair.of(ExceptionHandler.ErrorCode.CONNECTION, e.getMessage()));
		}
		this.schema = rows.getLeft();
		return StreamSupport.stream(rows.getRight().spliterator(), false).collect(Collectors.toList());
	}


	public static MutablePair<Schema, Iterable<Row>> handle(APIStepConfig config) throws IOException {

		switch (config.method.toUpperCase()) {
			case "GET":
				return HttpClient.GET(config.url, config.headers, conn -> {
					String response = IOUtils.toString(conn.getInputStream());
					return convert(response);
				});
			case "POST":
				return HttpClient.POST(config.url, config.headers, config.body, conn -> {
					String response = IOUtils.toString(conn.getInputStream());
					return convert(response);
				});
			case "PUT":
				return HttpClient.PUT(config.url, config.headers, config.body, conn -> {
					String response = IOUtils.toString(conn.getInputStream());
					return convert(response);
				});
			default:
				throw new IllegalStateException(
						"Unsupported HTTP method '" + config.method + "'");
		}

	}

	public static MutablePair<Schema, Iterable<Row>> convert(String json) {
		Schema schema = JsonAutodetect.autodetectJson(json, false);

		JsonStringBasedFlattenerReader jsonReader =
				new JsonStringBasedFlattenerReader(SchemaBasedRowConverter.of(schema),
						new ErrorHandler());

		List<Row> rows = Arrays.asList(jsonReader.document2BeamRow(json));
		return MutablePair.of(schema, rows);
	}

}
