package ontology.model;

public class HasChange {
	private Artifact sourceCommand;
	private Artifact change;
	public HasChange(Artifact sourceCommand, Artifact change) {
		super();
		this.sourceCommand = sourceCommand;
		this.change = change;
	}
	public Artifact getSourceCommand() {
		return sourceCommand;
	}
	public void setSourceCommand(Artifact sourceCommand) {
		this.sourceCommand = sourceCommand;
	}
	public Artifact getChange() {
		return change;
	}
	public void setChange(Artifact change) {
		this.change = change;
	}
	public String getHashKey() {
		return this.sourceCommand + " " + this.change;
	}
}
