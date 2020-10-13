package p1;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

public class flatmap {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		ParameterTool params = ParameterTool.fromArgs(args);

		env.getConfig().setGlobalJobParameters(params);

		DataSet<String> text = env.readTextFile(params.get("flatmap"));// text = [Nipun Noman...]

		DataSet<String> filtered = text.filter(new FilterFunction<String>()// filtered = dataset of [Nipun Noman ...]

		{
			public boolean filter(String value) 
			{
				return value.startsWith("N");
			}
		});
		
		DataSet<Tuple2<String, Integer>> tokenized = filtered.flatMap(new Tokenizer());// tokanized = [(Nipun,1) (Noman,1)...]

		DataSet<Tuple2<String, Integer>> counts = tokenized.groupBy(new int[] { 0 }).sum(1);// groupBy(0)
		
		
		if (params.has("flatmapoutput")) {
			counts.writeAsCsv(params.get("flatmapoutput"), "\n", " ");

			env.execute("WordCount Example");
		}
	}

	public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
	
		public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
			
			String[] tokens = value.split(" ");
			
			for (String token : tokens) {
				if(token.length() > 0) {
					out.collect(new Tuple2<String, Integer>(token, 1));
				}
			}
			
			
		}
	}

}
