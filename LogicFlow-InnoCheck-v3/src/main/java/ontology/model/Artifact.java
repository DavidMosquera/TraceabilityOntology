package ontology.model;

public class Artifact {
	private String indidivudalUri;
	public enum TraceStatus {
		NOT_TRACED,
		TRACED,
		UNKNOWN
	}
	public enum SuggestionStatus {
		SUGGESTED,
		NOT_SUGGESTED
	}
	private TraceStatus status;
	private SuggestionStatus suggestionStatus;
	public Artifact (String uri) {
		this.status = TraceStatus.UNKNOWN;
		this.setSuggestionStatus(SuggestionStatus.NOT_SUGGESTED);
		this.setIndidivudalUri(uri);
	}
	public String getHashKey() {
		return this.indidivudalUri;
	}
	public String getIndidivudalUri() {
		return indidivudalUri;
	}
	public void setIndidivudalUri(String indidivudalUri) {
		this.indidivudalUri = indidivudalUri;
	}
	public String toString() {
		return this.indidivudalUri.split("#")[1];
	}
	public TraceStatus getStatus() {
		return status;
	}
	public void setStatus(TraceStatus status) {
		this.status = status;
	}
	public SuggestionStatus getSuggestionStatus() {
		return suggestionStatus;
	}
	public void setSuggestionStatus(SuggestionStatus suggestionStatus) {
		this.suggestionStatus = suggestionStatus;
	}
}