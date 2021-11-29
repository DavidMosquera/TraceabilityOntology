package ontology.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

import ontology.model.Artifact;
import ontology.model.HasChange;
import ontology.model.NextCommand;
import ontology.model.Source;
import ontology.model.Target;
import ontology.model.Trace;
import openllet.jena.PelletReasonerFactory;

public class TraceViewController {
	
	private OntModel ontoModel; 
	private OntModel openlletModel; 
	private int traceCount;
	private final String ONTO_PREFIX ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"
			+ "PREFIX ontotrace: <http://www.semanticweb.org/mosq/ontologies/2021/10/traceOntology1#>\r\n";
	
	private HashMap<String, Source> sources;  
	private HashMap<String, Target> targets;
	private HashMap<String, Trace> traces;
	private HashMap<String, Artifact> commands;
	private HashMap<String, Artifact> changes;
	private HashMap<String, NextCommand> nextCommands;
	private HashMap<String, HasChange> hasChange;
	
	
	public void addTrace(Source source, Target target) {
		this.traceCount += 1;
		String query = ONTO_PREFIX
				+ "INSERT DATA { \r\n"
				+ "ontotrace:"+traceCount+"-Trace ontotrace:hasSource ontotrace:"+source+".\r\n"
				+ "ontotrace:"+traceCount+"-Trace ontotrace:hasTarget ontotrace:"+target+"."
				+ "}";
		insertQuery(openlletModel, query);
	}
	
	public List<Source> whatAreThePossibleSourceArtifactsThatMayBeTraceToAnSpecificTarget(String target) {
		this.traceCount += 1;
		List<Source> suggestedSources = new ArrayList<Source>();
		String query = ONTO_PREFIX
				+ "INSERT DATA { \r\n"
				+ "ontotrace:" +traceCount+"-Trace ontotrace:hasTarget ontotrace:"+target+"."
				+ "}";
		insertQuery(openlletModel, query);
		query = ONTO_PREFIX 
				+ "SELECT ?traceType ?traceSource ?sourceClass ?possibleSource \r\n"
				+ "	WHERE { \r\n"
				+ "         ?traceType ^a ontotrace:"+traceCount+"-Trace."
				+ "			?traceType rdfs:subClassOf ontotrace:Trace."
				+ "			?traceSource rdfs:domain ?traceType."
				+ "			?traceSource rdfs:subPropertyOf ontotrace:hasSource."
				+ "			?traceSource rdfs:range ?sourceClass."
				+ "			?sourceClass rdfs:subClassOf ontotrace:Source."
				+ "			OPTIONAL {"
				+ "				?possibleSource a ?sourceClass."
				+ "			}"
				+ "			filter (?traceSource != owl:bottomObjectProperty && ?sourceClass != ontotrace:Source)"
				+ "}";
		executeQuery(openlletModel, query);
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		while (result.hasNext()) {
			qs = result.next();
			if(qs.get("possibleSource") != null) {
				suggestedSources.add(this.sources.get(qs.get("possibleSource").toString()));
				this.sources.get(qs.get("possibleSource").toString()).setSuggestionStatus(Artifact.SuggestionStatus.SUGGESTED);
			}
		}
		return suggestedSources;
	}
	
	public List<Target> whatAreThePossibleTargetArtifactsThatMayBeTraceToAnSpecificSource(String source) {
		this.traceCount += 1;
		List<Target> suggestedTargets = new ArrayList<Target>();
		String query = ONTO_PREFIX
				+ "INSERT DATA { \r\n"
				+ "ontotrace:" +traceCount+"-Trace ontotrace:hasSource ontotrace:"+source+"."
				+ "}";
		insertQuery(openlletModel, query);
		query = ONTO_PREFIX 
				+ "SELECT ?traceType ?traceTarget ?targetClass ?possibleTarget \r\n"
				+ "	WHERE { \r\n"
				+ "         ?traceType ^a ontotrace:"+traceCount+"-Trace."
				+ "			?traceType rdfs:subClassOf ontotrace:Trace."
				+ "			?traceTarget rdfs:domain ?traceType."
				+ "			?traceTarget rdfs:subPropertyOf ontotrace:hasTarget."
				+ "			?traceTarget rdfs:range ?targetClass."
				+ "			?targetClass rdfs:subClassOf ontotrace:Target."
				+ "			OPTIONAL {"
				+ "				?possibleTarget a ?targetClass."
				+ "			}"
				+ "			filter (?traceTarget != owl:bottomObjectProperty && ?targetClass != ontotrace:Target)"
				+ "}";
		executeQuery(openlletModel, query);
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		while (result.hasNext()) {
			qs = result.next();
			if(qs.get("possibleTarget") != null) {
				suggestedTargets.add(this.targets.get(qs.get("possibleTarget").toString()));
				this.targets.get(qs.get("possibleTarget").toString()).setSuggestionStatus(Artifact.SuggestionStatus.SUGGESTED);
			}
		}
		return suggestedTargets;
	}
	
