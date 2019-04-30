package org.ananas.runner.model.steps.sql;

import org.ananas.runner.model.steps.commons.AbstractStepRunner;
import org.ananas.runner.model.steps.commons.StepRunner;
import org.ananas.runner.model.steps.commons.StepType;
import org.ananas.runner.model.steps.sql.udf.*;
import org.apache.beam.sdk.extensions.sql.SqlTransform;

public class SQLTransformer extends AbstractStepRunner implements StepRunner {


	private static final long serialVersionUID = -5626161538797830330L;

	public SQLTransformer(String stepId, String statement, StepRunner previous) {
		super(StepType.Transformer);
		this.stepId = stepId;
		this.output = previous.getOutput().apply("sql transform",
				SqlTransform.query(statement)
						.registerUdf("falseIfNull", new NullableBooleanFn())
						.registerUdf("zeroIfNull", new NullableBigDecimalFn())
						.registerUdf("zeroIfNull", new NullableIntegerFn())
						.registerUdf("emptyIfNull", new NullableStringFn())
						.registerUdf("hash", new HashFn())
		);
	}

}
