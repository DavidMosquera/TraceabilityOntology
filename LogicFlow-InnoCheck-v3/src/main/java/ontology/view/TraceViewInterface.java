package ontology.view;

public interface TraceViewInterface {
	
	void drawTargets();
	void drawSources();
	void drawCommands();
	void drawDetectedChanges();
	void resetLayout();
	
	void showNotTracedSources();
	void stopShowingNotTracedSources();
	void showNotTracedTargets();
	void stopShowingNotTracedTargets();
	void showCurrentTraces();
	void stopShowingCurrentTraces();
	void addTrace();
	void showSuggestedTargets();
	void stopShowingSuggestedTargets();
	void showSuggestedSources();
	void stopShowingSuggestedSources();
	void deleteTrace();
}
