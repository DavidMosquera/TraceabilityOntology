package ontology.model;

public class NextCommand {
	private Artifact sourceCommand;
	private Artifact targetCommand;
	public NextCommand(Artifact sourceCommand, Artifact targetCommand) {
		super();
		this.sourceCommand = sourceCommand;
		this.targetCommand = targetCommand;
	}
	public Artifact getSourceCommand() {
		return sourceCommand;
	}
	public void setSourceCommand(Artifact sourceCommand) {
		this.sourceCommand = sourceCommand;
	}
	public Artifact getTargetCommand() {
		return targetCommand;
	}
	public void setTargetCommand(Artifact targetCommand) {
		this.targetCommand = targetCommand;
	}
	public String getHashKey() {
		return this.sourceCommand + " " + this.targetCommand;
	}
}