	public void whatAreTheTracesBetweenMyArtifacts() {
		this.traces = new HashMap<String, Trace>();
		for (Trace trace : getAllTraces()) {
			if(!this.traces.containsKey(trace.getHashKey())) {
				this.traces.put(trace.getHashKey(), trace);
			}
		}
	}
	
	public void isThereAnySourceArtifactThatDoesNotHaveALinkToATargetArtifact() {
		for(String sourceKey : this.sources.keySet()) {
			this.sources.get(sourceKey).setStatus(Artifact.TraceStatus.TRACED);
		}
		String query = ONTO_PREFIX
				+ "SELECT ?source  (count(?trace) as ?count) WHERE{\r\n"
				+ "	?class rdfs:subClassOf* ontotrace:Source.\r\n"
				+ "	?source a ?class.\r\n"
				+ "	OPTIONAL {\r\n"
				+ "		?trace a ontotrace:Trace. \r\n"
				+ "		?trace ontotrace:hasSource ?source."
				+ "		?trace ontotrace:hasTarget ?target.\r\n"
				+ "	}\r\n"
				+ "}GROUP BY ?source HAVING (count(?trace) = 0)";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		while (result.hasNext()) {
			qs = result.next();
			this.sources.get(qs.get("source").toString()).setStatus(Artifact.TraceStatus.NOT_TRACED);
		}
	}
	
	public void isThereAnyTargetArtifactThatDoesNotHaveALinkToASourceArtifact() {
		for(String targetKey : this.targets.keySet()) {
			this.targets.get(targetKey).setStatus(Artifact.TraceStatus.TRACED);
		}
		String query = ONTO_PREFIX
				+ "SELECT ?target (count(?trace) as ?count) WHERE{\r\n"
				+ "	?class rdfs:subClassOf* ontotrace:Target.\r\n"
				+ "	?target a ?class.	\r\n"
				+ "	OPTIONAL {\r\n"
				+ "		?trace a ontotrace:Trace.\r\n"
				+ "		?trace ontotrace:hasTarget ?target.\r\n"
				+ "		?trace ontotrace:hasSource ?source.	"
				+ "}\r\n"
				+ "}GROUP BY ?target HAVING (?count = 0)";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		while (result.hasNext()) {
			qs = result.next();
			this.targets.get(qs.get("target").toString()).setStatus(Artifact.TraceStatus.NOT_TRACED);
		}
	}
	
	
	public void getAllChanges() {
		this.changes = new HashMap<String, Artifact>();
		this.hasChange = new HashMap<String, HasChange>();
		String query = ONTO_PREFIX
				+ "SELECT ?command ?hasChange {\r\n"
				+ "	?command ontotrace:HasChange ?hasChange.\r\n"
				+ "}";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		HasChange hasChange; 
		Artifact artifact;
		while (result.hasNext()) {
			qs = result.next();
			if(!this.changes.containsKey(qs.get("hasChange").toString())) {
				artifact = new Artifact(qs.get("hasChange").toString());
				this.changes.put(artifact.getHashKey(), artifact);
			}
			hasChange = new HasChange(this.commands.get(qs.get("command").toString()), this.changes.get(qs.get("hasChange").toString()));
			this.hasChange.put(hasChange.getHashKey(), hasChange);
		}
	}
	
	public void getAllCommands() {
		this.commands = new HashMap<String, Artifact>();
		this.nextCommands = new HashMap<String, NextCommand>();
		String query = ONTO_PREFIX
				+ "SELECT ?command ?nextCommand {\r\n"
				+ "	?command ontotrace:HasNextCommand ?nextCommand.\r\n"
				+ "}";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		NextCommand nextCommand; 
		Artifact artifact;
		while (result.hasNext()) {
			qs = result.next();
			if(!this.commands.containsKey(qs.get("command").toString())) {
				artifact = new Artifact(qs.get("command").toString());
				this.commands.put(artifact.getHashKey(), artifact);
			}
			if(!this.commands.containsKey(qs.get("nextCommand").toString())) {
				artifact = new Artifact(qs.get("nextCommand").toString());
				this.commands.put(artifact.getHashKey(), artifact);
			}
			nextCommand = new NextCommand(this.commands.get(qs.get("command").toString()), this.commands.get(qs.get("nextCommand").toString()));
			nextCommands.put(nextCommand.getHashKey(), nextCommand);
		}
	}
	
