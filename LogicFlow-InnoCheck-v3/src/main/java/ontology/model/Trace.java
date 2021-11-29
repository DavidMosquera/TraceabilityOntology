package ontology.model;

public class Trace {
	private Source hasSource;
	private Target hasTarget;
	private String individualUri;
	public String getHashKey() {
		return hasSource.getHashKey() + " " + this.individualUri + " " + hasTarget.getHashKey();
	}
	public Trace (String uri) {
		this.setIndividualUri(uri);
	}
	public Trace (String uri, Target target) {
		this.setIndividualUri(uri);
		this.hasTarget = target; 
	}
	public Trace (String uri, Source source) {
		this.setIndividualUri(uri);
		this.hasSource = source; 
	}
	public Trace (String uri, Source source, Target target) {
		this.setIndividualUri(uri);
		this.hasSource = source;
		this.hasTarget = target; 
	}
	public Source getHasSource() {
		return hasSource;
	}
	public void setHasSource(Source hasSource) {
		this.hasSource = hasSource;
	}
	public Target getHasTarget() {
		return hasTarget;
	}
	public void setHasTarget(Target hasTarget) {
		this.hasTarget = hasTarget;
	}
	public String getIndividualUri() {
		return individualUri;
	}
	public void setIndividualUri(String individualUri) {
		this.individualUri = individualUri;
	}
	public String toString() {
		return this.hasSource.toString() + " " + this.individualUri.split("#")[1] + " " + this.hasTarget.toString();
	}
}