	public TraceViewController() {
		this.traceCount = 100;
		//READING ONTOLOGY FROM LOCAL FILE
		ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		this.targets = new HashMap<String, Target>();
		this.sources = new HashMap<String, Source>();
		this.traces = new HashMap<String, Trace>();
		try {
			OntDocumentManager dm = ontoModel.getDocumentManager();
			dm.addAltEntry("http://www.semanticweb.org/mosq/ontologies/2021/10/traceOntology1", 
						"file:"+this.getClass().getResource("/ontology/v18-12-2021.owl").getPath());
			ontoModel.read("http://www.semanticweb.org/mosq/ontologies/2021/10/traceOntology1");
			openlletModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			openlletModel.add(ontoModel);
			//INSTATNTIATE TARGETS SOURCES AND TRACES
			for (Source source : getAllSources()) {
				this.sources.put(source.getHashKey(), source);
			}
			for (Target target : getAllTargets()) {
				this.targets.put(target.getHashKey(), target);
			}
			for (Trace trace : getAllTraces()) {
				this.traces.put(trace.getHashKey(), trace);
			}
			
			//DOMAIN SPECIFIC METHODS
			getAllCommands();
			getAllChanges();
		}catch (JenaException je) {
			je.printStackTrace();
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public List<Trace> getAllTraces(){
		List<Trace> traces = new ArrayList<Trace>();
		String query = ONTO_PREFIX 
				+ "SELECT ?source ?trace ?target\r\n"
				+ "	WHERE {?trace a ontotrace:Trace. "
				+ "		   ?trace ontotrace:hasSource ?source."
				+ "		   ?trace ontotrace:hasTarget ?target.}";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		Trace trace;
		while (result.hasNext()) {
			qs = result.next();
			trace = new Trace(qs.get("trace").toString(), (Source) this.sources.get(qs.get("source").toString()), (Target) this.targets.get(qs.get("target").toString()));
			traces.add(trace);
		}
		return traces;
	}
	
	
	public List<Target> getAllTargets() {
		List<Target> targets = new ArrayList<Target>();
		String query = ONTO_PREFIX 
				+ "SELECT ?target\r\n"
				+ "	WHERE { ?target a ontotrace:Target}";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		while (result.hasNext()) {
			qs = result.next();
			targets.add(new Target(qs.get("target").toString()));
		}
		return targets;
	}
	
	public List<Source> getAllSources() {
		List<Source> sources = new ArrayList<Source>();
		String query = ONTO_PREFIX 
				+ "SELECT ?source\r\n"
				+ "	WHERE { ?source a ontotrace:Source}";
		ResultSet result = executeQuery(openlletModel, query);
		QuerySolution qs;
		while (result.hasNext()) {
			qs = result.next();
			sources.add(new Source(qs.get("source").toString()));
		}
		return sources;
	}

	public ResultSet executeQuery (OntModel model, String query) {
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet result = qe.execSelect();
		//qe.close();
		return result;
	}
	
	public void insertQuery (OntModel model, String query) {
		UpdateRequest qe = UpdateFactory.create();
		qe.add(query);
		UpdateAction.execute(qe, model);
	}
	
	public static void main(String[] agrs) {
		TraceViewController a = new TraceViewController();
		a.isThereAnyTargetArtifactThatDoesNotHaveALinkToASourceArtifact();
		a.isThereAnySourceArtifactThatDoesNotHaveALinkToATargetArtifact();
		a.whatAreTheTracesBetweenMyArtifacts();
		a.whatAreThePossibleTargetArtifactsThatMayBeTraceToAnSpecificSource("48-Click");
	}

	public OntModel getOntoModel() {
		return ontoModel;
	}

	public void setOntoModel(OntModel ontoModel) {
		this.ontoModel = ontoModel;
	}

	public OntModel getOpenlletModel() {
		return openlletModel;
	}

	public void setOpenlletModel(OntModel openlletModel) {
		this.openlletModel = openlletModel;
	}

	public int getTraceCount() {
		return traceCount;
	}

	public void setTraceCount(int traceCount) {
		this.traceCount = traceCount;
	}

	public HashMap<String, Source> getSources() {
		return sources;
	}

	public void setSources(HashMap<String, Source> sources) {
		this.sources = sources;
	}

	public HashMap<String, Target> getTargets() {
		return targets;
	}

	public void setTargets(HashMap<String, Target> targets) {
		this.targets = targets;
	}

	public HashMap<String, Trace> getTraces() {
		return traces;
	}

	public void setTraces(HashMap<String, Trace> traces) {
		this.traces = traces;
	}

	public HashMap<String, NextCommand> getNextCommands() {
		return nextCommands;
	}

	public void setNextCommands(HashMap<String, NextCommand> nextCommands) {
		this.nextCommands = nextCommands;
	}

	public String getONTO_PREFIX() {
		return ONTO_PREFIX;
	}

	public HashMap<String, Artifact> getCommands() {
		return commands;
	}

	public void setCommands(HashMap<String, Artifact> commands) {
		this.commands = commands;
	}

	public HashMap<String, Artifact> getChanges() {
		return changes;
	}

	public void setChanges(HashMap<String, Artifact> changes) {
		this.changes = changes;
	}

	public HashMap<String, HasChange> getHasChange() {
		return hasChange;
	}

	public void setHasChange(HashMap<String, HasChange> hasChange) {
		this.hasChange = hasChange;
	}
	
}
